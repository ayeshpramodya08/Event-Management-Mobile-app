package com.example.comexampleap

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EventAdapter(private val eventList: ArrayList<EventModel>) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_event_model, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEvent = eventList[position]

        // දත්ත UI එකට සම්බන්ධ කිරීම
        holder.tvTitle.text = currentEvent.title
        holder.tvCategory.text = currentEvent.category
        holder.tvDesc.text = currentEvent.description

        // අලුතින් එකතු කළ දිනය පෙන්වීම (date එක Model එකේ තිබිය යුතුයි)
        holder.tvDate.text = "📅 ${currentEvent.date}"

        // --- Role එක චෙක් කරලා Admin බටන් පෙන්වීම හෝ සැඟවීම ---
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserUid)
            userRef.child("role").get().addOnSuccessListener { snapshot ->
                val role = snapshot.value.toString()
                if (role == "Admin") {
                    holder.adminLayout.visibility = View.VISIBLE
                } else {
                    holder.adminLayout.visibility = View.GONE
                }
            }
        }

        // Delete Button Logic
        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes") { _, _ ->
                    FirebaseDatabase.getInstance().getReference("Events")
                        .child(currentEvent.id.toString()).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Event Deleted Successfully", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Edit Button Logic (මෙහිදී date එකත් Intent එකට එකතු කළා)
        holder.btnEdit.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CreateEventActivity::class.java)
            intent.putExtra("eventId", currentEvent.id)
            intent.putExtra("title", currentEvent.title)
            intent.putExtra("category", currentEvent.category)
            intent.putExtra("desc", currentEvent.description)
            intent.putExtra("date", currentEvent.date) // දිනයත් යවනවා
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = eventList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvShowTitle)
        val tvCategory: TextView = itemView.findViewById(R.id.tvShowCategory)
        val tvDesc: TextView = itemView.findViewById(R.id.tvShowDesc)

        // activity_event_model.xml එකේ දිනය පෙන්වන TextView එකේ ID එක මෙතනට දාන්න
        val tvDate: TextView = itemView.findViewById(R.id.tvShowDate)

        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEditEvent)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteEvent)
        val adminLayout: LinearLayout = itemView.findViewById(R.id.adminControlLayout)
    }
}