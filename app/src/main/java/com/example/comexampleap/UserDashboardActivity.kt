package com.example.comexampleap

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.*

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var eventList: ArrayList<EventModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var btnProfile: ImageButton
    private lateinit var categoryChipGroup: ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        // 1. UI අංග හඳුනා ගැනීම
        userRecyclerView = findViewById(R.id.userRecyclerView)
        btnProfile = findViewById(R.id.btnProfile)
        categoryChipGroup = findViewById(R.id.categoryChipGroup)

        // 2. RecyclerView සැකසීම
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        eventList = arrayListOf<EventModel>()

        // 3. මුලින්ම හැම Event එකක්ම පෙන්වන්න (Default selection is 'All')
        getFilteredEvents("All")

        // 4. ChipGroup එකේ තෝරන Category එක අනුව Filter කිරීමේ ලොජික් එක
        categoryChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chipAll -> getFilteredEvents("All")
                R.id.chipWorkshop -> getFilteredEvents("Workshop")
                R.id.chipSeminar -> getFilteredEvents("Seminar")
                R.id.chipSports -> getFilteredEvents("Sports")
                R.id.chipOther -> getFilteredEvents("Other")
                else -> getFilteredEvents("All") // කිසිවක් තෝරා නොමැති නම් සියල්ල පෙන්වන්න
            }
        }

        // 5. Profile Icon එක ක්ලික් කළ විට ProfileActivity එකට යාම
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getFilteredEvents(category: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("Events")

        // Firebase එකෙන් Category එක අනුව දත්ත Filter කරන Query එක
        val query = if (category == "All") {
            dbRef
        } else {
            dbRef.orderByChild("category").equalTo(category)
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                if (snapshot.exists()) {
                    for (eventSnap in snapshot.children) {
                        val eventData = eventSnap.getValue(EventModel::class.java)
                        if (eventData != null) {
                            eventList.add(eventData)
                        }
                    }
                    // Adapter එකට දත්ත ලබා දී RecyclerView එකට සම්බන්ධ කිරීම
                    val mAdapter = EventAdapter(eventList)
                    userRecyclerView.adapter = mAdapter
                } else {
                    eventList.clear()
                    userRecyclerView.adapter = EventAdapter(eventList)
                    Toast.makeText(this@UserDashboardActivity, "No events found in $category", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserDashboardActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}