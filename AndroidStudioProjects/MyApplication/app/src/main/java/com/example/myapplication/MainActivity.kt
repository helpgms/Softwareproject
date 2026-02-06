package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView


class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var addDiaryButton: Button
    private lateinit var addScheduleButton: Button
    private lateinit var scheduleTextView: TextView
    private var selectedDate: CalendarDay? = null
    private val schedulePrefs = "SchedulePrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.MaterialCalendarView)
        addDiaryButton = findViewById(R.id.addDiaryButton)
        addScheduleButton = findViewById(R.id.addScheduleButton)
        scheduleTextView = findViewById(R.id.scheduleTextView)

        // 날짜 선택 리스너
        calendarView.setOnDateChangedListener { _, date, _ ->
            selectedDate = date
            displayScheduleForSelectedDate(date)
        }

        addDiaryButton.setOnClickListener {
            val intent = Intent(this, ViewDiaryActivity::class.java)
            intent.putExtra("selectedDate", selectedDate?.toString() ?: "")
            startActivity(intent)
        }

        addScheduleButton.setOnClickListener {
            // selectedDate가 null일 경우 기본 문자열을 전달하여 예외 방지
            val intent = Intent(this, ScheduleActivity::class.java)
            intent.putExtra("selectedDate", selectedDate?.let { "${it.year}-${it.month + 1}-${it.day}" } ?: "")
            startActivity(intent)
        }

        updateCalendarWithSchedules()
    }

    private fun displayScheduleForSelectedDate(date: CalendarDay) {
        val sharedPreferences = getSharedPreferences(schedulePrefs, MODE_PRIVATE)
        val dateKey = "${date.year}-${date.month + 1}-${date.day}"
        val schedule = sharedPreferences.getString("${dateKey}_schedule", "등록된 일정이 없습니다.")

        scheduleTextView.text = schedule
        scheduleTextView.setTextColor(Color.BLACK)
        scheduleTextView.visibility = View.VISIBLE
    }

    private fun updateCalendarWithSchedules() {
        val sharedPreferences = getSharedPreferences(schedulePrefs, MODE_PRIVATE)

        // 일정이 있는 날짜에 대한 마커 표시
        val markedDates = mutableSetOf<CalendarDay>()
        sharedPreferences.all.keys.forEach { key ->
            if (key.contains("_schedule")) {
                val parts = key.split("_")[0].split("-")
                if (parts.size == 3) {
                    val year = parts[0].toIntOrNull()
                    val month = parts[1].toIntOrNull()
                    val day = parts[2].toIntOrNull()
                    if (year != null && month != null && day != null) {
                        markedDates.add(CalendarDay.from(year, month - 1, day)) // month는 0-based
                    }
                }
            }
        }

        if (markedDates.isNotEmpty()) {
            calendarView.addDecorator(EventDecorator(Color.RED, markedDates))
        }
    }

    override fun onResume() {
        super.onResume()
        updateCalendarWithSchedules()
        selectedDate?.let { displayScheduleForSelectedDate(it) }
    }
}