package com.fortyone.converter

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.fortyone.converter.databinding.FragmentConversionBinding
import com.fortyone.converter.model.Direction
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ConversionFragment : Fragment() {

    private val viewModel: ConversionFragmentViewModel by viewModels()
    private val args: ConversionFragmentArgs by navArgs()

    private var _binding: FragmentConversionBinding? = null
    private val binding get() = _binding!!

    //Since editing one edit box changes the value in the other, this boolean value prevents an
    //infinite loop, where the boxes infinitely change each other
    private var bEditTextBusy = false

    //These values hold the conversion factor relevant to the currently selected Conversion Units
    //private var globalFirstFactor = "1"
    //private var globalSecondFactor = "1"

    //Stores current result
    //var result = 0.00

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentConversionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Pass fragment transaction argument stating which tool was selected, to the View Model
        viewModel.locateArrayResource(args.toolName)

        //When the correct ID for the array resource (based on the selected tool) is determined by the ViewModel,
        //a global array is retrieved from the context resources and passed back to the viewmodel which then creates
        //an List containing the relevant Conversion Units
        viewModel.unitList.observe(viewLifecycleOwner) {
            viewModel.updateUnitsFromArray(resources.getStringArray(it))
        }

        viewModel.firstValue.observe(viewLifecycleOwner) {
            binding.edtValue1.setText(it.toString())
        }

        viewModel.secondValue.observe(viewLifecycleOwner) {
            binding.edtValue2.setText(it.toString())
        }


        //Populate the spinners based on the array created in the View Model
        viewModel.unitArray.observe(viewLifecycleOwner) { it ->
            val arrayAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item,
                it.map { it.name }
            )
            //Populate initial display values and factors
            binding.tvUnit1.setText(it[0].name)
            //globalFirstFactor = it[0].factor
            binding.tvUnit2.setText(it[0].name)
            //globalSecondFactor = it[0].factor

            binding.tvUnit1.setAdapter(arrayAdapter)
            binding.tvUnit2.setAdapter(arrayAdapter)

        }
        //Call the local function which loads listeners onto the dropdown menus, which determine which conversion units are selected
        loadSpinnerListeners()
        //Call the local function which loads textWatchers onto the edit boxes, which perform the conversion while text is being changed.
        loadTextWatchers()
    }

    //This function loads the text watchers onto the edit boxes, allowing the calculation to be
    //performed when the value of one of the text boxes is changed
//    private fun loadTextWatchers() {
//        with(binding) {
//            edtValue1.doBeforeTextChanged { text, _, _, _ ->
//                /* validateAndCalculate(
//                    text,
//                    edtValue2,
//                    globalFirstFactor,
//                    globalSecondFactor
//                )
//                binding.edtValue1.text
//                binding.tvFull1.text = result.toString()*/
//                //if (!bEditTextBusy) {
//                //bEditTextBusy = true
//                viewModel.calculate(text.toString().toDouble(), Direction.FORWARD)
//                //bEditTextBusy = false
//            }
//
//
//
//            edtValue2.doBeforeTextChanged { text, _, _, _ ->
//                /*validateAndCalculate(
//                    text,
//                    edtValue1,
//                    globalSecondFactor,
//                    globalFirstFactor
//                )
//                binding.tvFull2.text = result.toString()*/
//                //if (!bEditTextBusy) {
//                    //bEditTextBusy = true
//                    viewModel.calculate(text.toString().toDouble(), Direction.REVERSE)
//                    //bEditTextBusy = false
//                }
//            }
//
//        }

    var isManual = true

    private fun loadTextWatchers() {
        with(binding) {

            edtValue1.doAfterTextChanged { text ->
                if (isManual) {
                    viewModel.calculate(text.toString().toDouble(), Direction.FORWARD)

                }
                isManual = !isManual
            }

            edtValue2.doAfterTextChanged { text ->
                if (isManual) {
                    viewModel.calculate(text.toString().toDouble(), Direction.REVERSE)
                }
                isManual = !isManual
            }
        }
    }

    //This function allows the result of the conversion to be displayed when the unit selection is changed
    private fun loadSpinnerListeners() {
        with(binding) {
            tvUnit1.setOnItemClickListener { _, _, pos, _ ->
                //globalFirstFactor = viewModel.unitArray.value?.get(pos)?.factor ?: "0"
                //Log.e("Factors", globalFirstFactor)
/*                validateAndCalculate(
                    edtValue2.text.toString(),
                    edtValue1,
                    globalSecondFactor,
                    globalFirstFactor
                 )*/
                viewModel.changeFirstFactor(pos)
                //if (validate(binding.edtValue2)) viewModel.calculate(edtValue1.text.toString().toDouble(), Direction.REVERSE)
            }

            tvUnit2.setOnItemClickListener { _, _, pos, _ ->
                /*globalSecondFactor = viewModel.unitArray.value?.get(pos)?.factor ?: "0"
                Log.e("Factors", globalSecondFactor)
                validateAndCalculate(
                    edtValue1.text.toString(),
                    edtValue2,
                    globalFirstFactor,
                    globalSecondFactor
                )*/
                viewModel.changeSecondFactor(pos)
                // if (validate(binding.edtValue1)) viewModel.calculate(edtValue1.text.toString().toDouble(), Direction.FORWARD)
            }
        }
    }

    private fun validate(editText: EditText) : Boolean {
        return if (editText.text.toString() == "" || editText.length() == 0) {
            editText.setText("0")
            false
        } else {
            true
        }
    }

        //This function validates the input and displays the result
//    private fun validateAndCalculate(text: CharSequence?,
//                                     resultEditText: EditText,
//                                     firstFactor : String,
//                                     secondFactor: String) : Double {
//
//        //Introduce a new variable to be used for the calculation. This allows input validation
//        //to be performed, eliminating exceptions caused by no input
//        val textForCalc = if (text == "" || text?.length == 0) {
//            0.00
//        } else text.toString().toDouble()
//
//        if(!bEditTextBusy){  /* As the value of the two edit boxes are linked, this check ensures there is no infinite loop created when a value is changed */
//            bEditTextBusy = true
//
//            //calculate and display the result of the conversion
//            result = (textForCalc.toString().toDouble() * firstFactor.toDouble()) / secondFactor.toDouble()
//
//            bEditTextBusy = false
//        }
//        return result
//    }
    }