package com.example.service

import java.util.Calendar

interface DateFactory{
    suspend fun returnTheDayStart(fromDay: Int): Long
    suspend fun returnTheDayEnd(endMilli: Long): Long
    suspend fun returnTheHour(milliSecDate: Long): Int
    suspend fun returnTheDate(milliSecDate: Long): Calendar
    suspend fun returnStringDate(milliSecDate: Long): String
}