package com.example.projectodo

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.example.projectodo.databinding.ActivityAddBinding
import java.util.Calendar

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var startDateString = "" // 시작일
        var endDateString = "" // 마감일

        // 시작일 버튼 이벤트
        binding.startDateBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                startDateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.startDateText.setText(startDateString)
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 마감일 버튼 이벤트
        binding.endDateBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                endDateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.endDateText.setText(endDateString)
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 등록 버튼 이벤트
        binding.regisBtn.setOnClickListener {
            val result = "프로젝트 블록 추가"
            val intent = Intent()

            // DB에 값 넣기
            Thread{
                val database = AppDatabase.getInstance(this)
                val projectDao = database?.projectDAO()
                val projectTB = ProjectEntity(0, binding.titleEdit.text.toString(), binding.startDateText.text.toString(), binding.endDateText.text.toString())
                projectDao?.insertProject(projectTB)

                runOnUiThread{
                }
            }.start()

            Toast.makeText(this, "프로젝트가 등록되었습니다",Toast.LENGTH_SHORT).show() // 토스트 메시지 출력

            finish() // 등록 화면 종료
        }

        // 취소 버튼 이벤트
        binding.cancelBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("프로젝트 등록을 취소하시겠습니까?").setPositiveButton("확인", DialogInterface.OnClickListener{dialog, which ->
                Toast.makeText(this, "등록이 취소되었습니다", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_CANCELED) // 취소 버튼을 눌렀음을 저장
                finish()
            }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  })
            builder.show()
        }

        // 목표 추가 버튼 이벤트
        binding.targetBtn.setOnClickListener {
            addTarget(binding.targetLayout)
        }
    }

    // 동적으로 목표 블록 추가하는 함수
    fun addTarget(viewGroup: ViewGroup) {
        val parentLayout = findViewById<LinearLayout>(R.id.target_layout) // 목표 레이아웃 객체 연결
        val view = LayoutInflater.from(this).inflate(R.layout.target_block, null) // 목표 블록 객체

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val deleteTartgetBtn = view.findViewById<ImageButton>(R.id.delete_target_btn) // 목표 삭제 버튼 객체
        val todoAddBtn = view.findViewById<Button>(R.id.todo_add_btn) // 투두 추가 버튼 객체

        layoutParams.setMargins(0, 0, 0, 30)
        view.layoutParams = layoutParams

        // 부모를 이미 지정한 경우 초기화
        if(view.parent != null){
            (view.parent as ViewGroup).removeView(view)
        }
        parentLayout.addView(view) // 목표 블록 추가

        // 목표 삭제 버튼 클릭 시
        deleteTartgetBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("목표를 삭제하시겠습니까?").setPositiveButton("삭제", DialogInterface.OnClickListener{dialog, which ->
                (view.parent as ViewGroup).removeView(view) // 목표 블록 삭제
            }).setNegativeButton("취소", DialogInterface.OnClickListener{dialog, which ->  })
            builder.show()
        }

        // 일정 등록 버튼 이벤트
        todoAddBtn.setOnClickListener {
            val todoblock = LayoutInflater.from(this).inflate(R.layout.todo_block, null) // // 투두 블록 객체
            val todoLayout = view.findViewById<LinearLayout>(R.id.todo_layout) // 투두 레이아웃 객체
            val todoDeleteBtn= todoblock.findViewById<ImageButton>(R.id.delete_todo_btn) // 투두 삭제 버튼 객체
            if(todoblock.parent != null){
                (todoblock.parent as ViewGroup).removeView(todoblock)
            }
            todoLayout.addView(todoblock) // 투두 생성

            // 투두 삭제 버튼 이벤트
            todoDeleteBtn.setOnClickListener {
                todoLayout.removeView(todoblock)
            }
        }
    }
}