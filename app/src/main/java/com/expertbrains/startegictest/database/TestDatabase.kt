package com.expertbrains.startegictest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.expertbrains.startegictest.R
import com.expertbrains.startegictest.database.testtable.TestTable
import com.expertbrains.startegictest.database.testtable.TestTableDao

@Database(entities = [TestTable::class], exportSchema = false, version = 1)
abstract class TestDatabase : RoomDatabase() {
    companion object {
        var instance: TestDatabase? = null
        fun getDatabase(context: Context): TestDatabase {
            synchronized(TestDatabase::class.java) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        TestDatabase::class.java,
                        context.getString(R.string.app_name)
                    ).build()
                }
            }
            return instance!!
        }
    }

    abstract fun getTestTableDao(): TestTableDao
}