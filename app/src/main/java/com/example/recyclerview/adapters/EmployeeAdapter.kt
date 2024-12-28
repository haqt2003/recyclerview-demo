package com.example.recyclerview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.models.Employee
import com.example.recyclerview.R
import com.example.recyclerview.databinding.LayoutItemBinding

class EmployeeAdapter(
    private var items: MutableList<Employee>,
    private val onAdapterListener: OnAdapterListener
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    private var isLongClickMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = LayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        if (isLongClickMode) {
            holder.binding.cbSelect.visibility = View.VISIBLE
            holder.binding.tvEdit.visibility = View.VISIBLE
        } else {
            holder.binding.cbSelect.visibility = View.GONE
            holder.binding.tvEdit.visibility = View.GONE
            item.isSelected = false
        }

        holder.binding.cbSelect.isChecked = item.isSelected

        holder.itemView.setOnLongClickListener {
            isLongClickMode = !isLongClickMode
            item.isSelected = !item.isSelected
            notifyDataSetChanged()
            onAdapterListener.onLongClickItem()
            true
        }

        holder.binding.tvEdit.setOnClickListener {
            onAdapterListener.editItem(items[position].id)
        }

        holder.itemView.setOnClickListener {
            onAdapterListener.onClickItem(items[position].id)
        }

        holder.binding.cbSelect.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
            val selectedItems = items.filter { it.isSelected }
            onAdapterListener.updateSelectedItemCount(selectedItems.size)
        }
    }

    fun onCloseLongClickMode() {
        isLongClickMode = false
        notifyDataSetChanged()
    }

    fun selectAll() {
        items.forEach {
            it.isSelected = true
        }
    }

    fun deselectAll() {
        items.forEach {
            it.isSelected = false
        }
    }

    fun updateList(newList: MutableList<Employee>) {
        items = newList
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun editItem(id: String)
        fun onClickItem(id: String)
        fun onLongClickItem()
        fun updateSelectedItemCount(count: Int)
    }

    class EmployeeViewHolder(val binding: LayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: Employee) {
            binding.tvId.text = employee.id
            binding.tvName.text = employee.name
            when (employee.department) {
                "Dev" -> binding.ivDepartment.setImageResource(R.drawable.ic_dev)
                "Design" -> binding.ivDepartment.setImageResource(R.drawable.ic_design)
                "Marketing" -> binding.ivDepartment.setImageResource(R.drawable.ic_marketing)
            }
            when (employee.status) {
                "Intern" -> binding.ivStatus.setImageResource(R.drawable.ic_intern)
                "Full-time" -> binding.ivStatus.setImageResource(R.drawable.ic_employee)
            }
        }
    }
}