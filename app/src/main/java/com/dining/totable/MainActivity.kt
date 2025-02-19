package com.dining.totable

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dining.totable.navigation.AppNavigator
import com.dining.totable.ui.theme.ToTableTheme
import com.dining.totable.utils.PermissionHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToTableTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Request the notification permission
                    PermissionHelper.RequestNotificationPermission(
                        onPermissionGranted = {
                            // Permission granted
                        },
                        onPermissionDenied = {
                            // Handle the case where permission is denied
                        }
                    )
                    AppNavigator()
                    val db = Firebase.firestore
                    val docRef = db.collection("restaurants").document("1")
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Log.d("DATABASE", "DocumentSnapshot data: ${document.data}")
                            } else {
                                Log.d("DATABASE", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("DATABASE", "get failed with ", exception)
                        }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToTableTheme {
        Greeting("Android")
    }
}