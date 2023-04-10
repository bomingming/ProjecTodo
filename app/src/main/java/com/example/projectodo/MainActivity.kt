package com.example.projectodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
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
        binding.addBtn.setOnClickListener{
            val addIntent: Intent = Intent(this, AddActivity::class.java)
            startActivity(addIntent)
        }

        binding.blockLayout.setOnClickListener{
            val detailIntent: Intent = Intent(this, DetailActivity::class.java)
            startActivity(detailIntent)
        }

        /*val parentLayout = binding.blockLayout

        binding.addBtn.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.project_block, null) // 프로젝트 블록 연결

            // margin을 설정하기 위한 파라미터 선언
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(0, 10, 0, 40)
            view.layoutParams = layoutParams

            parentLayout.addView(view)
        }*/
    }
}