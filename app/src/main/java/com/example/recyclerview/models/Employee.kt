package com.example.recyclerview.models

import java.io.Serializable

data class Employee(
    var id: String,
    var name: String,
    var department: String,
    var status: String,
    var isSelected: Boolean = false
) : Serializable