package com.expertbrains.startegictest.database.testtable

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface TestTableDao {
    @Insert
    fun insertAccountData(testTable: TestTable)
}