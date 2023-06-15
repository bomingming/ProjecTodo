package com.example.projectodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectodo.databinding.ActivityDetailBinding
import com.example.projectodo.databinding.ActivityProgressPopupBinding

class ProgressPopupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}