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
    val timestamp: com.google.firebase.Timestamp,
    val tableNumber: String?
) {
    data class Item(
        val itemName: String,
        val price: Double,
        val quantity: Int,
        val specialRequests: String,
        val status: String
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

    suspend fun fetchRestaurantIdByEmail(email: String): String? {
        return try {
            val querySnapshot = db.collection("restaurants")
                .whereEqualTo("email", email)
                .get()
                .await()
            if (querySnapshot.isEmpty) {
                null
            } else {
                querySnapshot.documents.first().id
            }
        } catch (e: Exception) {
            android.util.Log.e("ToTableViewModel", "Error fetching restaurantId for email $email", e)
            null
        }
    }

    fun fetchOrders(restaurantId: String, deviceRoleId: String?) {
        listenerRegistration?.remove()
        listenerRegistration = db.collection("restaurants")
            .document(restaurantId)
            .collection("orders")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("ToTableViewModel", "Error fetching orders", error)
                    return@addSnapshotListener
                }
                val orderList = snapshot?.documents?.mapNotNull { doc ->
                    val itemsData = doc.get("items") as? List<*>
                    val filteredItems = itemsData?.mapNotNull { item ->
                        if (item is Map<*, *>) {
                            val roleId = item["roleId"] as? String
                            if (deviceRoleId == null || roleId == deviceRoleId) {
                                Order.Item(
                                    itemName = item["itemName"] as? String ?: "",
                                    price = (item["price"] as? Number)?.toDouble() ?: 0.0,
                                    quantity = (item["quantity"] as? Number)?.toInt() ?: 1,
                                    specialRequests = item["specialRequests"] as? String ?: "",
                                    status = item["status"] as? String ?: "pending"
                                )
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                    } ?: emptyList()

                    if (filteredItems.isNotEmpty() && filteredItems.any { it.status != "complete" }) {
                        Order(
                            id = doc.id,
                            items = filteredItems,
                            status = doc.getString("status") ?: "pending",
                            total = doc.getDouble("total") ?: 0.0,
                            timestamp = doc.getTimestamp("timestamp") ?: com.google.firebase.Timestamp.now(),
                            tableNumber = doc.getString("tableNumber")
                        )
                    } else {
                        null
                    }
                } ?: emptyList()
                _orders.value = orderList
            }
    }

    fun updateOrderItemStatus(
        restaurantId: String,
        orderId: String,
        newStatus: String
    ) {
        db.collection("restaurants")
            .document(restaurantId)
            .collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { document ->
                val items = document.get("items") as? List<Map<String, Any>> ?: return@addOnSuccessListener
                val updatedItems = items.map { item ->
                    if (item["status"] != "complete") {
                        item.toMutableMap().apply { put("status", newStatus) }
                    } else {
                        item
                    }
                }
                val orderStatus = when {
                    updatedItems.all { it["status"] == "complete" } -> "complete"
                    updatedItems.any { it["status"] == "in-progress" } -> "in-progress"
                    else -> "pending"
                }
                document.reference.update(
                    mapOf(
                        "items" to updatedItems,
                        "status" to orderStatus
                    )
                ).addOnSuccessListener {
                    android.util.Log.d("ToTableViewModel", "Updated order $orderId to status $orderStatus")
                }.addOnFailureListener { e ->
                    android.util.Log.e("ToTableViewModel", "Error updating order status", e)
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("ToTableViewModel", "Error fetching order for update", e)
            }
    }

    fun fetchRoles(restaurantId: String, callback: (Boolean) -> Unit) {
        roleListenerRegistration?.remove()
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
        roleListenerRegistration?.remove()
    }
}