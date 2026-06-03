package com.pedroaba.tccmobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform