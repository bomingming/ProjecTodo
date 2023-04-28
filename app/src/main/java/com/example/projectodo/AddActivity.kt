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
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.projectodo.databinding.ActivityAddBinding
import java.util.Calendar

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var startDateString = "" // 시작일
        var endDateString = "" // 마감일

        // 동적 뷰 관련 변수


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

            // 프로젝트 제목 입력값을 넘겨줌
            intent.putExtra("프로젝트 제목", binding.titleEdit.text.toString())
            // 프로젝트 기간 값을 넘겨줌
            intent.putExtra("프로젝트 기간", binding.startDateText.text.toString()+"~"+binding.endDateText.text.toString())

            // 프로젝트 등록 여부 넘겨줌
            intent.putExtra("프로젝트 등록", result)
            setResult(Activity.RESULT_OK, intent)
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

        // 목표 삭제 버튼 이벤트
        /*binding.deleteTargetBtn.setOnClickListener {
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
        }*/

        // 동적으로 뷰 추가
        binding.targetBtn.setOnClickListener {
            val parentLayout = binding.targetLayout // 목표 레이아웃 객체 연결
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.target_block, null)
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            layoutParams.setMargins(0, 0, 0, 30)
            view.layoutParams = layoutParams

            // 부모를 이미 지정한 경우 초기화
            if(view.parent != null){
                (view.parent as ViewGroup).removeView(view)
            }
            parentLayout.addView(view)
        }
    }
}