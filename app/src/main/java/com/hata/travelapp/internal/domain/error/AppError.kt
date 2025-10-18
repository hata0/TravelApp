package com.hata.travelapp.internal.domain.error

class AppError(
    val code: ErrorCode,
    cause: Throwable? = null
) : Exception(cause)