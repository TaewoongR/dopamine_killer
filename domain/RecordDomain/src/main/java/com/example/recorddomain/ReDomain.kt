package com.example.recorddomain

interface ReDomain{
    suspend fun getRecordList(): List<RecordDataDomain>
}