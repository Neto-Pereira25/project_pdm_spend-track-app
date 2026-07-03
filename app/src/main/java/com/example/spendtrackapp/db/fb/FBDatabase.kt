package com.example.spendtrackapp.db.fb

import android.util.Log
import android.util.Log.e
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class FBDatabase {

    interface Listener {
        fun onExpenseAdded(expense: FBExpense)
        fun onExpenseUpdated(expense: FBExpense)
        fun onExpenseRemoved(expense: FBExpense)

        fun onPublicPriceEntryAdded(entry: FBPublicPriceEntry)
        fun onPublicPriceEntryUpdated(entry: FBPublicPriceEntry)
        fun onPublicPriceEntryRemoved(entry: FBPublicPriceEntry)

        fun onSettingsLoaded(settings: FBSettings)
        fun onUserSignOut()
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private var expensesListReg: ListenerRegistration? = null
    private var settingsReg: ListenerRegistration? = null
    private var publicPriceEntriesReg: ListenerRegistration? = null

    private var listener: Listener? = null

    init {
        auth.addAuthStateListener { auth ->

            if (auth.currentUser == null) {
                expensesListReg?.remove()
                settingsReg?.remove()
                publicPriceEntriesReg?.remove()

                expensesListReg = null
                settingsReg = null
                publicPriceEntriesReg = null

                listener?.onUserSignOut()
                return@addAuthStateListener
            }

            val uid = auth.currentUser!!.uid
            val refCurrUser = db.collection("users").document(uid)

            expensesListReg?.remove()
            settingsReg?.remove()
            publicPriceEntriesReg?.remove()

            expensesListReg = refCurrUser
                .collection("expenses")
                .addSnapshotListener { snapshots, ex ->
                    if (ex != null) return@addSnapshotListener

                    snapshots?.documentChanges?.forEach { change ->
                        val fbExpense = change.document.toObject(FBExpense::class.java)

                        when (change.type) {
                            DocumentChange.Type.ADDED -> {
                                listener?.onExpenseAdded(fbExpense)
                            }

                            DocumentChange.Type.MODIFIED -> {
                                listener?.onExpenseUpdated(fbExpense)
                            }

                            DocumentChange.Type.REMOVED -> {
                                listener?.onExpenseRemoved(fbExpense)
                            }
                        }
                    }
                }

            settingsReg = refCurrUser
                .collection("settings")
                .document("main")
                .addSnapshotListener { snapshot, ex ->
                    if (ex != null) return@addSnapshotListener

                    snapshot?.toObject(FBSettings::class.java)?.let { settings ->
                        listener?.onSettingsLoaded(settings)
                    }
                }

            publicPriceEntriesReg = db
                .collection("public_price_entries")
                .addSnapshotListener { snapshots, ex ->
                    if (ex != null) return@addSnapshotListener

                    snapshots?.documentChanges?.forEach { change ->
                        val entry = change.document.toObject(FBPublicPriceEntry::class.java)

                        when (change.type) {
                            DocumentChange.Type.ADDED -> {
                                listener?.onPublicPriceEntryAdded(entry)
                            }

                            DocumentChange.Type.MODIFIED -> {
                                listener?.onPublicPriceEntryUpdated(entry)
                            }

                            DocumentChange.Type.REMOVED -> {
                                listener?.onPublicPriceEntryRemoved(entry)
                            }
                        }
                    }
                }
        }
    }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
    }

    fun registerUser(
        name: String,
        email: String,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")

        val uid = auth.currentUser!!.uid

        val fbUser = FBUser().apply {
            this.name = name
            this.email = email
        }

        db.collection("users")
            .document(uid)
            .set(fbUser)
            .addOnSuccessListener {
                onSuccess?.invoke()
            }
            .addOnFailureListener { ex ->
                onFailure?.invoke(ex)
            }
    }

    fun add(
        expense: FBExpense,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")

        if (expense.id.isNullOrBlank())
            throw RuntimeException("Expense with null or empty id!")

        val uid = auth.currentUser!!.uid
        val privateExpenseId = expense.id!!
        val publicEntryId = "${uid}_$privateExpenseId"

        val privateExpenseRef = db.collection("users")
            .document(uid)
            .collection("expenses")
            .document(privateExpenseId)

        privateExpenseRef
            .set(expense)
            .addOnSuccessListener {

                if (expense.lat != null && expense.lng != null) {
                    val publicEntry = FBPublicPriceEntry().apply {
                        id = publicEntryId
                        this.privateExpenseId = privateExpenseId
                        amount = expense.amount
                        category = expense.category
                        lat = expense.lat
                        lng = expense.lng
                        ownerUid = uid
                    }

                    db.collection("public_price_entries")
                        .document(publicEntryId)
                        .set(publicEntry)
                        .addOnSuccessListener {
                            onSuccess?.invoke()
                        }
                        .addOnFailureListener { ex ->
                            onFailure?.invoke(ex)
                        }
                } else {
                    onSuccess?.invoke()
                }
            }
            .addOnFailureListener { ex ->
                onFailure?.invoke(ex)
            }
    }

    fun remove(
        expense: FBExpense,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")

        if (expense.id.isNullOrBlank())
            throw RuntimeException("Expense with null or empty id!")

        val uid = auth.currentUser!!.uid
        val privateExpenseId = expense.id!!
        val publicEntryId = "${uid}_$privateExpenseId"

        db.collection("users")
            .document(uid)
            .collection("expenses")
            .document(privateExpenseId)
            .delete()
            .addOnSuccessListener {
                db.collection("public_price_entries")
                    .document(publicEntryId)
                    .delete()
                    .addOnSuccessListener {
                        onSuccess?.invoke()
                    }
                    .addOnFailureListener { ex ->
                        onFailure?.invoke(ex)
                    }
            }
            .addOnFailureListener { ex ->
                onFailure?.invoke(ex)
            }
    }

    fun saveMonthlyGoal(
        monthlyGoal: Double,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")

        val uid = auth.currentUser!!.uid

        val settings = FBSettings().apply {
            this.monthlyGoal = monthlyGoal
        }

        db.collection("users")
            .document(uid)
            .collection("settings")
            .document("main")
            .set(settings)
            .addOnSuccessListener {
                onSuccess?.invoke()
            }
            .addOnFailureListener { ex ->
                onFailure?.invoke(ex)
            }
    }
}