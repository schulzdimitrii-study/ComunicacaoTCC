package com.pedroaba.tccmobile.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme
import com.pedroaba.tccmobile.ui.components.AppCaptionMuted
import com.pedroaba.tccmobile.ui.components.AppLabelStrong
import com.pedroaba.tccmobile.ui.components.AppOverline

data class TabItem(
    val name: String,
    val label: String,
    val icon: ImageVector
)

val TAB_ITEMS = listOf(
    TabItem("home", "Home", Icons.Filled.Home),
    TabItem("rank", "Ranking", Icons.Filled.EmojiEvents),
    TabItem("perfil", "Perfil", Icons.Filled.Person)
)

@Composable
fun FloatingTabBar(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(AppTheme.colors.card.copy(alpha = 0.94f))
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TAB_ITEMS.forEach { tab ->
                val isSelected = currentTab == tab.name
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(22.dp))
                                .background(AppTheme.colors.primary)
                                .clickable { onTabSelected(tab.name) }
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(16.dp),
                                tint = AppTheme.colors.primaryForeground
                            )
                            AppLabelStrong(
                                text = tab.label,
                                modifier = Modifier
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clickable { onTabSelected(tab.name) }
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(14.dp),
                                tint = AppTheme.colors.textSecondary
                            )
                            AppOverline(
                                text = tab.label,
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
    }
}
