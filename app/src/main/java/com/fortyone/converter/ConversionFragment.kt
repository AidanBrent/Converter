package com.fortyone.converter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.fortyone.converter.databinding.FragmentConversionBinding

class ConversionFragment : Fragment() {

    private val viewModel: ConversionFragmentViewModel by viewModels()
    private val args: ConversionFragmentArgs by navArgs()

    private var _binding: FragmentConversionBinding? = null
    private val binding get() = _binding!!

    //Since editing one edit box changes the value in the other, this boolean value prevents an
    //infinite loop, where the boxes infinitely change each other
    private var bEditTextBusy = false

    //These values hold the conversion factor relevant to the currently selected Conversion Units
    private var globalFirstFactor = "1"
    private var globalSecondFactor = "1"

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
        viewModel.unitList.observe(viewLifecycleOwner){
            viewModel.updateUnitsFromArray(resources.getStringArray(it))
        }

        //Fetch and Display the current values for the Edit Texts
        viewModel.firstValue.observe(viewLifecycleOwner) {
            binding.edtValue1.setText(displayFormat.format(it))
            val display: String = it.toString().replace("E", " x10^")
            binding.tvExact2.text = getString(R.string.exact, binding.edtValue2.text)
            binding.tvExact1.text = getString(R.string.exact, display)
        }

        viewModel.secondValue.observe(viewLifecycleOwner) {
            binding.edtValue2.setText(displayFormat.format(it))
            val display: String = it.toString().replace("E", " x10^")
            binding.tvExact1.text = getString(R.string.exact, binding.edtValue1.text)
            binding.tvExact2.text = getString(R.string.exact, display)
        }
        //Populate the spinners based on the array created in the View Model
        viewModel.unitArray.observe(viewLifecycleOwner){ it ->
            val arrayAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item,
                it.map { it.name}
            )
            binding.tvUnit1.setAdapter(arrayAdapter)
            binding.tvUnit2.setAdapter(arrayAdapter)
        }
            //Call the local function which loads listeners onto the dropdown menu, which determine which conversion unit is selected
            loadSpinnerListeners()
            //Call the local function which loads textWatcher onto the edit boxes, which performs the conversion while text is being changed.
            loadTextWatchers()
        }

    //This function loads the text watchers onto the edit boxes, allowing the calculation to be
    //performed when the value of one of the text boxes is changed
    private fun loadTextWatchers() {
        with(binding) {
            edtValue1.doOnTextChanged { text, _, _, _ ->
                validateAndCalculate(
                    text,
                    edtValue2,
                    globalFirstFactor,
                    globalSecondFactor
                )
            }
            edtValue2.doOnTextChanged { text, _, _, _ ->
                validateAndCalculate(
                    text,
                    edtValue1,
                    globalSecondFactor,
                    globalFirstFactor
                )
            }
        }
    }

    //This function allows the result of the conversion to be displayed when the unit selection is changed
    private fun loadSpinnerListeners() {
        with(binding) {
            tvUnit1.setOnItemClickListener { _, _, pos, _ ->
                globalFirstFactor = viewModel.unitArray.value?.get(pos)?.factor ?: "0"
                validateAndCalculate(
                    edtValue2.text.toString(),
                    edtValue1,
                    globalSecondFactor,
                    globalFirstFactor
                 )
            }

            tvUnit2.setOnItemClickListener { _, _, pos, _ ->
                globalSecondFactor = viewModel.unitArray.value?.get(pos)?.factor ?: "0"
                validateAndCalculate(
                    edtValue1.text.toString(),
                    edtValue2,
                    globalFirstFactor,
                    globalSecondFactor
                )
            }

        }
    }

    //This function validates the input and displays the result
    private fun validateAndCalculate(text: CharSequence?,
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