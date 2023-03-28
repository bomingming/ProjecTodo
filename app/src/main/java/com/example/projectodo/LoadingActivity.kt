package com.example.projectodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.projectodo.databinding.ActivityLoadingBinding

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 화면 로딩
        startLoading()
    }

    // 화면 로딩 메소드
    private fun startLoading(){
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 3000)
    }
}