package com.example.spendtrackapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spendtrackapp.db.fb.FBDatabase
import com.example.spendtrackapp.db.fb.FBExpense
import com.example.spendtrackapp.db.fb.toFBExpense
import com.example.spendtrackapp.model.Expense
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.spendtrackapp.db.fb.FBPublicPriceEntry
import com.example.spendtrackapp.db.fb.FBSettings

import com.example.spendtrackapp.model.ExpenseCluster
import kotlin.math.pow
import kotlin.math.round

class MainViewModel(
    private val db: FBDatabase
) : ViewModel(), FBDatabase.Listener {

    private val _expenses = mutableStateListOf<Expense>()

    val expenses: List<Expense>
        get() = _expenses.toList()

    private val _publicPriceEntries = mutableStateListOf<FBPublicPriceEntry>()

    private val _monthlyGoal = mutableStateOf(0.0)

    val monthlyGoal: Double
        get() = _monthlyGoal.value

    init {
        db.setListener(this)
    }

    fun add(
        description: String,
        amount: Double,
        category: String,
        lat: Double? = null,
        lng: Double? = null,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        val expense = Expense(
            id = System.currentTimeMillis().toString(),
            description = description,
            amount = amount,
            category = category,
            lat = lat,
            lng = lng
        )

        db.add(
            expense = expense.toFBExpense(),
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun update(
        expense: Expense,
        description: String,
        amount: Double,
        category: String,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        val updatedExpense = expense.copy(
            description = description,
            amount = amount,
            category = category
        )

        db.update(
            expense = updatedExpense.toFBExpense(),
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

    fun findById(id: String): Expense? {
        return _expenses.find { it.id == id }
    }

    fun totalSpent(): Double {
        return _expenses.sumOf { it.amount }
    }

    fun totalItems(): Int {
        return _expenses.size
    }

    fun saveMonthlyGoal(
        monthlyGoal: Double,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        db.saveMonthlyGoal(
            monthlyGoal = monthlyGoal,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun goalUsagePercent(): Double {
        if (monthlyGoal <= 0.0) return 0.0
        return totalSpent() / monthlyGoal
    }

    fun remainingAmount(): Double {
        return monthlyGoal - totalSpent()
    }

    fun goalStatusMessage(): String {
        if (monthlyGoal <= 0.0) {
            return "Nenhuma meta mensal definida."
        }

        val percent = goalUsagePercent()

        return when {
            percent >= 1.0 -> {
                "Atenção: você ultrapassou sua meta mensal."
            }

            percent >= 0.8 -> {
                "Atenção: você já atingiu 80% da sua meta mensal."
            }

            else -> {
                "Seus gastos estão dentro da meta."
            }
        }
    }

    fun goalStatusLevel(): Int {
        if (monthlyGoal <= 0.0) return 0

        val percent = goalUsagePercent()

        return when {
            percent >= 1.0 -> 3
            percent >= 0.8 -> 2
            else -> 1
        }
    }

    fun priceClusters(precision: Int = 3): List<ExpenseCluster> {
        val expensesWithLocation = _expenses.filter { expense ->
            expense.lat != null && expense.lng != null
        }

        val groupedExpenses = expensesWithLocation.groupBy { expense ->
            val roundedLat = roundCoordinate(expense.lat!!, precision)
            val roundedLng = roundCoordinate(expense.lng!!, precision)
            "$roundedLat,$roundedLng"
        }

        return groupedExpenses.map { (key, groupedList) ->
            val averageLat = groupedList.mapNotNull { it.lat }.average()
            val averageLng = groupedList.mapNotNull { it.lng }.average()
            val totalAmount = groupedList.sumOf { it.amount }
            val averageAmount = totalAmount / groupedList.size

            val mainCategory = groupedList
                .groupingBy { it.category }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key ?: "Sem categoria"

            ExpenseCluster(
                key = key,
                lat = averageLat,
                lng = averageLng,
                averageAmount = averageAmount,
                totalAmount = totalAmount,
                count = groupedList.size,
                mainCategory = mainCategory
            )
        }
    }

    fun collectivePriceClusters(precision: Int = 3): List<ExpenseCluster> {
        val entriesWithLocation = _publicPriceEntries.filter { entry ->
            entry.lat != null &&
                    entry.lng != null &&
                    entry.amount != null
        }

        val groupedEntries = entriesWithLocation.groupBy { entry ->
            val roundedLat = roundCoordinate(entry.lat!!, precision)
            val roundedLng = roundCoordinate(entry.lng!!, precision)
            "$roundedLat,$roundedLng"
        }

        return groupedEntries.map { (key, groupedList) ->
            val averageLat = groupedList.mapNotNull { it.lat }.average()
            val averageLng = groupedList.mapNotNull { it.lng }.average()
            val totalAmount = groupedList.sumOf { it.amount ?: 0.0 }
            val averageAmount = totalAmount / groupedList.size

            val mainCategory = groupedList
                .groupingBy { it.category ?: "Sem categoria" }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key ?: "Sem categoria"

            ExpenseCluster(
                key = key,
                lat = averageLat,
                lng = averageLng,
                averageAmount = averageAmount,
                totalAmount = totalAmount,
                count = groupedList.size,
                mainCategory = mainCategory
            )
        }
    }

    private fun roundCoordinate(value: Double, precision: Int): Double {
        val factor = 10.0.pow(precision)
        return round(value * factor) / factor
    }

    override fun onExpenseAdded(expense: FBExpense) {
        val converted = expense.toExpense()

        if (_expenses.none { it.id == converted.id }) {
            _expenses.add(converted)
        }
    }

    override fun onExpenseUpdated(expense: FBExpense) {
        val converted = expense.toExpense()
        val index = _expenses.indexOfFirst { it.id == converted.id }

        if (index != -1) {
            _expenses[index] = converted
        }
    }

    override fun onExpenseRemoved(expense: FBExpense) {
        val converted = expense.toExpense()
        _expenses.removeAll { it.id == converted.id }
    }

    override fun onPublicPriceEntryAdded(entry: FBPublicPriceEntry) {
        val entryId = entry.id ?: return

        if (_publicPriceEntries.none { it.id == entryId }) {
            _publicPriceEntries.add(entry)
        }
    }

    override fun onPublicPriceEntryUpdated(entry: FBPublicPriceEntry) {
        val entryId = entry.id ?: return
        val index = _publicPriceEntries.indexOfFirst { it.id == entryId }

        if (index != -1) {
            _publicPriceEntries[index] = entry
        }
    }

    override fun onPublicPriceEntryRemoved(entry: FBPublicPriceEntry) {
        val entryId = entry.id ?: return
        _publicPriceEntries.removeAll { it.id == entryId }
    }

    override fun onSettingsLoaded(settings: FBSettings) {
        _monthlyGoal.value = settings.monthlyGoal ?: 0.0
    }

    override fun onUserSignOut() {
        _expenses.clear()
        _publicPriceEntries.clear()
        _monthlyGoal.value = 0.0
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

