package com.example.myapplication__1

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import android.text.style.ForegroundColorSpan

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var addDiaryButton: Button
    private lateinit var addScheduleButton: Button
    private lateinit var scheduleTextView: TextView
    private lateinit var darkModeSwitch: Switch
    private lateinit var feelingsButton: Button
    private lateinit var feelingTextView: TextView // 기분 표시 TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedDate: CalendarDay? = null
    private val schedulePrefs = "SchedulePrefs"
    private val feelingsPrefs = "FeelingsPrefs"
    private lateinit var eventDecorator: EventDecorator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)

        setContentView(R.layout.activity_main)

        // 뷰 초기화
        calendarView = findViewById(R.id.calendarView)
        addDiaryButton = findViewById(R.id.addDiaryButton)
        addScheduleButton = findViewById(R.id.addScheduleButton)
        scheduleTextView = findViewById(R.id.scheduleTextView)
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        feelingsButton = findViewById(R.id.feelingsButton)
        feelingTextView = findViewById(R.id.feelingTextView)

        // EventDecorator 초기화
        eventDecorator = EventDecorator(Color.RED)
        calendarView.addDecorator(eventDecorator)

        // 다크 모드 적용
        applyDarkMode()

        // 다크 모드 Switch 상태 설정
        darkModeSwitch.isChecked = sharedPreferences.getBoolean("DarkMode", false)

        // 다크 모드 전환 리스너 설정
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveDarkModeSetting(isChecked)
            applyDarkMode()
        }

        // 날짜 선택 리스너
        calendarView.setOnDateChangedListener { _, date, _ ->
            selectedDate = date
            displayFeelingsForDate(date)
            displayScheduleForSelectedDate(date)
        }

        addDiaryButton.setOnClickListener {
            val intent = Intent(this, ViewDiaryActivity::class.java)
            intent.putExtra("selectedDate", selectedDate?.toString() ?: "")
            startActivity(intent)
        }

        addScheduleButton.setOnClickListener {
            val intent = Intent(this, ScheduleActivity::class.java)
            intent.putExtra("selectedDate", selectedDate?.let { "${it.year}-${it.month + 1}-${it.day}" } ?: "")
            startActivityForResult(intent, 100)
        }

        feelingsButton.setOnClickListener {
            showFeelingsDialog()
        }

        updateCalendarWithSchedules()
    }

    private fun showFeelingsDialog() {
        val feelingsArray = arrayOf("😊", "😢", "😡", "😴", "😎","\uD83E\uDD7A","❤️","💔","취소")
        AlertDialog.Builder(this)
            .setTitle("오늘의 기분을 선택하세요")
            .setItems(feelingsArray) { _, which ->
                val selectedFeeling = feelingsArray[which]
                if(selectedFeeling == "취소"){
                    saveFeelings(selectedDate, " ")
                }else{
                    saveFeelings(selectedDate, selectedFeeling)
                }
                Toast.makeText(this, "오늘의 기분이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun saveFeelings(date: CalendarDay?, feeling: String) {
        if (date != null) {
            val sharedPreferences = getSharedPreferences(feelingsPrefs, MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val dateKey = "${date.year}-${date.month + 1}-${date.day}"
            editor.putString(dateKey, feeling)
            editor.apply()
        }
    }

    private fun displayFeelingsForDate(date: CalendarDay) {
        val sharedPreferences = getSharedPreferences(feelingsPrefs, MODE_PRIVATE)
        val dateKey = "${date.year}-${date.month + 1}-${date.day}"
        val feeling = sharedPreferences.getString(dateKey, null)

        if (feeling != null) {
            feelingTextView.text = feeling
            feelingTextView.visibility = TextView.VISIBLE
        } else {
            feelingTextView.text = ""
            feelingTextView.visibility = TextView.GONE
        }
    }

    private fun displayScheduleForSelectedDate(date: CalendarDay) {
        val sharedPreferences = getSharedPreferences(schedulePrefs, MODE_PRIVATE)
        val dateKey = "${date.year}-${date.month + 1}-${date.day}"
        val schedule = sharedPreferences.getString("${dateKey}_schedule", "등록된 일정이 없습니다.")

        scheduleTextView.text = schedule
        scheduleTextView.setTextColor(Color.BLACK)
        scheduleTextView.visibility = TextView.VISIBLE
    }

    private fun updateCalendarWithSchedules() {
        val sharedPreferences = getSharedPreferences(schedulePrefs, MODE_PRIVATE)

        val markedDates = mutableSetOf<CalendarDay>()
        sharedPreferences.all.keys.forEach { key ->
            if (key.contains("_schedule")) {
                val parts = key.split("_")[0].split("-")
                if (parts.size == 3) {
                    val year = parts[0].toIntOrNull()
                    val month = parts[1].toIntOrNull()
                    val day = parts[2].toIntOrNull()
                    if (year != null && month != null && day != null) {
                        markedDates.add(CalendarDay.from(year, month - 1, day))
                    }
                }
            }
        }

        eventDecorator.setDates(markedDates) // 데코레이터 갱신
        calendarView.invalidateDecorators() // 변경사항 반영
    }

    override fun onResume() {
        super.onResume()
        updateCalendarWithSchedules()
        selectedDate?.let {
            displayFeelingsForDate(it)
            displayScheduleForSelectedDate(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            updateCalendarWithSchedules()
        }
    }

    private fun saveDarkModeSetting(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean("DarkMode", isDarkMode).apply()
    }

    private fun applyDarkMode() {
        val isDarkMode = sharedPreferences.getBoolean("DarkMode", false)
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)

        // 캘린더 날짜 색상 변경
        val textColor = if (isDarkMode) Color.WHITE else Color.BLACK
        calendarView.addDecorator(DefaultTextColorDecorator(textColor))
        calendarView.invalidateDecorators() // 변경 사항 적용

        // 버튼 색상 변경
        val buttonBackgroundColor = if (isDarkMode) Color.DKGRAY else Color.LTGRAY
        val buttonTextColor = if (isDarkMode) Color.WHITE else Color.BLACK

        addDiaryButton.setBackgroundColor(buttonBackgroundColor)
        addDiaryButton.setTextColor(buttonTextColor)
        addScheduleButton.setBackgroundColor(buttonBackgroundColor)
        addScheduleButton.setTextColor(buttonTextColor)
        feelingsButton.setBackgroundColor(buttonBackgroundColor)
        feelingsButton.setTextColor(buttonTextColor)
        darkModeSwitch.setTextColor(buttonTextColor) // 스위치 텍스트 색상 변경
    }
}

class DefaultTextColorDecorator(private val color: Int) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return true // 모든 날짜에 적용
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(color))
    }
}
