package com.example.service

import java.util.Calendar

interface DateFactory{
    fun returnToday(): Long
    fun returnTheDayStart(fromDay: Int): Long
    fun returnTheDayEnd(endMilli: Long): Long
    fun returnTheHour(milliSecDate: Long): Int
    fun returnTheDate(milliSecDate: Long): Calendar
    fun returnStringDate(milliSecDate: Long): String
    fun returnDayOfWeek(milliSecDate: Long): Int
}