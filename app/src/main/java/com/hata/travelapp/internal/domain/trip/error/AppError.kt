package com.hata.travelapp.internal.domain.trip.error

class AppError(
    val code: ErrorCode,
    cause: Throwable? = null
) : Exception(cause)