package com.example.comexampleap

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var fabAddEvent: FloatingActionButton
    private lateinit var btnProfile: ImageButton // අලුත් Profile Button එක
    private lateinit var dbRef: DatabaseReference
    private lateinit var eventList: ArrayList<EventModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. UI අංග හඳුනා ගැනීම (Initialization)
        eventRecyclerView = findViewById(R.id.rvEvents)
        fabAddEvent = findViewById(R.id.fabAddEvent)
        btnProfile = findViewById(R.id.btnProfile) // XML එකේ අලුත් ID එක

        // 2. RecyclerView සැකසීම
        eventRecyclerView.layoutManager = LinearLayoutManager(this)
        eventRecyclerView.setHasFixedSize(true)

        eventList = arrayListOf<EventModel>()

        // 3. Profile Icon එක ක්ලික් කළ විට ProfileActivity එකට යාම
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // 4. Floating Action Button (FAB) එක ක්ලික් කළ විට CreateEventActivity එකට යාම
        fabAddEvent.setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
        }

        // 5. Firebase එකෙන් දත්ත ලබා ගැනීම ආරම්භ කිරීම
        getEventsData()
    }

    private fun getEventsData() {
        dbRef = FirebaseDatabase.getInstance().getReference("Events")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                if (snapshot.exists()) {
                    for (eventSnap in snapshot.children) {
                        // Firebase එකේ ඇති දත්ත Model එකට පරිවර්තනය කිරීම
                        val eventData = eventSnap.getValue(EventModel::class.java)
                        if (eventData != null) {
                            eventList.add(eventData)
                        }
                    }

                    // Adapter එක හරහා RecyclerView එකට දත්ත සම්බන්ධ කිරීම
                    val mAdapter = EventAdapter(eventList)
                    eventRecyclerView.adapter = mAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}