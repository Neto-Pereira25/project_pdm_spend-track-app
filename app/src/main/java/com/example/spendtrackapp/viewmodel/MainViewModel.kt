package com.example.spendtrackapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spendtrackapp.db.fb.FBDatabase
import com.example.spendtrackapp.db.fb.FBExpense
import com.example.spendtrackapp.db.fb.toFBExpense
import com.example.spendtrackapp.model.Expense
import android.util.Log


class MainViewModel(
    private val db: FBDatabase
) : ViewModel(), FBDatabase.Listener {

    companion object {
        private const val TAG = "SpendTrackExpense"
    }

    private val _expenses = mutableStateListOf<Expense>()

    val expenses: List<Expense>
        get() = _expenses.toList()

    init {
        db.setListener(this)
    }

    fun add(
        description: String,
        amount: Double,
        category: String,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        val expense = Expense(
            id = System.currentTimeMillis().toString(),
            description = description,
            amount = amount,
            category = category
        )

        Log.d(TAG, "ViewModel created expense: $expense")

        db.add(
            expense = expense.toFBExpense(),
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun remove(
        expense: Expense,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        db.remove(
            expense = expense.toFBExpense(),
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun totalSpent(): Double {
        return _expenses.sumOf { it.amount }
    }

    fun totalItems(): Int {
        return _expenses.size
    }

    override fun onExpenseAdded(expense: FBExpense) {
        val converted = expense.toExpense()
        Log.d(TAG, "Snapshot added expense received: $converted")
        if (_expenses.none { it.id == converted.id }) {
            _expenses.add(converted)
        }
    }

    override fun onExpenseUpdated(expense: FBExpense) {
        val converted = expense.toExpense()
        Log.d(TAG, "Snapshot updated expense received: $converted")
        val index = _expenses.indexOfFirst { it.id == converted.id }
        if (index != -1) {
            _expenses[index] = converted
        }
    }

    override fun onExpenseRemoved(expense: FBExpense) {
        val converted = expense.toExpense()
        Log.d(TAG, "Snapshot removed expense received: $converted")
        _expenses.removeAll { it.id == converted.id }
    }

    override fun onUserSignOut() {
        Log.d(TAG, "Auth sign-out detected, clearing local expenses list")
        _expenses.clear()
    }
}

class MainViewModelFactory(
    private val db: FBDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

