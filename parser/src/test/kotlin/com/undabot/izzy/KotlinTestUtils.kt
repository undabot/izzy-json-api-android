package com.undabot.izzy

import org.junit.Assert.assertEquals
import java.io.File

fun `Given`(unit: () -> Unit) = unit.invoke()
fun `When`(unit: () -> Unit) = unit.invoke()
fun `Then`(unit: () -> Unit) = unit.invoke()
infix fun Any?.`And`(unit: () -> Unit) = unit.invoke()
infix fun Any?.equals(expected: kotlin.Any?) = assertEquals(expected, this)
fun String.asResource(): String = File("src/test/resources/$this").readText()
