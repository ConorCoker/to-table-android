package com.dining.totable.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ListenerRegistration

data class Order(
    val id: String = "",
    val itemName: String = "",
    val specialRequests: Map<String, String>,
    val price: Double = 0.0,
    val status: String = "pending"
)

class OrdersViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders
    private var listenerRegistration: ListenerRegistration? = null

    fun fetchOrders(restaurantId: String) {
        listenerRegistration = db.collection("restaurants")
            .document(restaurantId)
            .collection("orders")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error (e.g., log it or update UI)
                    return@addSnapshotListener
                }
                val orderList = snapshot?.documents?.map { doc ->
                    Order(
                        id = doc.id,
                        itemName = doc.getString("itemName") ?: "",
                        specialRequests = doc.get("specialRequests") as? Map<String, String> ?: emptyMap(),
                        price = doc.getDouble("price") ?: 0.0,
                        status = doc.getString("status") ?: "pending"
                    )
                } ?: emptyList()
                _orders.value = orderList
            }
    }

    fun updateOrderStatus(restaurantId: String, orderId: String, newStatus: String) {
        db.collection("restaurants")
            .document(restaurantId)
            .collection("orders")
            .document(orderId)
            .update("status", newStatus)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}