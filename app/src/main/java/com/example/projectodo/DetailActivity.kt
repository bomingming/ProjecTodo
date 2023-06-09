package com.example.projectodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.projectodo.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    // 목표 블록 내부의 변수
    private var dynamicTarget : TextView? = null // 목표 이름
    private var dynamicTodo : TextView? = null // 일정 내용

    // 체크박스 리스트
    private var checkBoxList = mutableListOf<CheckBox>()

    // 일정 코드 리스트
    private var todoCodeList = mutableListOf<Int>()

    // 진행률 리스트
    private var progressList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val projectCode_delete = intent.getIntExtra("프로젝트 코드", 0)

        // 최상위 뷰
        val rootView = findViewById<View>(android.R.id.content)

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
            val progressIntent = Intent(this, ProgressPopupActivity::class.java) // 진행률 상세 화면 인텐트
            progressIntent.putExtra("프로젝트 코드", projectCode_delete) // 프로젝트 코드 전달
            val progressArray = ArrayList(progressList)
            progressIntent.putIntegerArrayListExtra("진행률 목록", progressArray)
            startActivity(progressIntent) // 진행률 화면 띄우기
        }

        // 도움말 버튼 클릭 시 말풍선 출력
        binding.helpBtn.setOnClickListener {
            binding.helpText.visibility = View.VISIBLE
            binding.helpTail.visibility = View.VISIBLE
        }

        // 화면 아무데나 클릭 시 말풍선 사라짐
        rootView.setOnClickListener{
            binding.helpText.visibility = View.GONE
            binding.helpTail.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()

        // DB 연결 Thread 호출
        refreshDetail(binding)
    }

    private fun refreshDetail(binding: ActivityDetailBinding){
        val database = AppDatabase.getInstance(this)
        val projectDao = database?.projectDAO()

        // 프로젝트 코드를 기준으로 DB에서 값 받아오기
        val projectCode_detail = intent.getIntExtra("프로젝트 코드", 0)
        progressList.clear()

        Thread{
            val project = projectDao?.getProjectByCode(projectCode_detail) // 프로젝트 코드로 프로젝트 값 받아오기
            val itemTarget = projectDao?.getTargetByCode(projectCode_detail) // 프로젝트 코드로 목표 목록 받아오기

            runOnUiThread{
                binding.titleText.text = project?.project_title // 프로젝트 제목
                binding.projDatePeriod.text = "${project?.start_day} ~ ${project?.end_day}" // 프로젝트 기간
                binding.progressBarDetail.progress = project!!.pj_progress // 프로그레스바
                binding.progressPerDetail.setText("${project?.pj_progress}%") // 진행률 수치

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

                        // 진행률 시도
                        var endCheckCount = 0
                        var result : Double
                        var todoCount = item.todo_count

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
                                        val todoLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
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
                                        todoLayoutParams.setMargins(0, 0, 0, 10)
                                        tdBlockDetailLayout.layoutParams = todoLayoutParams
                                        tdBlockDetailParentLayout.addView(tdBlockDetailLayout) // 일정 블록 추가

                                        // DB에서 해당 일정의 완료여부 가져오기
                                        if(item_for_todo.end_check == 1){ // 완료 여부가 1인 경우
                                            tdBlockDetailLayout.findViewById<CheckBox>(R.id.todo_check_detail).isChecked = true // 체크박스 체크
                                            endCheckCount = endCheckCount +1
                                        }

                                        // 체크박스 체크 이벤트
                                        tdBlockDetailLayout.findViewById<CheckBox>(R.id.todo_check_detail).setOnCheckedChangeListener { buttonView, isChecked ->
                                            if(isChecked){ // 체크박스가 체크된 경우
                                                Thread{
                                                    projectDao?.editTodoCheck(item_for_todo.todo_code, 1) // DB의 완료값 1로 변경
                                                }.start()
                                            }else{ // 체크 해제 된 경우
                                                Thread{
                                                    projectDao?.editTodoCheck(item_for_todo.todo_code, 0) // DB의 완료값 0으로 변경
                                                }.start()
                                            }
                                        }
                                    }
                                }
                                if(todoCount != 0){
                                    result = endCheckCount.toDouble()/todoCount.toDouble()*100 // 목표별 진행률 계산
                                    progressList.add(result.toInt()) // 결과값 목록에 추가
                                }else{
                                    progressList.add(0) // 0을 목록에 추가
                                }

                                // onResume() 내부 호출의 경우 progressList가 중복되지 않으므로 " / 2 "를 제거하고 계산
                                if(progressList.size.equals(itemTarget.size)){
                                    val progress = progressList.sum()/itemTarget.size
                                    updatePJProgress(projectCode_detail, progress)
                                }else{
                                    // 프로젝트 진행률 구하여 프로그레스바와 퍼센트에 출력 (알고리즘 : 진행률합 / 2(Thread 때문에 중복되므로) / 목표 개수 )
                                    val progress = progressList.sum()/2/itemTarget.size
                                    updatePJProgress(projectCode_detail, progress)
                                }
                            }
                        }.start()
                    }
                }
            }
        }.start()
    }

    // 프로젝트 진행률 DB값 업데이트
    fun updatePJProgress(projectCode: Int, progress: Int){
        Thread{
            val database = AppDatabase.getInstance(this)
            val projectDao = database?.projectDAO()
            // 프로젝트 테이블에서 프로젝트의 진행률 값 업데이트
            projectDao?.editPJProgress(projectCode, progress)
        }.start()
    }
}