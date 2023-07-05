package com.example.projectodo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.size
import com.example.projectodo.databinding.ActivityDetailBinding
import com.example.projectodo.databinding.ActivityEditBinding
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    
    // 목표 블록 내부 변수
    private var dynamicTarget : TextView? = null // 목표 이름
    private var dynamicTodo : TextView? = null // 일정 내용

    // 목표 코드 리스트
    private val targetCodeList: MutableList<Int> = mutableListOf()

    // 목표 리스트
    private val targetCodeMap : MutableMap<Int?, MutableList<Int?>> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var startDateString = "" // 시작일
        var endDateString = "" // 마감일

        var isStartDateSelected = false // 시작일이 설정되었는지 체크

        openEdit(binding) // 수정 화면에 데이터 구성

        // 시작일 버튼 이벤트
        binding.startDateBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                startDateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.startDateText.setText(startDateString)
                isStartDateSelected = true // 시작일이 선택되었으므로 true
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(
                Calendar.DAY_OF_MONTH)).show()
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

        // 수정 버튼 이벤트
        binding.regisBtn.setOnClickListener {
            // 목표 제목이 비었는지 확인하는 변수
            var targetTitleIsEmpty = false

            if(binding.titleEdit.text.isEmpty()){
                Toast.makeText(this, "프로젝트 제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            }else{
                for(i in 0 until binding.tgBlockEidtLayout.childCount){
                    val targetBlock = binding.tgBlockEidtLayout.getChildAt(i) as ConstraintLayout
                    val targetTitle = targetBlock.findViewById<EditText>(R.id.target_title)
                    if(targetTitle.text.isEmpty()){
                        targetTitleIsEmpty = true
                    }
                }
                // 목표 중 제목이 빈 목표가 있는 경우
                if(targetTitleIsEmpty){
                    Toast.makeText(this, "목표 제목을 입력해주세요", Toast.LENGTH_SHORT).show()
                }else{
                    val projectCode = intent.getIntExtra("프로젝트 코드_for수정", 0)
                    val newTitle = binding.titleEdit.text.toString()
                    val newStart = binding.startDateText.text.toString()
                    val newEnd = binding.endDateText.text.toString()
                    editProjectFromDB(projectCode, newTitle, newStart, newEnd) // 프로젝트 DB값 UPDATE

                    Thread{
                        val database = AppDatabase.getInstance(this)
                        val projectDao = database?.projectDAO()

                        for((key, value) in targetCodeMap){
                            val targetBlock = binding.tgBlockEidtLayout.getChildAt(targetCodeMap.keys.toList().indexOf(key)) // 목표 블록 객체
                            val targetTitle = targetBlock.findViewById<EditText>(R.id.target_title).text.toString() // 목표 이름 칸에 적힌 문자열
                            val todoLayout = targetBlock.findViewById<LinearLayout>(R.id.td_add_layout) // 일정 레이아웃 객체
                            if(key != null){ // 기존 목표의 경우
                                for(element in value){ // 목표 당 일정 수만큼 반복
                                    val todoBlock = todoLayout.getChildAt(value.indexOf(element))
                                    val todoDetail = todoBlock.findViewById<EditText>(R.id.todo_list).text.toString() // 일정 내용 칸에 적힌 문자열
                                    if(element != null){ // 기존 목표의 기존 일정이 수정된 경우
                                        projectDao?.editTodo(key, todoDetail) // 일정 내용 수정
                                    }else{ // 기존 목표에 새 일정이 추가된 경우
                                        val newTodo = TodoEntity(0, key, todoDetail, 0)
                                        projectDao?.insertTodo(newTodo) // 새 일정 추가
                                    }
                                }
                            }else{ // 새 목표의 경우
                                val newTarget = TargetEntity(0, projectCode, targetTitle, todoLayout.childCount) // 새로운 목표 객체
                                val targetCode = projectDao?.insertTarget(newTarget)?.toInt() // 새로운 목표 값 DB 삽입 후 생성된 목표 코드 반환
                                for(i in 0 until todoLayout.childCount){
                                    val todoDetail = todoLayout.findViewById<TextView>(R.id.todo_list).text.toString()
                                    val newTodo = TodoEntity(0, targetCode!!, todoDetail, 0)
                                    projectDao?.insertTodo(newTodo)
                                }
                            }
                        }
                        runOnUiThread {
                            Toast.makeText(this, "프로젝트가 수정되었습니다", Toast.LENGTH_SHORT).show()
                        }
                    }.start()

                    finish() // 수정 화면 종료
                }
            }
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

        // 목표 추가 버튼 이벤트
        binding.targetBtn.setOnClickListener {
            // 목표 추가 메소드 호출
            addTarget(binding.tgBlockEidtLayout)
        }
    }

    // 수정 화면 실행 메소드
    private fun openEdit(binding: ActivityEditBinding){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()

            // 프로젝트 코드를 기준으로 DB에서 값 받아오기
            val projectCode_edit = intent.getIntExtra("프로젝트 코드_for수정", 0)
            val project = projectDao?.getProjectByCode(projectCode_edit)
            val itemTarget = projectDao?.getTargetByCode(projectCode_edit) // 프로젝트 코드로 목표 목록 받아오기

            runOnUiThread{
                binding.titleEdit.setText(project?.project_title) // 프로젝트 제목 값 불러오기
                binding.startDateText.setText(project?.start_day) // 프로젝트 시작일 값 불러오기
                binding.endDateText.setText(project?.end_day) // 프로젝트 마감일 값 불러오기

                // 목표 블록
                val parentLayout = findViewById<LinearLayout>(R.id.tg_block_eidt_layout) // 수정 화면 내부 레이아웃 객체 연결
                val inflater = LayoutInflater.from(this)
                parentLayout.removeAllViews() // 기존 블록 제거
                if(itemTarget != null){
                    for(i in 0 until itemTarget.size){
                        val view = inflater.inflate(R.layout.target_block, null)
                        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        val item = itemTarget[i]
                        val targetCode = item.target_code // 목표 코드(일정 불러오기 위함)

                        var todoCodeList : MutableList<Int?> = mutableListOf() // 일정 코드를 담을 리스트

                        if(view.parent != null){
                            (view.parent as ViewGroup).removeView(view)
                        }
                        layoutParams.setMargins(0, 10, 0, 40)
                        view.layoutParams = layoutParams

                        parentLayout.addView(view)
                        targetCodeList.add(targetCode) // 목표 리스트에 목표 코드 추가

                        dynamicTarget = view.findViewById(R.id.target_title)
                        dynamicTarget?.text = item.target_title

                        // 기존 목표 블록의 삭제 버튼 이벤트
                        view.findViewById<ImageButton>(R.id.delete_target_btn).setOnClickListener {
                            val builder = AlertDialog.Builder(this)
                            builder.setMessage("목표를 삭제하시겠습니까?").setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                                (view.parent as ViewGroup).removeView(view) // 목표 블록 삭제
                                targetCodeMap.remove(targetCode) // 목표 맵에서 목표 삭제
                                //Log.e("기존 목표 제거", targetCodeMap.toString())
                            }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  })
                            builder.show()
                        }

                        // 기존 목표 블록의 일정 추가 버튼 이벤트
                        view.findViewById<Button>(R.id.todo_add_btn).setOnClickListener {
                            val newTodoBlock = inflater.inflate(R.layout.todo_block, null)
                            val newDeleteBtn = newTodoBlock.findViewById<ImageButton>(R.id.delete_todo_btn) // 기존 목표 블록 내부의 새로운 일정 블록에 속한 삭제 버튼

                            // 기존 목표 블록 내부마다 동적으로 새로운 일정 블록 생성
                            view.findViewById<LinearLayout>(R.id.td_add_layout).addView(newTodoBlock)
                            todoCodeList.add(null) // 일정 코드 리스트에 null 추가
                            //Log.e("기존 목표 블록에 새 일정 추가", targetCodeMap.toString())

                            // 기존 목표 블록 내부의 새로운 일정 삭제 버튼 이벤트
                            newDeleteBtn.setOnClickListener {
                                view.findViewById<LinearLayout>(R.id.td_add_layout).removeView(newTodoBlock)
                                val todoRemoveList = targetCodeMap[targetCode]
                                todoRemoveList?.remove(null) // 일정 코드 리스트에서 null 제거
                                //Log.e("기존 목표 블록에 새 일정 제거", targetCodeMap.toString())
                            }
                        }

                        Thread{
                            val itemTodo = projectDao?.getTodoByCode(targetCode) // 목표 코드로 일정 목록 받아오기

                            runOnUiThread {
                                val superParentLayout = findViewById<LinearLayout>(R.id.tg_block_eidt_layout)

                                if(itemTodo != null){
                                    for(j in 0 until itemTodo!!.size){
                                        val todoblock = inflater.inflate(R.layout.todo_block, null)
                                        val item_for_todo = itemTodo[j]

                                        val todoLayout = superParentLayout.getChildAt(i) as ConstraintLayout
                                        val tdBlockEditParentLayout = todoLayout.findViewById<LinearLayout>(R.id.td_add_layout)

                                        if(todoblock.parent !=null){
                                            (todoblock.parent as ViewGroup).removeView(todoblock)
                                        }
                                        tdBlockEditParentLayout.addView(todoblock)
                                        todoCodeList.add(item_for_todo.todo_code) // 일정 코드를 리스트에 삽입
                                        //Log.e("기존 목표 블록에 기존 일정 불러오기", targetCodeMap.toString())

                                        dynamicTodo = todoblock.findViewById(R.id.todo_list)
                                        dynamicTodo?.text = item_for_todo.todo_detail

                                        // 기존 일정 블록의 삭제 버튼 이벤트
                                        todoblock.findViewById<ImageButton>(R.id.delete_todo_btn).setOnClickListener {
                                            tdBlockEditParentLayout.removeView(todoblock) // 기존 일정 블록 삭제
                                            val todoRemoveList = targetCodeMap[targetCode] // 목표 맵 중 해당 목표 리스트
                                            todoRemoveList?.remove(item_for_todo.todo_code) // 목표 리스트에서 일정 삭제
                                            //Log.e("기존 목표 블록에 기존 일정 삭제", targetCodeMap.toString())
                                        }
                                    }
                                }
                                targetCodeMap.put(targetCode, todoCodeList) // 목표 맵에 기존 목표의 일정 값 추가
                                Log.e("현재 목표 맵", targetCodeMap.toString())
                            }
                        }.start()
                    }
                }
            }
        }.start()
    }

    // 동적으로 목표 블록 추가하는 메소드
    private fun addTarget(viewGroup: ViewGroup){
        val parentLayout = findViewById<LinearLayout>(R.id.tg_block_eidt_layout) // 목표 레이아웃 객체 연결
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

        //var todoCodeList : MutableList<Int?> = mutableListOf() // 일정 코드를 담을 리스트
        parentLayout.addView(view) // 목표 블록 추가
        targetCodeMap.put(null, mutableListOf()) // 목표 맵에 코드가 null인 새 목표 추가
        //Log.e("과연?", targetCodeMap.toString())

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

            todoLayout.addView(todoblock) // 투두 생성

            // 투두 삭제 버튼 이벤트
            todoDeleteBtn.setOnClickListener {
                todoLayout.removeView(todoblock)
            }
        }
    }

    // 수정된 프로젝트 값 DB에 넣기
    private fun editProjectFromDB(projectCode: Int, newTitle: String, newStart: String, newEnd: String){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()

            var project = projectDao?.getProjectByCode(projectCode)
            if(project != null){
                projectDao?.editProject(projectCode, newTitle, newStart, newEnd) // 수정된 값을 DB에 UPDATE
            }
        }.start()
    }
}