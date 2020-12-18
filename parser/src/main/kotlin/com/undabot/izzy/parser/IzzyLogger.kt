package com.undabot.izzy.parser

import com.undabot.izzy.parser.IzzyLogger.Level.ALL
import com.undabot.izzy.parser.IzzyLogger.Level.ERRORS
import com.undabot.izzy.parser.IzzyLogger.Level.INFO
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Objects
import java.util.logging.Logger

const val TAG = "Izzy"
private const val STRING_WRITER_SIZE = 256

interface IzzyLogger {
    fun log(message: String?, throwable: Throwable)
    fun log(message: String)
    fun lastElementSearchedIn(key: String, jsonNode: String)
    var level: Level

    companion object {
        val DEFAULT = object : IzzyLogger {
            private val globalLogger = Logger.getGlobal()
            override var level: Level = Level.NONE
            private var lastKnownProcessAction: String = ""

            override fun lastElementSearchedIn(key: String, jsonNode: String) {
                if (level == ERRORS || level == ALL || level == INFO) {
                    lastKnownProcessAction = "===>find key:$key<=== in $jsonNode"
                }
            }

            override fun log(message: String?, throwable: Throwable) {
                if (level == ERRORS || level == ALL) {
                    val formattedLog =
                        StringBuilder().append("$TAG-ERROR").append(message).append(
                            getStackTraceString(
                                findCauseUsingPlainJava(throwable) ?: throwable
                            )
                        ).append("----->LAST KNOWN ACTION: $lastKnownProcessAction").toString()
                    globalLogger.log(java.util.logging.Level.WARNING, formattedLog)
                }
            }

            override fun log(message: String) {
                if (level == ERRORS || level == ALL) {
                    val formattedLog =
                        StringBuilder().appendln("$TAG-INFO").appendln(message).toString()
                    globalLogger.log(java.util.logging.Level.INFO, formattedLog)
                }
            }
        }
    }

    enum class Level {
        NONE, INFO, ERRORS, ALL
    }
}

private fun getStackTraceString(t: Throwable): String? {
    val sw = StringWriter(STRING_WRITER_SIZE)
    val pw = PrintWriter(sw, false)
    t.printStackTrace(pw)
    pw.flush()
    return sw.toString()
}

fun findCauseUsingPlainJava(throwable: Throwable?): Throwable? {
    Objects.requireNonNull(throwable)
    var rootCause = throwable
    while (rootCause!!.cause != null) {
        rootCause = rootCause.cause
    }
    return rootCause
}
