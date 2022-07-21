package com.fortyone.converter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fortyone.converter.model.ConversionUnit
import com.fortyone.converter.model.Direction
import com.fortyone.converter.model.Status

class ConversionFragmentViewModel: ViewModel() {

    //Livedata that holds the resource ID of the currently selected tool
    private val _unitID = MutableLiveData<Int>()
    val unitList: LiveData<Int> get() = _unitID

    //Livedata that holds the array of Units to be displayed, and the corresponding conversion factor
    private val _unitArray = MutableLiveData<List<ConversionUnit>>()
    val unitArray: LiveData<List<ConversionUnit>> get() = _unitArray

    private val _firstValue = MutableLiveData<Double>()
    val firstValue: LiveData<Double> get() = _firstValue

    private val _secondValue = MutableLiveData<Double>()
    val secondValue: LiveData<Double> get() = _secondValue

    //private val _status = MutableLiveData(Status.COMPLETE)
    //val status : LiveData<Status> get() = _status

    private var firstFactor : Double = 1.00
    private var secondFactor : Double = 1.00

    //A function to be called by the UI layer, determining which tool was selected.
    fun locateArrayResource(toolName: String) {
        when (toolName) {
            "Distance" -> {
                _unitID.postValue(R.array.distance_combo)
            }
            "Liquid" -> {
                _unitID.postValue(R.array.liquid_combo)
            }
        }
    }

    //A function to be called by the UI layer, which creates a global list containing all the conversion units and their factor, relevant to the selected tool
    fun updateUnitsFromArray(stringArray: Array<String>) {
        val array = mutableListOf<ConversionUnit>()

        stringArray.map {
            val (unit, factor) = it.split(";")
            array.add(ConversionUnit(unit, factor))
        }
        Log.e("aidan2", array[0].factor)
        _unitArray.postValue(array)

        firstFactor = array[0].factor.toDouble()
        secondFactor = array[0].factor.toDouble()
    }

    fun calculate(originValue: Double, direction: Direction) {
        when (direction) {
            Direction.FORWARD -> {
                val result = ((originValue * firstFactor) / secondFactor)
                _secondValue.postValue(result)
            }
            Direction.REVERSE -> {
                val result = (originValue * secondFactor) / firstFactor
                _firstValue.postValue(result)
            }
        }
    }

    fun changeFirstFactor(arrayIndex: Int) {
        firstFactor = _unitArray.value?.get(arrayIndex)?.factor?.toDouble() ?: 1.00
    }

    fun changeSecondFactor(arrayIndex: Int) {
        secondFactor = _unitArray.value?.get(arrayIndex)?.factor?.toDouble() ?: 1.00
    }
}