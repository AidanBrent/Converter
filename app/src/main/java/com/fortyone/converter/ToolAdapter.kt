package com.fortyone.converter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.fortyone.converter.databinding.ToolItemBinding
import com.fortyone.converter.model.Tool

class ToolAdapter(private var toolList: List<Tool>, private val onItemClicked: (Tool) -> Unit) : RecyclerView.Adapter<ToolAdapter.ToolViewHolder>() {

    class ToolViewHolder(var binding: ToolItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val toolViewHolder = ToolViewHolder(
            ToolItemBinding.inflate(
                LayoutInflater.from( parent.context),
                parent,
                false
            )
        )
        return toolViewHolder
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        holder.binding.imgTool.load(toolList[position].image)
        holder.binding.tvTool.text = toolList[position].name
        holder.itemView.setOnClickListener { onItemClicked(toolList[position])}
    }

    override fun getItemCount(): Int {
        return toolList.size
    }
}