package com.example.recyclerview.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerview.models.Employee
import com.example.recyclerview.adapters.EmployeeAdapter
import com.example.recyclerview.R
import com.example.recyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), EmployeeAdapter.OnAdapterListener {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: EmployeeAdapter
    private val items: MutableList<Employee> = mutableListOf()

    private var isLongClickMode = false
    private var isSelectAll = false
    private var countItemSelect = 0

    private val addEmployeeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                data?.let {
                    val employee = it.getParcelableExtra<Employee>("employee")
                    if (employee != null) {
                        items.add(employee)
                        adapter.notifyItemInserted(items.size - 1)
                    }
                }
            }
        }

    private val editEmployeeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                data?.let {
                    val employee = it.getParcelableExtra<Employee>("employee")
                    val employeeEdit = items.find { it.id == employee?.id }
                    val position = items.indexOfFirst { it.id == employee?.id }
                    employeeEdit?.let {
                        it.name = employee?.name.toString()
                        it.department = employee?.department.toString()
                        it.status = employee?.status.toString()
                        adapter.notifyItemChanged(position)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        items.add(Employee("B21DCPT007", "Tran Quang Ha", "Dev", "Intern"))
        items.add(Employee("B21DCPT032", "Tran Dang Minh", "Marketing", "Full-time"))
        items.add(Employee("B21DCPT009", "Nguyen Viet Hoang", "Design", "Intern"))

        adapter = EmployeeAdapter(
            items, this
        )

        with(binding) {
            rvList.adapter = adapter
            rvList.layoutManager = LinearLayoutManager(this@MainActivity)

            ivClose.setOnClickListener {
                isLongClickMode = false
                adapter.onCloseLongClickMode()
                adapter.notifyDataSetChanged()
                binding.tbSelect.visibility = View.GONE
            }

            ivDelete.setOnClickListener {
                val selectedItems = items.filter { it.isSelected }
                items.removeAll(selectedItems)
                countItemSelect = 0
                binding.tvItemSelected.text = "0"
                isLongClickMode = false
                binding.tbSelect.visibility = View.GONE
                adapter.onCloseLongClickMode()
                adapter.notifyDataSetChanged()
            }

            ivSelectAll.setOnClickListener {
                isSelectAll = !isSelectAll
                if (isSelectAll) {
                    adapter.selectAll()
                    adapter.notifyDataSetChanged()
                } else {
                    adapter.deselectAll()
                    adapter.notifyDataSetChanged()
                }
            }

            etSearch.addTextChangedListener {
                val searchQuery = binding.etSearch.text.toString()

                val searchList = if (searchQuery.isEmpty()) {
                    val tmpItems = mutableListOf<Employee>()
                    tmpItems.addAll(items)
                    tmpItems
                } else {
                    val tmpItems = mutableListOf<Employee>()
                    tmpItems.addAll(items)
                    tmpItems.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }.toMutableList()
                }

                adapter.updateList(searchList)
            }
        }

        binding.btAdd.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            addEmployeeLauncher.launch(intent)
        }
    }

    override fun editItem(id: String) {
        val intent = Intent(this, EditActivity::class.java)
        val employee = items.find { it.id == id }
        intent.putExtra("employee", employee)
        editEmployeeLauncher.launch(intent)
    }

    override fun onClickItem(id: String) {
        val intent = Intent(this, DetailActivity::class.java)
        val employee = items.find { it.id == id }
        intent.putExtra("employee", employee)
        startActivity(intent)
    }

    override fun onLongClickItem() {
        isLongClickMode = !isLongClickMode
        if (isLongClickMode) {
            binding.tbSelect.visibility = View.VISIBLE
        } else binding.tbSelect.visibility = View.GONE
    }

    override fun updateSelectedItemCount(count: Int) {
        binding.tvItemSelected.text = "$count"
    }
}