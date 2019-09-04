package com.undabot.izzy.exceptions

class TypeNotRegisteredException(
    type: String,
    message: String = "Type $type is not registered in the configuration." +
            " Please register the type."
) :
        RuntimeException(message)