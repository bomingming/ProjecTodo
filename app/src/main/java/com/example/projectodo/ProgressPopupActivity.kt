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
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(binding.root)

        val width = resources.getDimensionPixelSize(R.dimen.popup_width) // 액티비티 너비
        val height = resources.getDimensionPixelSize(R.dimen.popup_height) // 액티비티 높이

        val window = window
        window.setLayout(width, height)

    }

    private fun getTarget(){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()

        }.start()
    }
}