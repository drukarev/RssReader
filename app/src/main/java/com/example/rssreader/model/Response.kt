package com.example.rssreader.model

sealed class Response<out T : Any> {

    data class Result<out T : Any>(val value: T) : Response<T>()
    data class Fail(val value: Failure) : Response<Nothing>()
}

sealed class Failure {
    object Unknown : Failure()
    object RssParsingFailure : Failure()
}
