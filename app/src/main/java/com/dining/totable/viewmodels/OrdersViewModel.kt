package com.dining.totable.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ListenerRegistration

data class Order(
    val id: String,
    val items: List<Item>,
    val status: String,
    val total: Double,
    val timestamp: com.google.firebase.Timestamp
) {
    data class Item(
        val itemName: String,
        val price: Double,
        val quantity: Int,
        val specialRequests: String
    )
}

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
                    val items = when (val itemsData = doc.get("items")) {
                        is List<*> -> itemsData.mapNotNull { item ->
                            if (item is Map<*, *>) {
                                Order.Item(
                                    itemName = item["itemName"] as? String ?: "",
                                    price = (item["price"] as? Number)?.toDouble() ?: 0.0,
                                    quantity = (item["quantity"] as? Number)?.toInt() ?: 1,
                                    specialRequests = item["specialRequests"] as? String ?: ""
                                )
                            } else {
                                null
                            }
                        }
                        else -> emptyList()
                    }
                    Order(
                        id = doc.id,
                        items = items,
                        status = doc.getString("status") ?: "pending",
                        total = doc.getDouble("total") ?: 0.0,
                        timestamp = doc.getTimestamp("timestamp") ?: com.google.firebase.Timestamp.now()
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