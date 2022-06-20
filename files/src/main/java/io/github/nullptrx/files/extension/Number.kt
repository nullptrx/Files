package io.github.nullptrx.files.extension

fun Long.hasBits(bits: Long): Boolean = this and bits == bits

infix fun Long.andInv(other: Long): Long = this and other.inv()

fun Int.hasBits(bits: Int): Boolean = this and bits == bits

infix fun Int.andInv(other: Int): Int = this and other.inv()