package com.example.comexampleap

data class EventModel(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val category: String? = null,
    val date: String? = null, // අලුතින් එකතු කළ දිනය
    val timestamp: Long? = null
)