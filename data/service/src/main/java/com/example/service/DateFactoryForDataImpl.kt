package com.example.service

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateFactoryForDataImpl @Inject constructor(): DateFactoryForData{

    override fun returnToday(): Long {
        return Calendar.getInstance().timeInMillis
    }

    override fun returnRightBeforeFixedTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.apply {
            add(Calendar.HOUR_OF_DAY, -1)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    override fun returnTheDayStart(fromDay: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.apply {
            add(Calendar.DATE, -fromDay)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    override fun returnTheDayEnd(endMilli: Long): Long{
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(endMilli)
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }

    override fun returnTheHour(milliSecDate: Long): Int{
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSecDate)
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    override fun returnTheDate(milliSecDate: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSecDate)
        return calendar
    }

    override fun returnStringDate(milliSecDate: Long): String{
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSecDate)
        return SimpleDateFormat("yyyyMMdd").format(calendar.time)
    }

    override fun returnDayOfWeek(milliSecDate: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSecDate)
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    override fun returnDayOfMonth(milliSecDate: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSecDate)
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    override fun returnLastMonthStart(): Long{
        val calendar = Calendar.getInstance()
        calendar.apply {
            add(Calendar.MONTH, -1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    override fun returnLastMonthEnd(): Long{
        val calendar = Calendar.getInstance()
        calendar.apply {
            add(Calendar.MONTH, 0)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis - 1
    }

    override fun returnLastMonthEndDate(milliSecDate: Long): Int{
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSecDate)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    override fun returnWeekStartFrom(numberAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.apply {
            add(Calendar.WEEK_OF_YEAR, -numberAgo)
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)      // Calendar의 주의 시작은 일요일
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    override fun returnWeekEndFrom(numberAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.apply {
            add(Calendar.WEEK_OF_YEAR, -numberAgo + 1)
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis - 1
    }

    override fun returnMonthEndFrom(numberAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.apply {
            add(Calendar.MONTH, -numberAgo + 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis - 1
    }

    override fun returnMonthStartFrom(numberAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.apply {
            add(Calendar.MONTH, -numberAgo)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    override fun getIncludedHourlyMark(startMillis: Long, endMillis: Long): Long? {
        val calendar = Calendar.getInstance()

        // 두 밀리초 사이가 한시간 이내 인지 확인
        if (endMillis - startMillis >= 3600000L) { // 3600000 milliseconds = 1 hour
            return null // 한시간 이외
        }

        // 인스턴스의 시작 밀리초 설정
        calendar.timeInMillis = startMillis

        // Move to the next full hour if necessary
        if (calendar.get(Calendar.MINUTE) != 0 || calendar.get(Calendar.SECOND) != 0 || calendar.get(Calendar.MILLISECOND) != 0) {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }

        // If the calculated next full hour is within the end time, return it
        if (calendar.timeInMillis <= endMillis) {
            return calendar.timeInMillis
        }

        return null // No full hour mark within the given timespan
    }

    override fun calculateDayPassed(stringDate: String): Int {
        // 입력된 날짜 형식에 맞는 SimpleDateFormat 생성
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        // 오늘 날짜
        val today = Calendar.getInstance()

        // 입력된 날짜
        val inputDate = Calendar.getInstance().apply {
            time = dateFormat.parse(stringDate) ?: throw IllegalArgumentException("Invalid date format")
        }

        // 오늘 날짜와 입력된 날짜의 차이를 계산
        val differenceInMillis = today.timeInMillis - inputDate.timeInMillis
        val differenceInDays = differenceInMillis / (24 * 60 * 60 * 1000)

        return differenceInDays.toInt()
    }
}
