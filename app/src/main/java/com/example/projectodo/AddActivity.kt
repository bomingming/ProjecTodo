package com.example.projectodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectodo.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}