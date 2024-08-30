package com.example.finance.entities

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TransactionEntity(
    val id: Int,
    val type: TransactionType,
    val detail: String,
    val value: Double,
    val date: Date,
)

enum class TransactionType(val type: String) {
    CREDIT("Crédito"), DEBIT("Débito");

    companion object {
        fun fromType(type: String): TransactionType {
            return entries.first() { it.type == type }
        }
    }
}

fun Date.fromDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(this)
}

fun String.toDate(): Date? {
    val format = when {
        this.matches(Regex("""\w{3} \w{3} \d{2} \d{2}:\d{2}:\d{2} GMT[+-]\d{2}:\d{2} \d{4}""")) ->
            "EEE MMM dd HH:mm:ss zzz yyyy"

        this.matches(Regex("""\d{2}/\d{2}/\d{4}""")) ->
            "dd/MM/yyyy"

        else ->
            return null
    }

    return try {
        SimpleDateFormat(format, Locale.ENGLISH).parse(this)
    } catch (e: ParseException) {
        null
    }
}

