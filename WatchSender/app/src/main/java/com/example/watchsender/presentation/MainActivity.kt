package com.example.datalayertest

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private var progressState by mutableStateOf(GameProgressState())
    private var telemetryRunning by mutableStateOf(false)
    private var telemetryStatus by mutableStateOf("")

    private val stateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            syncUiFromStoredState()
        }
    }

    private val requestBasePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val baseGranted = requiredBasePermissions().all { permissions[it] == true || hasPermission(it) }
        if (!baseGranted) {
            telemetryStatus = "Permissoes necessarias"
            return@registerForActivityResult
        }

        if (needsBackgroundHealthPermission() && !hasPermission(BACKGROUND_HEALTH_PERMISSION)) {
            requestBackgroundHealthPermission.launch(BACKGROUND_HEALTH_PERMISSION)
        } else {
            iniciarTelemetria()
        }
    }

    private val requestBackgroundHealthPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            iniciarTelemetria()
        } else {
            telemetryStatus = "Ative segundo plano nas configuracoes"
            syncUiFromStoredState()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        syncUiFromStoredState()
        setContent {
            WatchGameDashboard(
                progressState = progressState,
                telemetryRunning = telemetryRunning,
                telemetryStatus = telemetryStatus,
                onToggleTelemetry = {
                    if (TelemetrySessionService.isTelemetryRunning(this@MainActivity)) {
                        pararTelemetria()
                    } else if (hasAllRequiredPermissions()) {
                        iniciarTelemetria()
                    } else {
                        requestBasePermissions.launch(requiredBasePermissions())
                    }
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val telemetryFilter = IntentFilter(TelemetrySessionService.ACTION_TELEMETRY_STATE_CHANGED)
        val progressFilter = IntentFilter(GameProgressStore.ACTION_GAME_PROGRESS_CHANGED)
        registerReceiver(stateReceiver, telemetryFilter, RECEIVER_NOT_EXPORTED)
        registerReceiver(stateReceiver, progressFilter, RECEIVER_NOT_EXPORTED)
        syncUiFromStoredState()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(stateReceiver)
    }

    private fun syncUiFromStoredState() {
        telemetryRunning = TelemetrySessionService.isTelemetryRunning(this)
        telemetryStatus = TelemetrySessionService.loadTelemetryStatus(this).ifBlank {
            if (telemetryRunning) "Telemetria ativa" else "Toque para iniciar"
        }
        progressState = GameProgressStore.load(this)
    }

    private fun hasAllRequiredPermissions(): Boolean {
        val baseGranted = requiredBasePermissions().all(::hasPermission)
        val backgroundGranted = !needsBackgroundHealthPermission() || hasPermission(BACKGROUND_HEALTH_PERMISSION)
        return baseGranted && backgroundGranted
    }

    private fun requiredBasePermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            if (Build.VERSION.SDK_INT >= 36) HEART_RATE_PERMISSION else Manifest.permission.BODY_SENSORS
        )
    }

    private fun needsBackgroundHealthPermission(): Boolean = Build.VERSION.SDK_INT >= 36

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun iniciarTelemetria() {
        ContextCompat.startForegroundService(
            this,
            TelemetrySessionService.createStartIntent(this)
        )
        telemetryStatus = "Iniciando sensores..."
        syncUiFromStoredState()
    }

    private fun pararTelemetria() {
        startService(TelemetrySessionService.createStopIntent(this))
        telemetryStatus = "Parando telemetria..."
        syncUiFromStoredState()
    }

    companion object {
        private const val HEART_RATE_PERMISSION = "android.permission.health.READ_HEART_RATE"
        private const val BACKGROUND_HEALTH_PERMISSION =
            "android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND"
    }
}

@Composable
private fun WatchGameDashboard(
    progressState: GameProgressState,
    telemetryRunning: Boolean,
    telemetryStatus: String,
    onToggleTelemetry: () -> Unit
) {
    val scrollState = rememberScrollState()
    val animatedProgress by animateFloatAsState(
        targetValue = progressState.progress.coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(durationMillis = 700),
        label = "progress"
    )
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        AmbientField(pulse = pulse)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderPill(
                text = if (progressState.isRunning) "HORDA ATIVA" else "AGUARDANDO JOGO",
                alert = progressState.risk >= 0.7
            )
            Spacer(Modifier.height(6.dp))
            ProgressDial(
                progress = animatedProgress,
                bpm = progressState.bpm,
                risk = progressState.risk,
                pulse = pulse
            )
            Spacer(Modifier.height(8.dp))
            MetricStrip(progressState = progressState)
            Spacer(Modifier.height(8.dp))
            SignalBar(label = "Horda", value = progressState.hordePressure, color = Crimson)
            Spacer(Modifier.height(5.dp))
            SignalBar(label = "Performance", value = progressState.performanceScore, color = Success)
            Spacer(Modifier.height(8.dp))
            StatusLine(
                telemetryRunning = telemetryRunning,
                telemetryStatus = telemetryStatus,
                progressState = progressState
            )
            Spacer(Modifier.height(8.dp))
            ActionButton(
                text = if (telemetryRunning) "Parar" else "Iniciar",
                running = telemetryRunning,
                onClick = onToggleTelemetry
            )
        }
    }
}

