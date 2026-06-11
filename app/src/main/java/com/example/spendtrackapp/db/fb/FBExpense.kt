package com.example.spendtrackapp.db.fb

import com.example.spendtrackapp.model.Expense

class FBExpense {
    var id: String? = null
    var description: String? = null
    var amount: Double? = null
    var category: String? = null

    fun toExpense(): Expense {
        return Expense(
            id = id ?: "",
            description = description ?: "",
            amount = amount ?: 0.0,
            category = category ?: ""
        )
    }
}

fun Expense.toFBExpense(): FBExpense {
    val fbExpense = FBExpense()
    fbExpense.id = this.id
    fbExpense.description = this.description
    fbExpense.amount = this.amount
    fbExpense.category = this.category
    return fbExpense
}
