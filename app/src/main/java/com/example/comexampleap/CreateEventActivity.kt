package com.example.comexampleap

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private var isUpdateMode = false
    private var existingEventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val etTitle = findViewById<EditText>(R.id.etEventTitle)
        val etDate = findViewById<EditText>(R.id.etEventDate) // New
        val etDesc = findViewById<EditText>(R.id.etDescription)
        val spCategory = findViewById<Spinner>(R.id.spCategory)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val tvHeaderTitle = findViewById<TextView>(R.id.tvHeaderTitle)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // 1. Date Picker Logic
        val calendar = Calendar.getInstance()
        etDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = String.format("%02d/%02d/%d", day, month + 1, year)
                etDate.setText(selectedDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }

        // 2. Spinner Categories
        val categories = arrayOf("Select Category", "Workshop", "Seminar", "Exhibition", "Sports", "Cultural", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapter

        // 3. Update Mode Check
        if (intent.hasExtra("eventId")) {
            isUpdateMode = true
            existingEventId = intent.getStringExtra("eventId")
            etTitle.setText(intent.getStringExtra("title"))
            etDesc.setText(intent.getStringExtra("desc"))
            etDate.setText(intent.getStringExtra("date")) // Get existing date

            val categoryValue = intent.getStringExtra("category")
            spCategory.setSelection(adapter.getPosition(categoryValue))

            tvHeaderTitle.text = "Update Event"
            btnSubmit.text = "Update Event"
        }

        btnBack.setOnClickListener { finish() }
        btnCancel.setOnClickListener { finish() }

        // 4. Submit Logic
        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val date = etDate.text.toString().trim()
            val description = etDesc.text.toString().trim()
            val category = spCategory.selectedItem.toString()

            if (title.isEmpty() || date.isEmpty() || description.isEmpty() || category == "Select Category") {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            val database = FirebaseDatabase.getInstance().getReference("Events")
            val eventId = if (isUpdateMode) existingEventId else database.push().key

            val eventMap = mapOf(
                "id" to eventId,
                "title" to title,
                "date" to date,
                "description" to description,
                "category" to category,
                "timestamp" to System.currentTimeMillis()
            )

            if (eventId != null) {
                database.child(eventId).setValue(eventMap)
                    .addOnSuccessListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, if (isUpdateMode) "Updated!" else "Created!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}