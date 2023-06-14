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
import com.example.projectodo.databinding.ActivityDetailBinding
import com.example.projectodo.databinding.ActivityEditBinding
import java.util.*

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    
    // 목표 블록 내부 변수
    private var dynamicTarget : TextView? = null // 목표 이름
    private var dynamicTodo : TextView? = null // 일정 내용

    // 목표 코드 리스트
    private val targetCodeList: MutableList<Int> = mutableListOf()

    // 일정 코드 리스트
    private val todoCodeList: MutableList<Int> = mutableListOf()

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
            val projectCode = intent.getIntExtra("프로젝트 코드_for수정", 0)
            val newTitle = binding.titleEdit.text.toString()
            val newStart = binding.startDateText.text.toString()
            val newEnd = binding.endDateText.text.toString()
            editProjectFromDB(projectCode, newTitle, newStart, newEnd) // DB값 UPDATE

            Thread{
                val database = AppDatabase.getInstance(this)
                val projectDao = database?.projectDAO()

                // 기존 목표는 수정, 새로운 목표는 삽입
                for(i in 0 until binding.tgBlockEidtLayout.childCount){
                    val targetBlock = binding.tgBlockEidtLayout.getChildAt(i) // 목표 블록
                    val targetTitle = targetBlock.findViewById<EditText>(R.id.target_title).text.toString() // 목표 이름 칸에 적힌 문자열
                    val targetCode = targetCodeList.getOrNull(i) // 목표 코드

                    var targetCodeForTodo: Int? = null // 일정을 위한 목표 코드

                    if(targetCode != null){ // 목표 코드가 존재하면
                        projectDao?.editTarget(targetCode, targetTitle, 0) // 기존 목표이므로 값을 UPDATE
                        //targetCodeForTodo = targetCode
                    }else{ // 목표 코드가 존재하지 않으면
                        val newTarget = TargetEntity(0, projectCode, targetTitle, 0)
                        targetCodeForTodo = projectDao?.insertTarget(newTarget)?.toInt()
                    }

                    //Log.e("값", targetCodeForTodo.toString())
                    val todoLayout = targetBlock.findViewById<LinearLayout>(R.id.td_add_layout)

                    // 기존 일정은 수정, 새로운 일정은 삽입
                    for(j in 0 until todoLayout.childCount){
                        val todoBlock = todoLayout.getChildAt(j)
                        val todoDetail = todoBlock.findViewById<EditText>(R.id.todo_list).text.toString() // 일정 내용 칸에 적힌 문자열
                        val todoCode = todoCodeList.getOrNull(i * todoLayout.childCount + j) // 일정 코드

                        Log.e("목표 코드는?", targetCodeForTodo.toString())
                        if(todoCode != null){
                            projectDao?.editTodo(todoCode, todoDetail)
                            //Log.e("여기만 돌아가나?", "넹")
                        }else{
                            val newTodo = TodoEntity(0, targetCodeForTodo!!, todoDetail, 0)
                            Log.e("이 값은?", targetCodeForTodo.toString())
                            projectDao?.insertTodo(newTodo)
                        }
                    }
                }
            }.start()

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
            val items = projectDao?.getAllProject()

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
                                deleteTargetFromDB(targetCode) // DB에서 해당 목표 삭제
                                (view.parent as ViewGroup).removeView(view) // 목표 블록 삭제
                                targetCodeList.remove(targetCode) // 목표 리스트에서 삭제
                                
                            }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  })
                            builder.show()
                        }

                        // 기존 일정 블록의 추가 버튼 이벤트
                        view.findViewById<Button>(R.id.todo_add_btn).setOnClickListener {
                            val newTodoBlock = inflater.inflate(R.layout.todo_block, null)
                            val newDeleteBtn = newTodoBlock.findViewById<ImageButton>(R.id.delete_todo_btn) // 기존 목표 블록 내부의 새로운 일정 블록에 속한 삭제 버튼

                            // 기존 목표 블록 내부마다 동적으로 일정 블록 생성
                            view.findViewById<LinearLayout>(R.id.td_add_layout).addView(newTodoBlock)

                            // 기존 목표 블록 내부의 새로운 일정 삭제 버튼 이벤트
                            newDeleteBtn.setOnClickListener {
                                view.findViewById<LinearLayout>(R.id.td_add_layout).removeView(newTodoBlock)
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
                                        todoCodeList.add(item_for_todo.todo_code) // 일정 코드 리스트에 추가

                                        dynamicTodo = todoblock.findViewById(R.id.todo_list)
                                        dynamicTodo?.text = item_for_todo.todo_detail

                                        // 기존 일정 블록의 삭제 버튼 이벤트
                                        todoblock.findViewById<ImageButton>(R.id.delete_todo_btn).setOnClickListener {
                                            deleteTodoFromDB(item_for_todo.todo_code) // DB에서 해당 일정 삭제
                                            tdBlockEditParentLayout.removeView(todoblock) // 기존 일정 블록 삭제
                                            todoCodeList.remove(item_for_todo.todo_code) // 일정 코드 리스트에서 삭제
                                        }
                                    }
                                }
                            }
                        }.start()
                    }
                }
            }
        }.start()
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
            //todoDataList.add(todoDetail)

            todoLayout.addView(todoblock) // 투두 생성

            // 투두 삭제 버튼 이벤트
            todoDeleteBtn.setOnClickListener {
                todoLayout.removeView(todoblock)
                //todoDataList.remove(todoDetail) // todo 리스트에서도 일정 정보 삭제
            }
        }
    }
    
    // DB에서 목표 값 삭제 메소드
    private fun deleteTargetFromDB(targetCode: Int){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()
            projectDao?.deleteTarget(targetCode)
        }.start()
    }

    // DB에서 일정 값 삭제 메소드
    private fun deleteTodoFromDB(todoCode: Int){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()
            projectDao?.deleteTodo(todoCode)
        }.start()
    }
}