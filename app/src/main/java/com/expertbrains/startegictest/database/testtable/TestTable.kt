package com.expertbrains.startegictest.database.testtable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_table")
class TestTable {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "google_id")
    var googleId = ""

    @ColumnInfo(name = "first_name")
    var firstName = ""

    @ColumnInfo(name = "last_name")
    var lastName = ""

    @ColumnInfo(name = "designation")
    var designation = ""

    @ColumnInfo(name = "profile_url")
    var profileUrl = ""

    @ColumnInfo(name = "organization")
    var organization = ""

    @ColumnInfo(name = "date_of_birth")
    var dateOfBirth = ""

    @ColumnInfo(name = "country")
    var country = ""

    @ColumnInfo(name = "state")
    var state = ""

    @ColumnInfo(name = "city")
    var city = ""


}