package com.example.projectodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        //refreshDetail()

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

            runOnUiThread{
                val parentLayout = findViewById<LinearLayout>(R.id.block_layout) // 레이아웃 객체 연결
                val inflater = LayoutInflater.from(this)
                parentLayout.removeAllViews() // 기존 블록 제거

                if(items != null) {
                    for (item in items) {
                        val view = inflater.inflate(R.layout.project_block, null) // 프로젝트 블록 연결
                        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        if(view.parent != null){
                            (view.parent as ViewGroup).removeView(view)
                        }
                        layoutParams.setMargins(0, 10, 0, 40)
                        view.layoutParams = layoutParams
                        parentLayout.addView(view)

                        dynamicTitle = view.findViewById(R.id.proj_title)
                        dynamicDate = view.findViewById(R.id.proj_date)

                        dynamicTitle?.text = item.project_title
                        dynamicDate?.text = "${item.start_day}~${item.end_day}"

                    }
                }
            }
        }.start()
    }
}