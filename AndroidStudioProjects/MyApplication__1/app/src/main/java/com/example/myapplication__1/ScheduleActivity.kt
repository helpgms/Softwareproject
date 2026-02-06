package com.example.myapplication__1

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Calendar
import android.graphics.Color

class ScheduleActivity : AppCompatActivity() {

    private lateinit var scheduleEditText: EditText
    private lateinit var saveScheduleButton: Button
    private lateinit var deleteScheduleButton: Button
    private var selectedDate: CalendarDay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        scheduleEditText = findViewById(R.id.scheduleEditText)
        saveScheduleButton = findViewById(R.id.saveScheduleButton)
        deleteScheduleButton = findViewById(R.id.deleteScheduleButton)

        requestNotificationPermission() // Android 13 이상에서 알림 권한 요청
        checkExactAlarmPermission() // 정확한 알람 권한 확인 및 요청

        val dateString = intent.getStringExtra("selectedDate")
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

        deleteScheduleButton.setOnClickListener {
            deleteSchedule()
        }
        saveScheduleButton.setBackgroundColor(Color.LTGRAY) // 저장 버튼 회색
        saveScheduleButton.setTextColor(Color.BLACK) // 텍스트 색

        deleteScheduleButton.setBackgroundColor(Color.LTGRAY) // 삭제 버튼 회색
        deleteScheduleButton.setTextColor(Color.BLACK) // 텍스트 색
    }

    // Android 13 이상에서 알림 권한 요청
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }
    }

    // 정확한 알람 권한 확인 및 요청
    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
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
        val scheduleText = scheduleEditText.text.toString()

        editor.putString("${dateKey}_schedule", scheduleText)
        editor.apply()

        selectedDate?.let {
            if (isToday(it)) {
                // 오늘 날짜의 일정인 경우 알림 즉시 표시
                showImmediateNotification(it, scheduleText)
            } else {
                // 미래 날짜의 일정은 알람으로 설정
                setAlarm(it, scheduleText)
            }
        }

        Toast.makeText(this, "일정이 저장되었습니다", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun deleteSchedule() {
        val sharedPreferences = getSharedPreferences("SchedulePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val dateKey = "${selectedDate?.year}-${selectedDate?.month?.plus(1)}-${selectedDate?.day}"

        if (sharedPreferences.contains("${dateKey}_schedule")) {
            editor.remove("${dateKey}_schedule") // 일정 삭제
            editor.apply()

            scheduleEditText.setText("") // EditText 초기화
            Toast.makeText(this, "일정이 삭제되었습니다", Toast.LENGTH_SHORT).show()

            // 알람 취소
            selectedDate?.let { cancelAlarm(it) }

            // 삭제 성공 결과를 MainActivity로 전달
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "삭제할 일정이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAlarm(date: CalendarDay, scheduleText: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, date.year)
            set(Calendar.MONTH, date.month) // month는 0-based
            set(Calendar.DAY_OF_MONTH, date.day)
            set(Calendar.HOUR_OF_DAY, 0) // 자정 이후
            set(Calendar.MINUTE, 1) // 00시 01분
            set(Calendar.SECOND, 0)
        }

        val now = Calendar.getInstance()

        // 알람 시간이 현재 시간보다 이전이면, 현재 시간에서 1분 뒤로 설정
        if (calendar.before(now)) {
            calendar.timeInMillis = now.timeInMillis
            calendar.add(Calendar.MINUTE, 1) // 현재 시간에서 1분 뒤로 설정
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("event_name", scheduleText) // 알림 메시지 전달
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            date.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 로그 추가
        Toast.makeText(this, "알람 설정 시간: ${calendar.time}", Toast.LENGTH_SHORT).show()
        Log.d("setAlarm", "알람 설정 시간: ${calendar.timeInMillis}")
        Log.d("setAlarm", "알람 텍스트: $scheduleText")

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun cancelAlarm(date: CalendarDay) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            date.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun isToday(date: CalendarDay): Boolean {
        val today = Calendar.getInstance()
        return date.year == today.get(Calendar.YEAR) &&
                date.month == today.get(Calendar.MONTH) &&
                date.day == today.get(Calendar.DAY_OF_MONTH)
    }

    private fun showImmediateNotification(date: CalendarDay, scheduleText: String) {
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("event_name", scheduleText)
        }

        sendBroadcast(intent) // BroadcastReceiver 호출하여 알림 표시
    }
}