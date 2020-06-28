package com.expertbrains.startegictest.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.expertbrains.startegictest.R
import com.expertbrains.startegictest.database.TestDatabase

open class BaseActivity : AppCompatActivity() {

    private lateinit var mContext: Context
    lateinit var toolbar: Toolbar
    lateinit var actionBar: ActionBar
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
    }

    fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionBar = this.supportActionBar!!
    }

    var toolbarTitle: String
        get() = toolbar.title.toString()
        set(value) {
            toolbar.post {
                toolbar.title = value
            }
        }

    fun makeToast(message: String) {
        if (toast != null)
            toast?.cancel()
        toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    val database: TestDatabase
        get() = TestDatabase.getDatabase(mContext)

    fun displayHomeButton(isDisplay: Boolean) {
        actionBar.setDisplayHomeAsUpEnabled(isDisplay)
        // actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    fun openPermissionDialog() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}