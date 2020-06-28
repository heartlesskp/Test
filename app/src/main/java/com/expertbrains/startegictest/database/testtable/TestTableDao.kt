package com.expertbrains.startegictest.database.testtable

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TestTableDao {
    @Insert
    fun insertAccountData(testTable: TestTable)

    @Query("SELECT * FROM test_table")
    fun getAllUserData(): LiveData<List<TestTable>>

    @Query("DELETE FROM test_table WHERE id = :id")
    fun deleteUserData(id: Int)

    @Query("SELECT * FROM test_table WHERE id= :id")
    fun getSelectedUserData(id: Int): LiveData<TestTable>

    @Query("SELECT *  FROM test_table")
    fun getAllEditUser(): List<TestTable>

    @Update
    fun updateUserData(testTable: TestTable): Int
}