package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay


class ScheduleActivity : AppCompatActivity() {

    private lateinit var scheduleEditText: EditText
    private lateinit var saveScheduleButton: Button
    private var selectedDate: CalendarDay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        scheduleEditText = findViewById(R.id.scheduleEditText)
        saveScheduleButton = findViewById(R.id.saveScheduleButton)

        val dateString = intent.getStringExtra("selectedDate")

        // dateString이 비어 있거나 잘못된 형식일 때 예외 처리
        if (!dateString.isNullOrBlank()) {
            val parts = dateString.split("-").mapNotNull { it.toIntOrNull() }
            if (parts.size == 3) {
                selectedDate = CalendarDay.from(parts[0], parts[1] - 1, parts[2]) // month는 0-based
            } else {
                Toast.makeText(this, "잘못된 날짜 형식입니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "날짜 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        loadSchedule()

        saveScheduleButton.setOnClickListener {
            saveSchedule()
        }
    }

    private fun loadSchedule() {
        val sharedPreferences = getSharedPreferences("SchedulePrefs", MODE_PRIVATE)
        val dateKey = "${selectedDate?.year}-${selectedDate?.month?.plus(1)}-${selectedDate?.day}"
        val existingSchedule = sharedPreferences.getString("${dateKey}_schedule", "")
        scheduleEditText.setText(existingSchedule)
    }

    private fun saveSchedule() {
        val sharedPreferences = getSharedPreferences("SchedulePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val dateKey = "${selectedDate?.year}-${selectedDate?.month?.plus(1)}-${selectedDate?.day}"
        editor.putString("${dateKey}_schedule", scheduleEditText.text.toString())
        editor.apply()

        Toast.makeText(this, "일정이 저장되었습니다", Toast.LENGTH_SHORT).show()
        finish()
    }
}