package com.example.projectodo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.projectodo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 프로젝트 등록 이벤트를 위한 변수 선언
    private lateinit var launcher : ActivityResultLauncher<Intent>
    
    // 동적으로 추가되는 뷰 내부의 텍스트 뷰의 참조 변수
    private var dynamicTitle : TextView? = null // 프로젝트 제목
    private var dynamicDate : TextView? = null // 프로젝트 기간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로딩화면으로 시작
        val loadingIntent: Intent = Intent(this, LoadingActivity::class.java)
        startActivity(loadingIntent)

        // 프로젝트 추가 버튼 이벤트 처리(새창)
        binding.addBtn.setOnClickListener{
            //val addIntent: Intent = Intent(this, AddActivity::class.java)
            //startActivity(addIntent)
            launcher.launch(Intent(this, AddActivity::class.java))
        }

        // 프로젝트 블록 클릭 이벤트
        binding.blockLayout.setOnClickListener{
            val detailIntent: Intent = Intent(this, DetailActivity::class.java)
            startActivity(detailIntent)
        }

        // ActivityResultLauncher 초기화
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){ // 등록 버튼이 눌러진 경우
                val data = result.data
                val addedView = data?.getStringExtra("프로젝트 등록")
                val projTitle = data?.getStringExtra("프로젝트 제목")
                val projDate = data?.getStringExtra("프로젝트 기간")

                val parentLayout = binding.blockLayout // 레이아웃 객체 연결
                val inflater = LayoutInflater.from(this)
                val view = inflater.inflate(R.layout.project_block, null) // 프로젝트 블록 연결
                val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                
                // 동적으로 뷰 추가
                addedView?.let{
                    layoutParams.setMargins(0, 10, 0, 40)
                    view.layoutParams = layoutParams

                    parentLayout.addView(view)

                    // 추가된 블록의 텍스트 뷰 참조 변수 초기화
                    dynamicTitle = view.findViewById(R.id.proj_title)
                    dynamicDate = view.findViewById(R.id.proj_date)
                }

                // 동적 뷰 내부 텍스트 뷰 수정
                projTitle?.let{value ->
                    dynamicTitle?.text = value
                }
                projDate?.let { value->
                    dynamicDate?.text = value
                }
            }
        }

    }
}