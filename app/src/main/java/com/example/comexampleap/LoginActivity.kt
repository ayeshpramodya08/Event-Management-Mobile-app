package com.example.comexampleap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Firebase Authentication හරහා ලොග් වීම
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // ලොගින් වීම සාර්ථකයි නම් පමණක් Role එක පරීක්ෂා කරයි
                        checkUserRole()
                    } else {
                        Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        tvSignUp.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    // Database එකේ ඇති Role එක අනුව අදාළ Dashboard එකට යැවීම
    private fun checkUserRole() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

            dbRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val role = snapshot.child("role").value.toString()

                    if (role == "Admin") {
                        // Admin නම් CRUD සහිත MainActivity වෙත
                        Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // සාමාන්‍ය User නම් View පමණක් කළ හැකි UserDashboard වෙත
                        Toast.makeText(this, "Welcome User!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, UserDashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Role not assigned. Please contact admin.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Database Error. Try again!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ඔයාට හැමවෙලේම Email/PW ගහලා ලොග් වෙන්න ඕනේ නිසා,
    // onStart එක ඇතුළේ තිබුණු auto-redirect කේතය ඉවත් කර ඇත.
    override fun onStart() {
        super.onStart()
        // මෙතන හිස්ව තැබීමෙන් හැමවිටම Login පේජ් එක මුලින්ම දිස්වේ.
    }
}