package com.expertbrains.startegictest.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.expertbrains.startegictest.R
import com.expertbrains.startegictest.adapter.PersonAdapter
import com.expertbrains.startegictest.base.BaseActivity
import com.expertbrains.startegictest.database.testtable.TestTable
import com.expertbrains.startegictest.extra.Constant
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        init()
    }

    private fun init() {
        bindToolbar()
        displayHomeButton(true)
        toolbarTitle = getString(R.string.persons)
        val adapter = PersonAdapter()
        rvPersonList.layoutManager = GridLayoutManager(this, 2)
        rvPersonList.adapter = adapter

        val alData = ArrayList<TestTable>()
        adapter.addData(alData)
        adapter.onItemClick = {
            if (it) makeToast("kp deleted") else makeToast("kp msg")
        }
        fabAdd.setOnClickListener {
            startActivity(
                Intent(this, AddPersonActivity::class.java)
                    .putExtra(Constant.IS_EDIT, false)
            )
        }
    }
}