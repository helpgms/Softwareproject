package com.example.myapplication__1

import android.text.style.ForegroundColorSpan
import com.google.android.material.datepicker.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.CalendarDay

class EventDecorator(private val color: Int) : DayViewDecorator {

    private val dates = mutableSetOf<CalendarDay>()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(color)) // 날짜 텍스트 색상을 설정
    }

    // 특정 날짜를 추가
    fun addDate(date: CalendarDay) {
        dates.add(date)
    }

    // 특정 날짜를 제거
    fun removeDate(date: CalendarDay) {
        dates.remove(date)
    }

    // 날짜 목록을 설정
    fun setDates(newDates: Set<CalendarDay>) {
        dates.clear()
        dates.addAll(newDates)
    }
}