package com.example.projectodo

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setMargins
import com.example.projectodo.databinding.ActivityDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.nio.file.Files.size
import java.util.concurrent.ThreadPoolExecutor

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    // 목표 블록 내부의 변수
    private var dynamicTarget : TextView? = null // 목표 이름
    private var dynamicTodo : TextView? = null // 일정 내용

    // 체크박스 리스트
    private var checkBoxList = mutableListOf<CheckBox>()

    // 일정 코드 리스트
    private var todoCodeList = mutableListOf<Int>()

    // 진행률 팝업 다이얼로그
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val projectCode_delete = intent.getIntExtra("프로젝트 코드", 0)

        // DB에서 값 불러와 데이터 출력
        refreshDetail(binding)

        // 더보기 버튼 이벤트 처리
        binding.moreBtn.setOnClickListener {
            val bottomSheet = BottomSheetFragment()
            // Fragment로 프로젝트 코드 넘겨주기
            bottomSheet.arguments = Bundle().apply {
                putInt("프로젝트 코드", projectCode_delete)
            }
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        // 진행률 더보기 버튼 클릭 이벤트
        binding.progressMoreBtn.setOnClickListener {
            //showPopup(projectCode_delete)
            val progressIntent: Intent = Intent(this, ProgressPopupActivity::class.java) // 진행률 상세 화면 인텐트
            progressIntent.putExtra("프로젝트 코드", projectCode_delete) // 프로젝트 코드 전달
            startActivity(progressIntent) // 진행률 화면 띄우기
        }
    }

    override fun onResume() {
        super.onResume()

        // DB 연결 Thread 호출
        refreshDetail(binding)
    }

    override fun onDestroy() {
        super.onDestroy()

        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()

            // 체크박스 완료 체크 여부 확인
            for (index in 0..checkBoxList.size-1) {
                if (checkBoxList[index].isChecked) {
                    // DB에 완료 값 저장(1)
                    projectDao?.editTodoCheck(todoCodeList[index], 1)
                } else {
                    // DB에 미완료 값 저장(0)
                    projectDao?.editTodoCheck(todoCodeList[index], 0)
                }
            }
        }.start()
    }

    private fun refreshDetail(binding: ActivityDetailBinding){
        val database = AppDatabase.getInstance(this)
        val projectDao = database?.projectDAO()

        // 프로젝트 코드를 기준으로 DB에서 값 받아오기
        val projectCode_detail = intent.getIntExtra("프로젝트 코드", 0)

        Thread{
            val project = projectDao?.getProjectByCode(projectCode_detail) // 프로젝트 코드로 프로젝트 값 받아오기
            val itemTarget = projectDao?.getTargetByCode(projectCode_detail) // 프로젝트 코드로 목표 목록 받아오기

            runOnUiThread{
                binding.titleText.text = project?.project_title // 프로젝트 제목
                binding.projDatePeriod.text = "${project?.start_day} ~ ${project?.end_day}" // 프로젝트 기간

                // 목표 블록
                val parentLayout = findViewById<LinearLayout>(R.id.tg_block_detail_layout) // 레이아웃 객체 연결
                val inflater = LayoutInflater.from(this)
                parentLayout.removeAllViews() // 기존 블록 제거
                if(itemTarget != null){
                    for(i in 0 until itemTarget.size){ // 해당 프로젝트의 목표 개수만큼 반복
                        val view = inflater.inflate(R.layout.target_block_detail, null) // 목표 블록 연결
                        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        val item = itemTarget[i]
                        val targetCode = item.target_code // 목표 코드(일정을 불러오기 위해 할당)

                        if(view.parent != null){
                            (view.parent as ViewGroup).removeView(view)
                        }
                        layoutParams.setMargins(0, 10, 0, 40)
                        view.layoutParams = layoutParams

                        parentLayout.addView(view) // 목표 블록 추가

                        dynamicTarget = view.findViewById(R.id.target_title_detail) // 목표 블록 객체 할당
                        dynamicTarget?.text = item.target_title // 목표 이름 출력

                        Thread{
                            val itemTodo = projectDao.getTodoByCode(targetCode) // 목표 코드로 일정 목록 받아오기

                            runOnUiThread{
                                val targetBlockLayout = parentLayout.getChildAt(i) as ConstraintLayout
                                val tdBlockDetailParentLayout = targetBlockLayout.findViewById<LinearLayout>(R.id.td_block_detail_layout)
                                tdBlockDetailParentLayout.removeAllViews() // 일정 레이아웃 초기화

                                if(itemTodo != null){
                                    for(j in 0 until itemTodo!!.size){
                                        val tdBlockDetailLayout = inflater.inflate(R.layout.todo_block_detail, null)
                                        val item_for_todo = itemTodo[j]

                                        val checkbox = tdBlockDetailLayout.findViewById<CheckBox>(R.id.todo_check_detail)
                                        // 체크박스를 리스트에 추가
                                        checkBoxList.add(checkbox)

                                        // 일정 코드를 리스트에 추가
                                        todoCodeList.add(item_for_todo.todo_code)

                                        dynamicTodo = tdBlockDetailLayout.findViewById(R.id.todo_list_detail)
                                        dynamicTodo?.text = item_for_todo.todo_detail

                                        if(tdBlockDetailLayout.parent != null){
                                            (tdBlockDetailLayout.parent as ViewGroup).removeView(tdBlockDetailLayout)
                                        }
                                        tdBlockDetailParentLayout.addView(tdBlockDetailLayout) // 일정 블록 추가
                                    }
                                }
                            }
                        }.start()
                    }
                }
            }
        }.start()
    }

}