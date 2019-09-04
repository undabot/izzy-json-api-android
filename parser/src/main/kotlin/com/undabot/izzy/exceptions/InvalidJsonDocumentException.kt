package com.undabot.izzy.exceptions

class InvalidJsonDocumentException(
    message: String = "Provided JSON file is not in proper format!"
) : IllegalArgumentException(message)