package com.example.projectodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectodo.databinding.ActivityDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 더보기 버튼 이벤트 처리
        binding.moreBtn.setOnClickListener {
            val bottomSheet = BottomSheetFragment()
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }
}