package com.apero.kmm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform