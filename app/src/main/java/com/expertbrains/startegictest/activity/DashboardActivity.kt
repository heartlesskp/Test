package com.expertbrains.startegictest.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.expertbrains.startegictest.R
import com.expertbrains.startegictest.adapter.PersonAdapter
import com.expertbrains.startegictest.base.BaseActivity
import com.expertbrains.startegictest.database.testtable.TestTable
import com.expertbrains.startegictest.extra.Constant
import com.expertbrains.startegictest.widgets.CustomDialog
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
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
        adapter.onItemClick = { item, isDelete ->
            if (isDelete) showConfirmDialog(item) else showProfileDialog(item)
        }
        fabAdd.setOnClickListener {
            startActivity(
                Intent(this, AddPersonActivity::class.java)
                    .putExtra(Constant.IS_EDIT, false)
            )
        }

        database.getTestTableDao().getAllUserData()
            .observe(this,
                Observer<List<TestTable>?> { item ->
                    item?.let {
                        val listToArray: ArrayList<TestTable> = ArrayList(it.size)
                        listToArray.addAll(it)
                        adapter.addData(listToArray)
                    }
                })
    }

    private fun showConfirmDialog(item: TestTable) {
        CustomDialog.showDialog(
            this,
            getString(R.string.are_you_sure),
            getString(R.string.delete_this_detail),
            true,
            getString(R.string.ok),
            getString(R.string.cancel),
            onClickCallBack = object : CustomDialog.OnClickCallback {
                override fun positiveOnclick() {
                    Observable.fromCallable { database.getTestTableDao().deleteUserData(item.id) }
                        .subscribeOn(Schedulers.io()).subscribe()
                }

                override fun negativeOnClick() {
                }
            })
    }

    private fun showProfileDialog(item: TestTable) {
        val builder = AlertDialog.Builder(this)
        val dialog: AlertDialog
        builder.setCancelable(true)
        val view = layoutInflater.inflate(R.layout.lay_profile_view, null)
        builder.setView(view)
        val tvName = view.findViewById(R.id.tvPersonName) as AppCompatTextView
        val tvOrganization = view.findViewById(R.id.tvOrganization) as AppCompatTextView
        val tvDesignation = view.findViewById(R.id.tvDesignation) as AppCompatTextView
        val tvBirthDate = view.findViewById(R.id.tvBirthDate) as AppCompatTextView
        val tvAddress = view.findViewById(R.id.tvAddress) as AppCompatTextView
        val btnViewLocation = view.findViewById(R.id.btnViewLocation) as AppCompatButton
        val btnEdit = view.findViewById(R.id.btnEdit) as AppCompatButton
        val btnDelete = view.findViewById(R.id.btnDelete) as AppCompatButton

        dialog = builder.create()
        dialog.show()

        tvName.text =
            getString(R.string.persons).plus(" ").plus(item.firstName).plus(" ").plus(item.lastName)
        tvOrganization.text = item.organization
        tvDesignation.text = item.designation
        tvBirthDate.text = item.dateOfBirth
        tvAddress.text = "${item.country}, ${item.state}, ${item.city}"

        btnDelete.setOnClickListener { showConfirmDialog(item) }
        btnEdit.setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(this, AddPersonActivity::class.java)
                    .putExtra(Constant.IS_EDIT, true)
                    .putExtra(Constant.USER_ITEM, item)
            )
        }


    }
}