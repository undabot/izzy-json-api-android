package com.undabot.izzy.retrofit

import okhttp3.ResponseBody
import retrofit2.Converter

class IzzyInvalidJsonResponseBodyConverter : Converter<ResponseBody, Any?> {

    override fun convert(value: ResponseBody): Any? {
        return null
    }
}