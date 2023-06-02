package com.example.projectodo

import android.content.Intent
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

    private  lateinit var binding: ActivityDetailBinding

    // 동적으로 추가되는 뷰 내부의 텍스트 뷰의 참조 변수
    private var dynamicTitle : TextView? = null // 프로젝트 제목
    private var dynamicDate : TextView? = null // 프로젝트 기간
    private var projectCode: Int = 0 // 프로젝트 코드


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val projectCode_delete = intent.getIntExtra("프로젝트 코드", 0)
        
        // DB에서 값 불러와 데이터 출력
        refreshDetail(binding)

        // 더보기 버튼 이벤트 처리
        binding.moreBtn.setOnClickListener {
            val bottomSheet = BottomSheetFragment()
            // Fragment로 프로젝트 코드 넘겨주기
            bottomSheet.arguments = Bundle().apply {
                putInt("프로젝트 코드", projectCode_delete)
            }
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }

    private fun refreshDetail(binding: ActivityDetailBinding){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()
            val items = projectDao?.getAllProject()
            
            // 프로젝트 코드를 기준으로 DB에서 값 받아오기
            val projectCode_detail = intent.getIntExtra("프로젝트 코드", 0)
            val project = projectDao?.getProjectByCode(projectCode_detail)

            runOnUiThread{
                binding.titleText.text = project?.project_title // 프로젝트 제목
                binding.projDatePeriod.text = "${project?.start_day} ~ ${project?.end_day}" // 프로젝트 기간
            }
        }.start()
    }
}