package com.expertbrains.startegictest.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.expertbrains.startegictest.R
import com.expertbrains.startegictest.base.BaseActivity
import com.expertbrains.startegictest.database.testtable.TestTable
import com.expertbrains.startegictest.extra.Constant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*


class MapActivity : BaseActivity(), OnMapReadyCallback {
    private var isShowLocation: Boolean = false
    private var latLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        init()
    }

    private fun init() {
        bindToolbar()
        toolbarTitle = getString(R.string.pick_location)
        displayHomeButton(true)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        isShowLocation = intent.getBooleanExtra(Constant.IS_SHOW_LOCATION, false)
        btnPick.visibility = if (isShowLocation) View.GONE else View.VISIBLE

        btnPick.setOnClickListener {
            latLng?.let {
                val resultIntent = Intent()
                resultIntent.putExtra(Constant.LAT, it.latitude)
                resultIntent.putExtra(Constant.LNG, it.longitude)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let { map ->

            if (isShowLocation) {
                map.clear()
                val item = intent.getSerializableExtra(Constant.USER_ITEM) as TestTable
                val marker = MarkerOptions().position(LatLng(item.lat, item.lng))
                    .title(item.country.plus(", ").plus(item.state).plus(", ").plus(item.city))
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(item.lat, item.lng),
                        8f
                    )
                )
                map.addMarker(marker)
            } else {
                map.setOnMapClickListener { point ->
                    googleMap.clear()
                    point?.let {
                        val marker =
                            MarkerOptions().position(LatLng(it.latitude, it.longitude))
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 8f))
                        map.addMarker(marker)
                        latLng = LatLng(it.latitude, it.longitude)
                    }
                }
            }
        }
    }
}