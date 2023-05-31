package com.example.projectodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.projectodo.databinding.ActivityDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailActivity : AppCompatActivity() {
    // 동적으로 추가되는 뷰 내부의 텍스트 뷰의 참조 변수
    private var dynamicTitle : TextView? = null // 프로젝트 제목
    private var dynamicDate : TextView? = null // 프로젝트 기간
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // DB에서 값 불러와 데이터 출력
        refreshDetail()

        // 더보기 버튼 이벤트 처리
        binding.moreBtn.setOnClickListener {
            val bottomSheet = BottomSheetFragment()
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }

    private fun refreshDetail(){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()
            val items = projectDao?.getAllProject()
            
            // TextView 객체들
            val projectTitle = findViewById<TextView>(R.id.title_text)
            val projectDate = findViewById<TextView>(R.id.proj_date_period)
            
            // 메인화면에서 클릭한 블록의 데이터
            val projectCode = intent.getIntExtra("프로젝트 코드", 0)

            // 프로젝트 코드를 기준으로 DB에서 값 받아오기
            val project = projectDao?.getProjectByCode(projectCode)

            runOnUiThread{
                projectTitle.text = project?.project_title // 프로젝트 제목
                projectDate.text = "${project?.start_day} ~ ${project?.end_day}" // 프로젝트 기간

            }
        }.start()
    }
}