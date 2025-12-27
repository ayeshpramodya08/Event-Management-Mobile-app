package com.example.comexampleap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etStudentId = findViewById<EditText>(R.id.etStudentId)
        val etEmailReg = findViewById<EditText>(R.id.etEmailReg)
        val etPassReg = findViewById<EditText>(R.id.etPassReg)

        btnBack.setOnClickListener {
            finish()
        }

        btnSignUp.setOnClickListener {
            val name = etFullName.text.toString().trim()
            val studentId = etStudentId.text.toString().trim()
            val email = etEmailReg.text.toString().trim()
            val password = etPassReg.text.toString().trim()

            if (name.isEmpty() || studentId.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all details!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Firebase Authentication හරහා User සාදා ගැනීම
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val userId = auth.currentUser?.uid
                        val database = FirebaseDatabase.getInstance().getReference("Users")

                        // 2. අමතර දත්ත සමඟ 'role' එක 'User' ලෙස ඇතුළත් කිරීම
                        val userMap = mapOf(
                            "fullName" to name,
                            "studentId" to studentId,
                            "email" to email,
                            "role" to "User" // Default ලෙස සෑම කෙනෙකුම සාමාන්‍ය User කෙනෙකි
                        )

                        userId?.let {
                            database.child(it).setValue(userMap).addOnSuccessListener {
                                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }.addOnFailureListener { e ->
                                Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}