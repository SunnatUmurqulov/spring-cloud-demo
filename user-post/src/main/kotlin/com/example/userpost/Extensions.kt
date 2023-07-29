package com.example.userpost

fun Boolean.runIfFalse(func: () -> Unit) {
    if (!this) func()
}