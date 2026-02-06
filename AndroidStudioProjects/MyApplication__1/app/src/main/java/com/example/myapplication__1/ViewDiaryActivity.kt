package com.example.myapplication__1

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import android.graphics.Color

class ViewDiaryActivity : AppCompatActivity() {

    private lateinit var diaryImageView: ImageView
    private lateinit var diaryTextView: TextView
    private lateinit var editDiaryButton: Button
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_diary)

        diaryImageView = findViewById(R.id.diaryImageView)
        diaryTextView = findViewById(R.id.diaryTextView)
        editDiaryButton = findViewById(R.id.editDiaryButton)

        selectedDate = intent.getStringExtra("selectedDate")
        loadDiary(selectedDate)

        editDiaryButton.setOnClickListener {
            val intent = Intent(this, DiaryActivity::class.java)
            intent.putExtra("selectedDate", selectedDate)
            startActivity(intent)
            finish()
        }
        editDiaryButton.setBackgroundColor(Color.LTGRAY) // 수정 버튼 회색
        editDiaryButton.setTextColor(Color.BLACK) // 텍스트 색
    }

    private fun loadDiary(date: String?) {
        val sharedPreferences = getSharedPreferences("DiaryPrefs", MODE_PRIVATE)

        val diaryContent = sharedPreferences.getString("${date}_text", "일기가 없습니다.")
        val imagePath = sharedPreferences.getString("${date}_imagePath", null)

        diaryTextView.text = diaryContent

        if (imagePath != null) {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                diaryImageView.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            diaryImageView.setImageResource(0)
        }
    }
}