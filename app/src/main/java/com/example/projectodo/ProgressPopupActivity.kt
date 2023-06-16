package com.example.projectodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
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

        val projectCode = intent.getIntExtra("프로젝트 코드", 0) // 프로젝트 코드 받아오기
        val parentLayout = findViewById<LinearLayout>(R.id.progress_layout) // 레이아웃 객체 연결

        getTarget(projectCode){ targetCount->
            for(i in 0 until targetCount){
                val progressView = layoutInflater.inflate(R.layout.progressbar, null)
                if(progressView.parent != null){
                    (progressView.parent as ViewGroup).removeView(progressView)
                }
                parentLayout.addView(progressView) // 목표 개수만큼 프로그레스바 동적 출력
            }
        }
    }

    private fun getTarget(projectCode: Int, callback: (Int)-> Unit){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()
            val targets = projectDao?.getTargetByCode(projectCode) // 프로젝트 코드로 목표 받아오기

            runOnUiThread{
                val targetCount = targets?.size
                callback(targetCount?: 0)
            }


        }.start()
    }
}