package com.example.projectodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.projectodo.databinding.ActivityDetailBinding
import com.example.projectodo.databinding.ActivityProgressPopupBinding
import org.w3c.dom.Text

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
        val progressList = intent.getIntegerArrayListExtra("진행률 목록") // 상세화면으로부터 진행률 목록 받아오기

        getTarget(projectCode){ targetCount, test->
            for(i in 0 until targetCount){ // 목표 개수만큼 반복
                val progressView = layoutInflater.inflate(R.layout.progressbar, null)

                if(progressView.parent != null){
                    (progressView.parent as ViewGroup).removeView(progressView)
                }
                val progressBar = progressView.findViewById<ProgressBar>(R.id.progressBar_obj) // 프로그레스바 객체
                val progressText = progressView.findViewById<TextView>(R.id.progress_text) // 프로그레스바 텍스트 객체
                val progressTitle = progressView.findViewById<TextView>(R.id.progress_title) // 목표 이름 객체

                progressBar.progress = progressList!!.get(i) // 진행률을 목록의 값으로 동적 출력
                progressText.setText("${progressList.get(i)}%") // 진행률을 텍스트로 동적 출력
                progressTitle.setText(test.get(i)) // 목표 이름을 동적 출력

                parentLayout.addView(progressView) // 목표 개수만큼 프로그레스바 동적 출력

            }
        }

        binding.popupBtn.setOnClickListener {
            finish() // 팝업창 종료
        }
    }

    private fun getTarget(projectCode: Int, callback: (Int, List<String>)-> Unit){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()
            val targets = projectDao?.getTargetByCode(projectCode) // 프로젝트 코드로 목표 받아오기
            val test = projectDao?.getTargetTitle(projectCode)

            runOnUiThread{
                val targetCount = targets?.size // 해당 프로젝트의 목표 개수
                callback(targetCount?: 0, test!!)
            }
        }.start()
    }
}