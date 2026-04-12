package com.example.plyground.ui.common

sealed class ScreenState<out T> {
    data object Loading : ScreenState<Nothing>()
    data class Success<T>(val data: T) : ScreenState<T>()
    data class Error(val message: String, val retryable: Boolean = true) : ScreenState<Nothing>()
    data object Empty : ScreenState<Nothing>()
}

fun <T> ScreenState<T>.isLoading() = this is ScreenState.Loading
