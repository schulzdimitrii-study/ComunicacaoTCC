package com.pedroaba.tccmobile.game.debug

object GameDebugLogger {
    fun log(tag: String, vararg fields: Pair<String, Any?>) {
        val payload = fields.joinToString(" ") { (key, value) -> "$key=$value" }
        println("[TCCGame][$tag] $payload")
    }
}
