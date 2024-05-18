package com.example.service

import java.util.Calendar

interface DateFactoryForData{
    fun returnToday(): Long
    fun returnRightBeforeFixedTime(): Long
    fun returnTheDayStart(fromDay: Int): Long
    fun returnTheDayEnd(endMilli: Long): Long
    fun returnTheHour(milliSecDate: Long): Int
    fun returnTheDate(milliSecDate: Long): Calendar
    fun returnStringDate(milliSecDate: Long): String
    fun returnDayOfWeek(milliSecDate: Long): Int
    fun returnDayOfMonth(milliSecDate: Long): Int
    fun returnLastMonthStart(): Long
    fun returnLastMonthEnd(): Long
    fun returnLastMonthEndDate(milliSecDate: Long): Int
    fun returnWeekStartFrom(numberAgo: Int): Long
    fun returnWeekEndFrom(numberAgo: Int): Long
    fun returnMonthStartFrom(numberAgo: Int): Long
    fun returnMonthEndFrom(numberAgo: Int): Long
    fun getIncludedHourlyMark(startMillis: Long, endMillis: Long): Long?
}