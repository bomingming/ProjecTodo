package com.example.projectodo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.projectodo.databinding.ActivityDetailBinding
import com.example.projectodo.databinding.ActivityEditBinding
import java.util.*

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var startDateString = "" // 시작일
        var endDateString = "" // 마감일

        openEdit(binding) // 수정 화면에 데이터 구성

        // 시작일 버튼 이벤트
        binding.startDateBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                startDateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.startDateText.setText(startDateString)
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(
                Calendar.DAY_OF_MONTH)).show()
        }

        // 마감일 버튼 이벤트
        binding.endDateBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                endDateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.endDateText.setText(endDateString)
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(
                Calendar.DAY_OF_MONTH)).show()
        }

        // 수정 버튼 이벤트
        binding.regisBtn.setOnClickListener {
            Toast.makeText(this, "프로젝트가 수정되었습니다", Toast.LENGTH_SHORT).show()
            finish() // 수정 화면 종료
        }

        // 취소 버튼 이벤트
        binding.cancelBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("프로젝트 수정을 취소하시겠습니까?").setPositiveButton("확인", DialogInterface.OnClickListener{ dialog, which ->
                Toast.makeText(this, "수정이 취소되었습니다", Toast.LENGTH_SHORT).show()
                finish()
            }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  })
            builder.show()
        }

        // 목표 삭제 버튼 이벤트
        binding.deleteTargetBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("목표를 삭제하시겠습니까?").setPositiveButton("삭제", DialogInterface.OnClickListener{ dialog, which ->
                // 목표 삭제 이벤트 구현 필요
            }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  })
            builder.show()
        }

        // 일정 삭제 버튼 이벤트
        binding.deleteTodoBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("일정을 삭제하시겠습니까?").setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                // 일정 삭제 이벤트 구현 필요
            }).setNegativeButton("취소", DialogInterface.OnClickListener{ dialog, which ->  })
            builder.show()
        }
    }

    // 수정 화면 실행 메소드
    private fun openEdit(binding: ActivityEditBinding){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()
            val items = projectDao?.getAllProject()

            // 프로젝트 코드를 기준으로 DB에서 값 받아오기
            val projectCode_edit = intent.getIntExtra("프로젝트 코드_for수정", 0)
            val project = projectDao?.getProjectByCode(projectCode_edit)

            runOnUiThread{
                binding.titleEdit.setText(project?.project_title) // 프로젝트 제목 값 불러오기
                binding.startDateText.setText(project?.start_day) // 프로젝트 시작일 값 불러오기
                binding.endDateText.setText(project?.end_day) // 프로젝트 마감일 값 불러오기
            }
        }.start()
    }

    // 수정된 결과 DB에 넣기
    private fun resultEdit(binding: ActivityEditBinding){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()
            val items = projectDao?.getAllProject()

            // 프로젝트 코드를 기준으로 DB에서 값 받아오기
            val projectCode_edit = intent.getIntExtra("프로젝트 코드_for수정", 0)
            val project = projectDao?.getProjectByCode(projectCode_edit)

            runOnUiThread{
            }
        }.start()
    }
}