package com.undabot.izzy.parser

import com.undabot.izzy.exceptions.InvalidJsonDocumentException
import com.undabot.izzy.models.Errors

class DeserializeErrors(private val deserializeError: DeserializeError = DeserializeError()) {

    fun from(jsonElements: JsonElements): Errors {
        return when (true) {
            isNull(jsonElements) -> throw InvalidJsonDocumentException("Errors should not be null.")
            isNotArray(jsonElements) -> throw InvalidJsonDocumentException("Errors MUST be an array!")
            else -> parseErrorsFrom(jsonElements)
        }
    }

    private fun parseErrorsFrom(jsonElements: JsonElements): Errors {
        val errorList = jsonElements.jsonElementsArray(ERRORS).map { deserializeError.from(it) }
        return Errors(errorList)
    }

    private fun isNotArray(jsonElements: JsonElements) = !jsonElements.jsonElement(ERRORS).isArray()

    private fun isNull(jsonElements: JsonElements) = !jsonElements.hasNonNull(ERRORS)
}