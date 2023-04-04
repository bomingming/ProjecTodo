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

        // 시작일 버튼 클릭 이벤트
        binding.startDateBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                startDateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.startDateText.setText(startDateString)
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 마감일 버튼 클릭 이벤트
        binding.endDateBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                endDateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.endDateText.setText(endDateString)
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 등록 버튼 클릭 이벤트
        binding.regisBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            Toast.makeText(this, "프로젝트가 등록되었습니다",Toast.LENGTH_SHORT).show()
            finish() // 등록 화면 종료
        }

    }
}