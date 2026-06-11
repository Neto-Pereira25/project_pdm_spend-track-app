package com.example.spendtrackapp.model

data class Expense(
    val id: String,
    val description: String,
    val amount: Double,
    val category: String
)

