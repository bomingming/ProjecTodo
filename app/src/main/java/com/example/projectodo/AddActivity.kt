package com.example.projectodo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            Toast.makeText(this, "프로젝트가 등록되었습니다",Toast.LENGTH_SHORT).show()
            finish() // 등록 화면 종료
        }

        // 취소 버튼 이벤트
        binding.cancelBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("프로젝트 등록을 취소하시겠습니까?").setPositiveButton("확인", DialogInterface.OnClickListener{dialog, which ->
                Toast.makeText(this, "등록이 취소되었습니다", Toast.LENGTH_SHORT).show()
                finish()
            }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  })
            builder.show()
        }

        // 목표 삭제 버튼 이벤트
        binding.deleteTargetBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("목표를 삭제하시겠습니까?").setPositiveButton("삭제", DialogInterface.OnClickListener{dialog, which ->
                // 목표 삭제 이벤트 구현 필요
            }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  })
            builder.show()
        }
        
        // 일정 삭제 버튼 이벤트
        binding.deleteTodoBtn.setOnClickListener { 
            val builder = AlertDialog.Builder(this)
            builder.setMessage("일정을 삭제하시겠습니까?").setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which -> 
                // 일정 삭제 이벤트 구현 필요
            }).setNegativeButton("취소", DialogInterface.OnClickListener{dialog, which ->  })
            builder.show()
        }
    }
}