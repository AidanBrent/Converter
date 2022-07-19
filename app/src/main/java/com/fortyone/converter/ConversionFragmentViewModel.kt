package com.fortyone.converter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fortyone.converter.model.ConversionUnit

class ConversionFragmentViewModel: ViewModel() {


    private val _unitList = MutableLiveData<Int>()
    val unitList: LiveData<Int> get() = _unitList

    private val _valueList = MutableLiveData<Int>()
    val valueList: LiveData<Int> get() = _valueList

    private val _unitArray = MutableLiveData<List<ConversionUnit>>()
    val unitArray: LiveData<List<ConversionUnit>> get() = _unitArray


    fun locateArrayResource(toolName: String) {
        when (toolName) {
            "Distance" -> {
                _unitList.postValue(R.array.distance_combo)
            }
            "Liquid" -> {
                _unitList.postValue(R.array.liquid_combo)
            }
        }
    }

    fun updateUnitsFromArray(stringArray: Array<String>) {
        val array = mutableListOf<ConversionUnit>()

        stringArray.map {
            val (unit, factor) = it.split(";")
            array.add(ConversionUnit(unit, factor))
        }

        _unitArray.postValue(array)

    }
}