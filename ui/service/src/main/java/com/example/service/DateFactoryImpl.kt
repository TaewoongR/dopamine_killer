package com.example.service

import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateFactoryImpl @Inject constructor(): DateFactory{

     override suspend fun returnTheDayStart(fromDay: Int): Long {
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

    override suspend fun returnTheDayEnd(endMilli: Long): Long{
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

    override suspend fun returnTheHour(milliSecDate: Long): Int{
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSecDate)
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    override suspend fun returnTheDate(milliSecDate: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSecDate)
        return calendar
    }

    override suspend fun returnStringDate(milliSecDate: Long): String{
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSecDate)
        return SimpleDateFormat("yyyyMMdd").format(calendar.time)
    }
}
