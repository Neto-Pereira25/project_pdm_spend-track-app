package com.example.spendtrackapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.spendtrackapp.model.Expense

class MainViewModel : ViewModel() {

    private val _expenses = mutableStateListOf(
        Expense(
            id = "1",
            description = "Hambúrguer",
            amount = 18.50,
            category = "Alimentação"
        ),
        Expense(
            id = "2",
            description = "Uber",
            amount = 12.00,
            category = "Transporte"
        ),
        Expense(
            id = "3",
            description = "Café",
            amount = 6.00,
            category = "Alimentação"
        )
    )

    val expenses: List<Expense>
        get() = _expenses.toList()

    fun add(description: String, amount: Double, category: String) {
        _expenses.add(
            Expense(
                id = System.currentTimeMillis().toString(),
                description = description,
                amount = amount,
                category = category
            )
        )
    }

    fun remove(expense: Expense) {
        _expenses.remove(expense)
    }

    fun totalSpent(): Double {
        return _expenses.sumOf { it.amount }
    }

    fun totalItems(): Int {
        return _expenses.size
    }
}
