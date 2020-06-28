package com.expertbrains.startegictest.database.testtable

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

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

    @Query("UPDATE test_table SET first_name = :firstName, last_name = :lastName, organization= :organization, designation=:designation, date_of_birth= :dateOfBirth, profile_url=:profileUrl, country=:country, state=:state, city=:city, lat = :lat, lng =:lng WHERE id=:id")
    fun updateUserData(
        firstName: String, lastName: String, organization: String,
        designation: String, dateOfBirth: String, country: String, state: String, city: String,
        profileUrl: String, lat: Double, lng: Double, id: Int
    ): Int
}