@Composable
private fun AmbientField(pulse: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Crimson.copy(alpha = 0.18f * pulse), Color.Transparent),
                center = Offset(size.width * 0.5f, size.height * 0.18f),
                radius = size.minDimension * 0.72f
            )
        )
        drawCircle(
            color = DeepRed.copy(alpha = 0.28f),
            radius = size.minDimension * 0.64f,
            center = Offset(size.width * 0.5f, size.height * 1.05f)
        )
    }
}

@Composable
private fun HeaderPill(text: String, alert: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (alert) AlertFill else Card)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        LabelText(
            text = text,
            color = if (alert) Rose else Muted,
            size = 8,
            weight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProgressDial(progress: Float, bpm: Int?, risk: Double, pulse: Float) {
    val ringColor = when {
        risk >= 0.72 -> Crimson
        risk >= 0.42 -> Amber
        else -> Success
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(116.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
            val arcSize = Size(size.width - 14.dp.toPx(), size.height - 14.dp.toPx())
            val arcOffset = Offset(7.dp.toPx(), 7.dp.toPx())
            drawArc(
                color = Border,
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = stroke
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(DeepRed, ringColor, Rose)),
                startAngle = -215f,
                sweepAngle = 250f * progress,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = stroke
            )
            drawCircle(
                color = ringColor.copy(alpha = 0.08f * pulse),
                radius = size.minDimension * 0.38f
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LabelText(text = bpm?.toString() ?: "--", color = TextPrimary, size = 30, weight = FontWeight.Black)
            LabelText(text = "BPM", color = Rose, size = 9, weight = FontWeight.Bold)
            LabelText(text = "${(progress * 100).roundToInt()}%", color = Muted, size = 10, weight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MetricStrip(progressState: GameProgressState) {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        CompactMetric(value = formatDistance(progressState.distanceMeters), label = "dist")
        Spacer(Modifier.width(6.dp))
        CompactMetric(value = "${(progressState.risk * 100).roundToInt()}%", label = "risco")
        Spacer(Modifier.width(6.dp))
        CompactMetric(value = "${progressState.progressPercent}%", label = "meta")
    }
}

@Composable
private fun CompactMetric(value: String, label: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Card)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LabelText(text = value, color = TextPrimary, size = 12, weight = FontWeight.Bold)
        LabelText(text = label.uppercase(Locale.ROOT), color = Muted, size = 7, weight = FontWeight.SemiBold)
    }
}

@Composable
private fun SignalBar(label: String, value: Double, color: Color) {
    val animatedValue by animateFloatAsState(
        targetValue = value.coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(durationMillis = 650),
        label = label
    )
    Column(modifier = Modifier.fillMaxWidth(0.82f)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LabelText(text = label, color = Muted, size = 8, weight = FontWeight.SemiBold)
            LabelText(text = "${(animatedValue * 100).roundToInt()}%", color = color, size = 8, weight = FontWeight.Bold)
        }
        Spacer(Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Border)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedValue)
                    .height(5.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun StatusLine(
    telemetryRunning: Boolean,
    telemetryStatus: String,
    progressState: GameProgressState
) {
    val synced = progressState.updatedAtMs > 0L
    val text = when {
        !telemetryRunning -> "Sensores pausados"
        synced -> "Celular sincronizado"
        else -> telemetryStatus
    }
    LabelText(
        text = text,
        color = if (synced && telemetryRunning) Success else Muted,
        size = 9,
        weight = FontWeight.SemiBold,
        maxLines = 1
    )
}

@Composable
private fun ActionButton(text: String, running: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (running) AlertFill else DeepRed)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        LabelText(text = text, color = TextPrimary, size = 12, weight = FontWeight.Bold)
    }
}

@Composable
private fun LabelText(
    text: String,
    color: Color,
    size: Int,
    weight: FontWeight,
    maxLines: Int = 1
) {
    BasicText(
        text = text,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            color = color,
            fontSize = size.sp,
            fontWeight = weight,
            textAlign = TextAlign.Center
        )
    )
}

private fun formatDistance(meters: Double): String {
    return if (meters >= 1000.0) {
        String.format(Locale.US, "%.1fkm", meters / 1000.0)
    } else {
        "${meters.roundToInt()}m"
    }
}

private val Background = Color(0xFF09090B)
private val Card = Color(0xFF111113)
private val Border = Color(0xFF27272A)
private val Crimson = Color(0xFFDC2626)
private val DeepRed = Color(0xFF7F1D1D)
private val Rose = Color(0xFFFCA5A5)
private val TextPrimary = Color(0xFFFFF1F2)
private val Muted = Color(0xFFA1A1AA)
private val AlertFill = Color(0xFF4C1D1D)
private val Success = Color(0xFF86EFAC)
private val Amber = Color(0xFFFACC15)
