package com.fortyone.converter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.fortyone.converter.databinding.FragmentToolListBinding
import com.fortyone.converter.model.Tool

class ToolListFragment : Fragment() {

    private var _binding: FragmentToolListBinding? = null
    private val binding get() = _binding!!
    private val toolList = mutableListOf<Tool>() //Holds the list of tools

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //populate the hardcoded list of tools
        populateTools(toolList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentToolListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Create the adapter to be used by the recycler view, using the list of possible tools, and an onclick listener
        val adapter = ToolAdapter(toolList) {
            val action = ToolListFragmentDirections.actionToolListFragmentToConversionFragment(it.name)
            Log.e("ToolFragment", it.name)
            findNavController().navigate(action)
        }

        //Initialize the recycler view
        binding.rvTools.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvTools.adapter = adapter

    }

    private fun populateTools(toolList: MutableList<Tool>) {
        //Hardcoded list of tools
        toolList.add(Tool("Distance", R.drawable.tool_distance))
        toolList.add(Tool("Liquid", R.drawable.tool_liquid))
        toolList.add(Tool("Area", R.drawable.tool_distance))
        toolList.add(Tool("Mass", R.drawable.tool_mass))
        toolList.add(Tool("Pressure", R.drawable.tool_pressure))
        toolList.add(Tool("Torque", R.drawable.tool_torque))
    }
}