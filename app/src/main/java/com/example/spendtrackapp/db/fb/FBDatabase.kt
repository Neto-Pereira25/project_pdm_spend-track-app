package com.example.spendtrackapp.db.fb

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class FBDatabase {

    companion object {
        private const val TAG = "SpendTrackFirestore"
    }

    interface Listener {
        fun onExpenseAdded(expense: FBExpense)
        fun onExpenseUpdated(expense: FBExpense)
        fun onExpenseRemoved(expense: FBExpense)
        fun onUserSignOut()
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private var expensesListReg: ListenerRegistration? = null
    private var listener: Listener? = null

    init {
        auth.addAuthStateListener { auth ->
            Log.d(TAG, "Auth state changed. currentUser=${auth.currentUser?.uid}")
            if (auth.currentUser == null) {
                Log.d(TAG, "No authenticated user. Removing expense listener if present")
                expensesListReg?.remove()
                listener?.onUserSignOut()
                return@addAuthStateListener
            }

            val refCurrUser = db.collection("users").document(auth.currentUser!!.uid)
            Log.d(TAG, "Attaching expenses snapshot listener to ${refCurrUser.path}/expenses")

            expensesListReg = refCurrUser.collection("expenses")
                .addSnapshotListener { snapshots, ex ->
                    if (ex != null) return@addSnapshotListener

                    if (snapshots == null) {
                        Log.d(TAG, "Expense snapshot returned null for user ${auth.currentUser!!.uid}")
                        return@addSnapshotListener
                    }

                    Log.d(TAG, "Received ${snapshots.documentChanges.size} expense change(s) for user ${auth.currentUser!!.uid}")

                    snapshots?.documentChanges?.forEach { change ->
                        val fbExpense = change.document.toObject(FBExpense::class.java)

                        Log.d(
                            TAG,
                            "Expense snapshot change=${change.type} id=${fbExpense.id} path=${change.document.reference.path}"
                        )

                        if (change.type == DocumentChange.Type.ADDED) {
                            listener?.onExpenseAdded(fbExpense)
                        } else if (change.type == DocumentChange.Type.MODIFIED) {
                            listener?.onExpenseUpdated(fbExpense)
                        } else if (change.type == DocumentChange.Type.REMOVED) {
                            listener?.onExpenseRemoved(fbExpense)
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
            .addOnSuccessListener { onSuccess?.invoke() }
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
        val documentRef = db.collection("users")
            .document(uid)
            .collection("expenses")
            .document(expense.id!!)

        Log.d(
            TAG,
            "Saving expense for uid=$uid at path=${documentRef.path} payload=id=${expense.id}, description=${expense.description}, amount=${expense.amount}, category=${expense.category}"
        )

        documentRef
            .set(expense)
            .addOnSuccessListener {
                Log.d(TAG, "Expense saved successfully at ${documentRef.path}")
                onSuccess?.invoke()
            }
            .addOnFailureListener { ex ->
                Log.e(TAG, "Expense save failed at ${documentRef.path}", ex)
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
        val documentRef = db.collection("users")
            .document(uid)
            .collection("expenses")
            .document(expense.id!!)

        Log.d(TAG, "Removing expense for uid=$uid at path=${documentRef.path}")

        documentRef
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Expense removed successfully at ${documentRef.path}")
                onSuccess?.invoke()
            }
            .addOnFailureListener { ex ->
                Log.e(TAG, "Expense remove failed at ${documentRef.path}", ex)
                onFailure?.invoke(ex)
            }
    }
}
