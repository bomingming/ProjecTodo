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
import java.nio.file.Files.size

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    // 목표 블록 내부의 변수
    private var dynamicTarget : TextView? = null // 프로젝트 제목


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

    override fun onResume() {
        super.onResume()

        // DB 연결 Thread 호출
        refreshDetail(binding)
    }

    private fun refreshDetail(binding: ActivityDetailBinding){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()

            // 프로젝트 코드를 기준으로 DB에서 값 받아오기
            val projectCode_detail = intent.getIntExtra("프로젝트 코드", 0)

            val itemTarget = projectDao?.getAllTarget(projectCode_detail) // 해당 프로젝트의 모든 목표 목록

            val project = projectDao?.getProjectByCode(projectCode_detail)
            val target = projectDao?.getTargetByCode(projectCode_detail)

            runOnUiThread{
                binding.titleText.text = project?.project_title // 프로젝트 제목
                binding.projDatePeriod.text = "${project?.start_day} ~ ${project?.end_day}" // 프로젝트 기간

                // 목표 블록
                val parentLayout = findViewById<LinearLayout>(R.id.tg_block_detail_layout) // 레이아웃 객체 연결
                val inflater = LayoutInflater.from(this)
                parentLayout.removeAllViews() // 기존 블록 제거
                if(itemTarget != null){
                    for(i in 0 until itemTarget.size){ // 해당 프로젝트의 목표 개수만큼 반복
                        val view = inflater.inflate(R.layout.target_block_detail, null) // 목표 블록 연결
                        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        val item = itemTarget[i]

                        if(view.parent != null){
                            (view.parent as ViewGroup).removeView(view)
                        }
                        layoutParams.setMargins(0, 10, 0, 40)
                        view.layoutParams = layoutParams

                        parentLayout.addView(view)

                        dynamicTarget = view.findViewById(R.id.target_title_detail)
                        dynamicTarget?.text = item.target_title

                    }
                }
                
            }
        }.start()
    }
}