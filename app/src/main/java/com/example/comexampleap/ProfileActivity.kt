package com.example.comexampleap

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        // UI IDs හඳුනා ගැනීම
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvRoleBadge = findViewById<TextView>(R.id.tvRoleBadge)

        // 1. Back Button - කලින් පේජ් එකට යාම
        btnBack.setOnClickListener {
            finish()
        }

        // 2. Firebase එකෙන් දත්ත ගෙන ඒම
        if (userId != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("fullName").value.toString()
                        val email = snapshot.child("email").value.toString()
                        val role = snapshot.child("role").value.toString()

                        tvName.text = name
                        tvEmail.text = email

                        // Role එක අනුව Badge එකේ නම සහ පාට වෙනස් කිරීම
                        tvRoleBadge.text = role.uppercase()
                        if (role == "Admin") {
                            tvRoleBadge.setBackgroundColor(Color.parseColor("#4CAF50")) // Admin -> Green
                        } else {
                            tvRoleBadge.setBackgroundColor(Color.parseColor("#2196F3")) // User -> Blue
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // 3. Logout Button with Confirmation Dialog (UX Improvement)
        btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout from your account?")

            // "Yes" එබුවොත් පමණක් Logout කරන්න
            builder.setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                // කලින් ඇරිලා තිබුණු පේජ් ඔක්කොම වසා දමා Login එකට යවයි
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }

            // "No" එබුවොත් මුකුත් කරන්න එපා
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
}