package com.example.spendtrackapp.db.fb

import com.example.spendtrackapp.model.Expense


class FBExpense {
    var id: String? = null
    var description: String? = null
    var amount: Double? = null
    var category: String? = null
    var lat: Double? = null
    var lng: Double? = null

    fun toExpense(): Expense {
        return Expense(
            id = id ?: "",
            description = description ?: "",
            amount = amount ?: 0.0,
            category = category ?: "",
            lat = lat,
            lng = lng
        )
    }
}

fun Expense.toFBExpense(): FBExpense {
    val fbExpense = FBExpense()
    fbExpense.id = this.id
    fbExpense.description = this.description
    fbExpense.amount = this.amount
    fbExpense.category = this.category
    fbExpense.lat = this.lat
    fbExpense.lng = this.lng
    return fbExpense
}

