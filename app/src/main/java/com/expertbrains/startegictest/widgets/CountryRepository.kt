package com.expertbrains.startegictest.widgets

import com.expertbrains.startegictest.model.CountryItem
import java.util.*
import kotlin.collections.ArrayList

object CountryRepository {
    val all: ArrayList<CountryItem>
        get() {
            val countries: ArrayList<CountryItem> = ArrayList()
            val locales: Array<Locale> = Locale.getAvailableLocales()
            for (locale in locales) {
                val countryCode: String = locale.country
                val countryName: String = locale.displayCountry
                if (countryCode.trim { it <= ' ' }.isNotEmpty() && countryName.trim { it <= ' ' }
                        .isNotEmpty()) {
                    val country = CountryItem(countryCode, countryName)
                    countries.add(country)
                }
            }
            return countries
        }
}