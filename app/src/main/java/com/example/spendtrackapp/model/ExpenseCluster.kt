package com.example.spendtrackapp.model

data class ExpenseCluster(
    val key: String,
    val lat: Double,
    val lng: Double,
    val averageAmount: Double,
    val totalAmount: Double,
    val count: Int,
    val mainCategory: String
)
