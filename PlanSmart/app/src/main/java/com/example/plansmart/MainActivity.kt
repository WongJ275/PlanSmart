package com.example.plansmart

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

//firebase
private lateinit var auth: FirebaseAuth

private var fileNameList: ArrayList<String> = arrayListOf("bucketList.json", "dailyGoals.json", "todoList.json", "tag.json", "tagMap.json", "username.txt")

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx: Context = applicationContext
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        val navController = findNavController(R.id.fragmentContainerView)

        bottomNavBar.setupWithNavController(navController)

        try {
            ctx.openFileOutput("connected.txt", Context.MODE_PRIVATE).use {
                it.write("".toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //firebase
        auth = Firebase.auth
        val db = Firebase.firestore

        // Check if user is signed in (non-null) and update UI accordingly.
        val usr = auth.currentUser
        if (usr != null && !usr.isAnonymous) {
            Log.d("log", "signed in")
            val docRef = db.collection("data").document(usr.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("log", "DocumentSnapshot data: ${document.data}")
                        for (filename in fileNameList) {
                            try {
                                ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
                                    it.write(document.data?.get(filename.split(".")[0]).toString().toByteArray())
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        Log.d("log", "No such document")
                    }
                    //bottomNavBar.selectedItemId = R.id.todo_list
                    //bottomNavBar.selectedItemId = R.id.bucket_list
                    val id = navController.currentDestination?.id
                    navController.popBackStack(id!!,true)
                    navController.navigate(id)
                }
                .addOnFailureListener { exception ->
                    Log.d("log", "get failed with ", exception)
                }
        }
    }
}