package com.expertbrains.startegictest.activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
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
import com.expertbrains.startegictest.adapter.PersonDropDownAdapter
import com.expertbrains.startegictest.base.BaseActivity
import com.expertbrains.startegictest.base.BaseRoomObserver
import com.expertbrains.startegictest.database.testtable.TestTable
import com.expertbrains.startegictest.extra.Constant
import com.expertbrains.startegictest.widgets.CountryRepository
import com.expertbrains.startegictest.widgets.CustomDialog
import com.expertbrains.startegictest.widgets.Utility
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_person.*
import java.util.*
import kotlin.Comparator


class AddPersonActivity : BaseActivity() {

    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private lateinit var item: TestTable
    private lateinit var alPersonName: ArrayList<TestTable>
    private lateinit var personAdapter: PersonDropDownAdapter
    private var isEdit: Boolean = false
    private var countryPopupWindow: PopupWindow? = null
    private var personPopupWindow: PopupWindow? = null
    private var encodedString: String = ""

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
        toolbarTitle =
            if (isEdit) getString(R.string.edit_person) else getString(R.string.add_person)

        if (isEdit) {
            item = intent.getSerializableExtra(Constant.USER_ITEM) as TestTable
            setEditUserData(item)
        }

        tvPersonList.visibility = if (isEdit) View.VISIBLE else View.GONE
        ivProfile.setOnClickListener { checkPermission() }
        tvDateOfBirth.setOnClickListener { openDatePickerDialog() }
        btnCancel.setOnClickListener { finish() }
        tvCountry.setOnClickListener { showCountryDialog(it) }
        tvPersonList.setOnClickListener { showPersonListDialog(it) }
        btnSave.setOnClickListener {
            if (checkValidation()) {
                saveDataInDatabase()
            } else makeToast("Please fill detail")
        }
        btnPickLocation.setOnClickListener {
            startActivityForResult(Intent(this, MapActivity::class.java), Constant.REQUEST_CODE)
        }

    }

    private fun setEditUserData(item: TestTable) {
        tvPersonList.text = item.firstName
        etFirstName.setText(item.firstName)
        etLastName.setText(item.lastName)
        etOrganization.setText(item.organization)
        etDesignation.setText(item.designation)
        etState.setText(item.state)
        etCity.setText(item.city)
        tvDateOfBirth.text = item.dateOfBirth
        tvCountry.text = item.country
        lat = item.lat
        lng = item.lng
        encodedString = item.profileUrl
        ivProfile.setImageBitmap(Utility.getDecodeStringBitmap(item.profileUrl))
        Observable.fromCallable {
            database.getTestTableDao().getAllEditUser()
        }.subscribeOn(Schedulers.io()).subscribe(object : BaseRoomObserver<List<TestTable>>() {
            override fun onSuccess(response: List<TestTable>) {
                alPersonName = ArrayList(response.size)
                alPersonName.addAll(response)
            }

            override fun onFail(errorMessage: Throwable) {
            }
        })
    }

    private fun saveDataInDatabase() {
        val table = TestTable()
        table.firstName = etFirstName.text.toString()
        table.lastName = etLastName.text.toString()
        table.organization = etOrganization.text.toString()
        table.designation = etDesignation.text.toString()
        table.dateOfBirth = tvDateOfBirth.text.toString()
        table.country = tvCountry.text.toString()
        table.state = etState.text.toString()
        table.city = etCity.text.toString()
        table.profileUrl = encodedString
        table.lat = lat
        table.lng = lng
        Observable.fromCallable {
            if (isEdit) database.getTestTableDao().updateUserData(
                table.firstName,
                table.lastName,
                table.organization,
                table.designation,
                table.dateOfBirth,
                table.country,
                table.state,
                table.city,
                table.profileUrl,
                table.lat,
                table.lng,
                item.id
            )
            else database.getTestTableDao().insertAccountData(table)
        }
            .subscribeOn(Schedulers.io()).subscribe(object : BaseRoomObserver<Any>() {
                override fun onSuccess(response: Any) {

                    finish()
                }

                override fun onFail(errorMessage: Throwable) {}
            })
    }

    private fun checkValidation(): Boolean {
        var isValid = true
        if (etFirstName.text.isNullOrEmpty())
            isValid = false
        if (etLastName.text.isNullOrEmpty())
            isValid = false
        if (etOrganization.text.isNullOrEmpty())
            isValid = false
        if (etDesignation.text.isNullOrEmpty())
            isValid = false
        if (tvDateOfBirth.text.isNullOrEmpty()
            || tvDateOfBirth.text.toString()
                .equals(getString(R.string.date_of_birth), ignoreCase = true)
        )
            isValid = false
        if (tvCountry.text.isNullOrEmpty()
            || tvCountry.text.toString()
                .equals(getString(R.string.select_country), ignoreCase = true)
        )
            isValid = false
        if (etState.text.isNullOrEmpty())
            isValid = false
        if (etCity.text.isNullOrEmpty())
            isValid = false
        if (encodedString.isBlank())
            isValid = false

        return isValid
    }

    private fun showPersonListDialog(it: View) {
        personPopupWindow?.dismiss()
        if (personPopupWindow == null)
            provideUserPopupWindow(it)
        personPopupWindow!!.showAsDropDown(it, 0, -it.height)
    }

    private fun showCountryDialog(it: View) {
        countryPopupWindow?.dismiss()
        if (countryPopupWindow == null)
            provideCountryPopupWindow(it)
        countryPopupWindow!!.showAsDropDown(it, 0, -it.height)
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
                    encodedString = ""
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
                            encodedString = Utility.getEncodeBitmapString(selectedBitmap)
                            ivProfile.setImageURI(contentURL)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                takeImageCode -> {
                    try {
                        val bitmap = data.extras?.get("data") as Bitmap
                        encodedString = Utility.getEncodeBitmapString(bitmap)

                        val decodedString: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)

                        val decodedByte =
                            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        ivProfile.setImageBitmap(decodedByte)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Constant.REQUEST_CODE -> {
                    lat = data.getDoubleExtra(Constant.LAT, 0.0)
                    lng = data.getDoubleExtra(Constant.LNG, 0.0)
                    val geoCoder = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address>?
                    try {
                        addresses = geoCoder.getFromLocation(lat, lng, 1)
                        if (addresses != null && !addresses.isEmpty()) {
                            tvCountry.text = addresses[0].countryName
                            etCity.setText(addresses[0].locality)
                            etState.setText(addresses[0].adminArea)
                        }
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
        countryPopupWindow = PopupWindow(it.width, ViewGroup.LayoutParams.WRAP_CONTENT)
            .apply {

                isOutsideTouchable = true
                val rvListView = layoutInflater.inflate(
                    R.layout.lay_dropdown,
                    null,
                    false
                ) as RecyclerView
                val adapter = CountryDropDownAdapter()
                val alData = CountryRepository.all
                alData.sortWith(Comparator { t, t2 ->
                    t.countryName.compareTo(t2.countryName)
                })
                adapter.addData(alData)
                rvListView.layoutManager = LinearLayoutManager(this@AddPersonActivity)
                rvListView.adapter = adapter
                adapter.onItemClick = {
                    tvCountry.text = it.countryName
                    countryPopupWindow?.dismiss()
                }
                contentView = rvListView
            }
    }

    private fun provideUserPopupWindow(it: View) {
        personPopupWindow = PopupWindow(it.width, ViewGroup.LayoutParams.WRAP_CONTENT)
            .apply {

                isOutsideTouchable = true
                val rvListView = layoutInflater.inflate(
                    R.layout.lay_dropdown,
                    null,
                    false
                ) as RecyclerView
                personAdapter = PersonDropDownAdapter()
                rvListView.layoutManager = LinearLayoutManager(this@AddPersonActivity)
                rvListView.adapter = personAdapter

                personAdapter.addData(alPersonName)
                personAdapter.onItemClick = {
                    item = it
                    setEditUserData(it)
                    personPopupWindow?.dismiss()
                }
                contentView = rvListView
            }
    }
}