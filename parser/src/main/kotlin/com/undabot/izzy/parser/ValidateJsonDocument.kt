package com.undabot.izzy.parser

import com.undabot.izzy.exceptions.InvalidJsonDocumentException

class ValidateJsonDocument {

    fun from(jsonElements: JsonElements) {
        when (true) {
            rootIsNotObjectIn(jsonElements) -> throwRootNotObjectException()
            missingTopLevelMembersIn(jsonElements) -> throwMissingTopLevelMembersException()
            containsBothDataAndErrorsIn(jsonElements) -> throwBothDataAndErrorsIncludedException()
            containsIncludedWithoutDataIn(jsonElements) -> throwIncludedProvidedWithoutDataException()
        }
    }

    private fun rootIsNotObjectIn(jsonElements: JsonElements) = !jsonElements.isObject()

    private fun missingTopLevelMembersIn(jsonElements: JsonElements) =
            !(jsonElements.has(META) or jsonElements.has(DATA) or jsonElements.has(ERRORS))

    private fun containsBothDataAndErrorsIn(jsonElements: JsonElements) =
            jsonElements.has(DATA) and jsonElements.has(ERRORS)

    private fun containsIncludedWithoutDataIn(jsonElements: JsonElements) =
            jsonElements.has(INCLUDED) and !jsonElements.hasNonNull(DATA)

    private fun throwIncludedProvidedWithoutDataException() =
            throwExceptionWith("The 'included' member MUST NOT be present when 'data' is not provided.")

    private fun throwBothDataAndErrorsIncludedException() =
            throwExceptionWith("The members 'data' and 'errors' MUST NOT coexist in the same document.")

    private fun throwMissingTopLevelMembersException() =
            throwExceptionWith("A document MUST contain at least one of the following top-level members: 'data', 'errors', 'meta'")

    private fun throwRootNotObjectException() =
            throwExceptionWith("Root must be JSON object.")

    private fun throwExceptionWith(message: String) {
        throw InvalidJsonDocumentException(message)
    }
}