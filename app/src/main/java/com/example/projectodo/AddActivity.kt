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
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.projectodo.databinding.ActivityAddBinding
import com.example.projectodo.databinding.ActivityDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding

    // todo 목록 리스트
    private val todoDataList: MutableList<String> = mutableListOf()

    // 선택된 start date 담을 변수
    private lateinit var selectedDay : Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var startDateString = "" // 시작일
        var endDateString = "" // 마감일

        var isStartDateSelected = false // 시작일이 설정되었는지 체크

        // 시작일 버튼 이벤트
        binding.startDateBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                startDateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.startDateText.setText(startDateString)
                isStartDateSelected = true // 시작일이 선택되었으므로 true
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 마감일 버튼 이벤트
        binding.endDateBtn.setOnClickListener {
            if(!isStartDateSelected){
                // 시작일 선택되지 않은 경우 마감일 버튼 이벤트 처리 X
                Toast.makeText(this, "시작일을 먼저 선택해주세요", Toast.LENGTH_SHORT).show() // 시작일 먼저 선택하라는 메시지 출력
                return@setOnClickListener
            }
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                endDateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.endDateText.setText(endDateString)
            }
            val startDate = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA).parse(binding.startDateText.text.toString())
            val startDateInMillis = startDate?.time?:0
            val datePickerDialog = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.datePicker.minDate = startDateInMillis // 마감일 범위 지정
            datePickerDialog.show()
        }

        // 등록 버튼 이벤트
        binding.regisBtn.setOnClickListener {
            if(binding.titleEdit.text.isEmpty()){ // 프로젝트 제목이 비어있는 경우
                Toast.makeText(this, "프로젝트 제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            }else{ // 프로젝트 제목이 입력된 경우
                // DB에 값 넣기
                Thread{
                    val database = AppDatabase.getInstance(this)
                    val projectDao = database?.projectDAO()
                    val projectTB = ProjectEntity(0, binding.titleEdit.text.toString(), binding.startDateText.text.toString(), binding.endDateText.text.toString())
                    val projectCode = projectDao?.insertProject(projectTB) // DB에 프로젝트 추가

                    // 목표 데이터 DB에 넣기
                    for(i in 0 until binding.tgBlockAddLayout.childCount){
                        val targetBlock = binding.tgBlockAddLayout.getChildAt(i) as ConstraintLayout
                        val targetTitle = targetBlock.findViewById<EditText>(R.id.target_title)

                        // DB에 프로젝트 저장 후 해당 기본키 목표 블록의 외래키로 할당
                        val targetEntity = TargetEntity(0,  projectCode!!.toInt(), targetTitle.text.toString(), 0)
                        val targetCode = projectDao?.insertTarget(targetEntity) // DB에 목록 추가

                        // 일정 데이터 DB에 넣기기
                        val todoLayout = targetBlock.findViewById<LinearLayout>(R.id.td_add_layout)
                        for(j in 0 until todoLayout.childCount){
                            val todoBlock = todoLayout.getChildAt(j) as LinearLayout
                            val todoDetail = todoBlock.findViewById<EditText>(R.id.todo_list)
                            val todoEntity = TodoEntity(0, targetCode!!.toInt(), todoDetail.text.toString(), 0)
                            projectDao?.insertTodo(todoEntity)
                        }
                        projectDao?.editTargetTodoCount(targetCode!!.toInt(), todoLayout.childCount) // 목표별 일정 개수 목표TB에 UPDATE
                    }

                    runOnUiThread{
                    }
                }.start()
                Toast.makeText(this, "프로젝트가 등록되었습니다",Toast.LENGTH_SHORT).show() // 토스트 메시지 출력

                finish() // 등록 화면 종료
            }
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
            addTarget(binding.tgBlockAddLayout)
        }
    }

    // 동적으로 목표 블록 추가하는 함수
    fun addTarget(viewGroup: ViewGroup) {
        val parentLayout = findViewById<LinearLayout>(R.id.tg_block_add_layout) // 목표 레이아웃 객체 연결
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

        // 일정 추가 버튼 이벤트
        todoAddBtn.setOnClickListener {
            val todoblock = LayoutInflater.from(this).inflate(R.layout.todo_block, null) // // 투두 블록 객체
            val todoLayout = view.findViewById<LinearLayout>(R.id.td_add_layout) // 투두 레이아웃 객체
            val todoDeleteBtn= todoblock.findViewById<ImageButton>(R.id.delete_todo_btn) // 투두 삭제 버튼 객체

            if(todoblock.parent != null){
                (todoblock.parent as ViewGroup).removeView(todoblock)
            }

            // todo EditText의 값을 넣을 변수를 리스트에 추가
            val todoDetail = todoblock.findViewById<EditText>(R.id.todo_list).text.toString()
            todoDataList.add(todoDetail)

            todoLayout.addView(todoblock) // 투두 생성

            // 투두 삭제 버튼 이벤트
            todoDeleteBtn.setOnClickListener {
                todoLayout.removeView(todoblock)
                todoDataList.remove(todoDetail) // todo 리스트에서도 일정 정보 삭제
            }
        }
    }
}