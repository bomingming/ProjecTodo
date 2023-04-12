package com.example.projectodo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.projectodo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 프로젝트 등록 이벤트를 위한 변수 선언
    private lateinit var launcher : ActivityResultLauncher<Intent>

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

        binding.blockLayout.setOnClickListener{
            val detailIntent: Intent = Intent(this, DetailActivity::class.java)
            startActivity(detailIntent)
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                val addedView = data?.getStringExtra("프로젝트 등록")

                val parentLayout = binding.blockLayout
                val inflater = LayoutInflater.from(this)
                val view = inflater.inflate(R.layout.project_block, null) // 프로젝트 블록 연결
                val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                addedView?.let{
                    layoutParams.setMargins(0, 10, 0, 40)
                    view.layoutParams = layoutParams

                    parentLayout.addView(view)
                }
            }
        }
    }
}