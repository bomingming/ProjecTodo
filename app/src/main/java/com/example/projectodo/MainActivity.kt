package com.example.projectodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectodo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로딩화면으로 시작
        val loadingIntent: Intent = Intent(this, LoadingActivity::class.java)
        startActivity(loadingIntent)

        binding.addBtn.setOnClickListener{
            val addIntent: Intent = Intent(this, AddActivity::class.java)
            startActivity(addIntent)
        }
    }
}