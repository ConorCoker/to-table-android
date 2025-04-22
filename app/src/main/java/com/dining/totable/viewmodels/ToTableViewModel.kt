package com.dining.totable.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dining.totable.ui.screens.Role
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

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

class ToTableViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders
    private var listenerRegistration: ListenerRegistration? = null

    private val _roles = MutableLiveData<List<Role>>()
    val roles: LiveData<List<Role>> = _roles
    private var roleListenerRegistration: ListenerRegistration? = null

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    suspend fun fetchRestaurantIdByEmail(email: String): String? {
        return try {
            val querySnapshot = db.collection("restaurants")
                .whereEqualTo("email", email)
                .get()
                .await()
            if (querySnapshot.isEmpty) {
                null // No restaurant found
            } else {
                querySnapshot.documents.first().id // Assume first match as only 1 restaurant per email
            }
        } catch (e: Exception) {
            android.util.Log.e("ToTableViewModel", "Error fetching restaurantId for email $email", e)
            null
        }
    }

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

    fun fetchRoles(restaurantId: String, callback: (Boolean) -> Unit) {
        roleListenerRegistration?.remove() // Remove existing listener
        roleListenerRegistration = db.collection("restaurants")
            .document(restaurantId)
            .collection("roles")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("ToTableViewModel", "Error fetching roles", error)
                    _error.postValue("Error fetching roles: ${error.message}")
                    callback(false)
                    return@addSnapshotListener
                }
                val roleList = snapshot?.documents?.mapNotNull { doc ->
                    val name = doc.getString("name") ?: return@mapNotNull null
                    Role(
                        id = doc.id,
                        name = name
                    )
                } ?: emptyList()
                _roles.postValue(roleList)
                callback(true)
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}