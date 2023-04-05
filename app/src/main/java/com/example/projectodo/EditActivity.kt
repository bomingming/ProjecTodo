package com.example.projectodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectodo.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}