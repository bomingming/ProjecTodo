package com.example.projectodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.projectodo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로딩화면으로 시작
        val loadingIntent: Intent = Intent(this, LoadingActivity::class.java)
        startActivity(loadingIntent)

        // 프로젝트 추가 버튼 이벤트 처리(새창)
        /*binding.addBtn.setOnClickListener{
            val addIntent: Intent = Intent(this, AddActivity::class.java)
            startActivity(addIntent)
        }*/

        binding.block.setOnClickListener{
            val detailIntent: Intent = Intent(this, DetailActivity::class.java)
            startActivity(detailIntent)
        }

        val parentLayout = binding.block

        binding.addBtn.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.project_block, null)
            parentLayout.addView(view)
        }
    }
}