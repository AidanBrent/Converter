package com.fortyone.converter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.fortyone.converter.databinding.FragmentConversionBinding
import com.fortyone.converter.databinding.FragmentToolListBinding
import com.fortyone.converter.model.ConversionUnit

class ConversionFragment : Fragment() {

    private val viewModel: ConversionFragmentViewModel by viewModels()
    private val args: ConversionFragmentArgs by navArgs()

    private var _binding: FragmentConversionBinding? = null
    private val binding get() = _binding!!

    private var valueList = mutableListOf<ConversionUnit>()
    private var bEditTextBusy = false
    private var globalFirstFactor = "1"
    private var globalSecondFactor = "1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentConversionBinding.inflate(layoutInflater, container, false)
        //populateList(args.toolName)
        return binding.root
    }

    //private fun populateList(toolName: String) {
        //viewModel.locateArrayResource(args.toolName)
    //}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //populateList(args.toolName)



        //viewModel.locateArrayResource(args.toolName)

        //viewModel.unitList.observe(viewLifecycleOwner){
            //viewModel.updateUnitsFromArray(resources.getStringArray(it))
        //}


        //Populate the spinners based on the array fetched from context resource
        /*viewModel.unitArray.observe(viewLifecycleOwner){ it ->
            valueList = it
            val arrayAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item,
                valueList.map { it.name }
            )*/

            //arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //binding.tvUnit1.setAdapter(arrayAdapter)
            //binding.tvUnit2.setAdapter(arrayAdapter)
        //}
        when (args.toolName) {
            "Distance" -> {
                populateSpinners(R.array.distance_combo)
            //val toolId = R.array.distance_combo
            }
            "Liquid" -> {
                populateSpinners(R.array.liquid_combo)
                //val toolId = R.array.liquid_combo
            }
        }



            loadSpinnerListeners()
            loadTextWatchers()
        }

    fun populateSpinners(toolId : Int) {

        val array = resources.getStringArray(toolId)

        array.map {
            val (unit, factor) = it.split(";")
            valueList.add(ConversionUnit(unit, factor))
        }

        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            valueList.map {it.name}
        )
        binding.tvUnit1.setAdapter(arrayAdapter)
        binding.tvUnit2.setAdapter(arrayAdapter)
    }


    //This function loads the text watchers onto the edit boxes, allowing the calculation to be
    //performed when the value of one of the text boxes is changed
    private fun loadTextWatchers() {
        with(binding) {
            edtValue1.doOnTextChanged { text, _, _, _ ->
                validateAndCalculate(
                    text,
                    edtValue1,
                    edtValue2,
                    globalFirstFactor,
                    globalSecondFactor
                )
            }
            edtValue2.doOnTextChanged { text, _, _, _ ->
                validateAndCalculate(
                    text,
                    edtValue2,
                    edtValue1,
                    globalSecondFactor,
                    globalFirstFactor
                )
            }
        }
    }


    //This function allows the result of the conversion to be displayed when a new conversion unit
    //is selected
    private fun loadSpinnerListeners() {
        with(binding) {
            tvUnit1.setOnItemClickListener { _, _, pos, _ ->
                globalFirstFactor = valueList[pos].factor
                validateAndCalculate(
                    edtValue2.text.toString(),
                    edtValue2,
                    edtValue1,
                    globalSecondFactor,
                    globalFirstFactor
                )
            }

            tvUnit2.setOnItemClickListener { _, _, pos, _ ->
                globalSecondFactor = valueList[pos].factor
                validateAndCalculate(
                    edtValue1.text.toString(),
                    edtValue1,
                    edtValue2,
                    globalFirstFactor,
                    globalSecondFactor
                )
            }

        }
    }



    private fun validateAndCalculate(text: CharSequence?,
                                     firstEditText: EditText,
                                     secondEditText: EditText,
                                     firstFactor : String,
                                     secondFactor: String) {

        //Introduce a new variable to be used for the calculation. This allows for input validation
        //to be performed, eliminating exceptions caused by no input
        val textForCalc = if (text == "" || text?.length == 0) {
            0.00
        } else text.toString().toDouble()

        if(!bEditTextBusy){  /* As the value two edit boxes are linked, this check ensures there is no infinite loop created when a value is changed */
            bEditTextBusy = true

            //calculate and display the result of the conversion
            val resultAsString = ((textForCalc.toString().toDouble() * firstFactor.toDouble()) / secondFactor.toDouble()).toString()
            secondEditText.setText(resultAsString)

            bEditTextBusy = false
        }
    }



}