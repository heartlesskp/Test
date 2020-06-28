package com.expertbrains.startegictest.activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expertbrains.startegictest.R
import com.expertbrains.startegictest.adapter.CountryDropDownAdapter
import com.expertbrains.startegictest.base.BaseActivity
import com.expertbrains.startegictest.extra.Constant
import com.expertbrains.startegictest.widgets.CustomDialog
import com.expertbrains.startegictest.widgets.Utility
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_person.*
import java.util.*


class AddPersonActivity : BaseActivity() {

    private var isEdit: Boolean = false
    private var popupWindow: PopupWindow? = null

    companion object {
        const val selectImageCode = 1110
        const val takeImageCode = 1111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_person)
        init()
    }

    private fun init() {
        bindToolbar()
        displayHomeButton(true)

        isEdit = intent.getBooleanExtra(Constant.IS_EDIT, false)

        ivProfile.setOnClickListener { checkPermission() }
        tvDateOfBirth.setOnClickListener { openDatePickerDialog() }
        btnCancel.setOnClickListener { finish() }
        tvCountry.setOnClickListener {
            popupWindow?.dismiss()
            if (popupWindow == null)
                provideCountryPopupWindow(it)
            popupWindow!!.showAsDropDown(it, 0, -it.height)
        }
        tvPersonList.setOnClickListener {
            popupWindow?.dismiss()
            if (popupWindow == null)
                provideCountryPopupWindow(it)
            popupWindow!!.showAsDropDown(it, 0, -it.height)
        }
        tvPersonList.visibility = if (isEdit) View.VISIBLE else View.GONE
    }

    private fun checkPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        selectImage()
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        askPermissionDenyDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun askPermissionDenyDialog() {
        CustomDialog.showDialog(
            this,
            getString(R.string.permission_title),
            getString(R.string.permission_msg),
            false,
            getString(R.string.setting),
            getString(R.string.cancel),
            false, object : CustomDialog.OnClickCallback {
                override fun positiveOnclick() {
                    openPermissionDialog()
                }

                override fun negativeOnClick() {}
            }
        )
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>(
            getString(R.string.take_photo),
            getString(R.string.new_profile_photo),
            getString(R.string.remove_profile_photo)
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set Profile Photo")
        builder.setItems(options) { _, item ->
            when (item) {
                0 -> {
                    val takePicture =
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, takeImageCode)
                }
                1 -> {
                    startActivityForResult(
                        Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        ), selectImageCode
                    )
                }
                2 -> {
                    ivProfile.setImageResource(R.drawable.logo)
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                selectImageCode -> {
                    val contentURL = data.data
                    contentURL?.let {
                        try {
                            val selectedBitmap =
                                BitmapFactory.decodeStream(contentResolver.openInputStream(it))
                            val encodedString = Utility.getEncodeBitmapString(selectedBitmap)
                            ivProfile.setImageURI(contentURL)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                takeImageCode -> {
                    try {
                        val bitmap = data.extras?.get("data") as Bitmap
                        val encodedString = Utility.getEncodeBitmapString(bitmap)

                        val decodedString: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)

                        val decodedByte =
                            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        ivProfile.setImageBitmap(decodedByte)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun openDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            tvDateOfBirth.text = i3.toString().plus("/${i2 + 1}").plus("/${i}")
        }
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog: DatePickerDialog?
        val pastCalendar = Calendar.getInstance()
        pastCalendar.add(Calendar.YEAR, -10)
        datePickerDialog = DatePickerDialog(this, dateSetListener, year, month, day)
        datePickerDialog.datePicker.minDate = pastCalendar.timeInMillis
        datePickerDialog.datePicker.maxDate = calender.timeInMillis
        datePickerDialog.show()
    }

    private fun provideCountryPopupWindow(it: View) {
        popupWindow = PopupWindow(it.width, ViewGroup.LayoutParams.WRAP_CONTENT)
            .apply {

                isOutsideTouchable = true
                val rvListView = layoutInflater.inflate(
                    R.layout.lay_dropdown,
                    null,
                    false
                ) as RecyclerView
                val adapter = CountryDropDownAdapter()
                rvListView.layoutManager = LinearLayoutManager(this@AddPersonActivity)
                rvListView.adapter = adapter
                adapter.onItemClick = {
                    popupWindow?.dismiss()
                }
                contentView = rvListView
            }
    }
}