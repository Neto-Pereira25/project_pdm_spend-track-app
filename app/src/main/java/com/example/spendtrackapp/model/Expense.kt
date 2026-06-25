package com.example.spendtrackapp.model

data class Expense(
    val id: String,
    val description: String,
    val amount: Double,
    val category: String,
    val lat: Double? = null,
    val lng: Double? = null

)

