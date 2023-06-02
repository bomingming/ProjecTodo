package com.example.projectodo

import android.app.Activity
import android.app.AlertDialog
import android.app.Instrumentation.ActivityResult
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.projectodo.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {

    // binding 객체 미리 선언
    private lateinit var binding : FragmentBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 삭제 버튼 이벤트
        binding.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("프로젝트를 삭제하시겠습니까?").setPositiveButton("삭제", DialogInterface.OnClickListener{dialog, which ->

                //프로젝트 코드 받아오기
                val projectCode = arguments?.getInt("프로젝트 코드")?: 0 // getInt가 null 반환 시 0 할당하므로 Type mismatch 오류 해결

                // 프로젝트 삭제 구현
                deleteProjectFromDB(projectCode)

                // BottomSheet 종료
                dismiss()
                // 토스트 메시지로 삭제 완료 알림
                Toast.makeText(requireContext(), "프로젝트가 삭제되었습니다",Toast.LENGTH_SHORT).show()
            }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  })
            builder.show()
        }

        // 수정 버튼 이벤트
        binding.editBtn.setOnClickListener {
            val intent : Intent = Intent(requireContext(), EditActivity::class.java)
            startActivity(intent)
            // BottomSheet는 종료
            dismiss()
        }
    }

    // DB에서 프로젝트를 삭제
    private fun deleteProjectFromDB(projectCode: Int){
        Thread{
            val database = AppDatabase.getInstance(requireContext())
            val projectDao = database?.projectDAO()
            projectDao?.deleteProject(projectCode)
        }.start()
    }

}