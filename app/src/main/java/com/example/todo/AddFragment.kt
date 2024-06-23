package com.example.todo

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.todo.data.Todo
import com.example.todo.data.TodoDatabase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<EditText>(R.id.et_title)
        val type = view.findViewById<Spinner>(R.id.sp_type)
        val startDate = view.findViewById<TextView>(R.id.et_startDate)
        val overDate = view.findViewById<TextView>(R.id.et_overDate)
        val description = view.findViewById<EditText>(R.id.et_description)
        val priority = view.findViewById<Spinner>(R.id.sp_priority)
        val daKa = view.findViewById<RadioGroup>(R.id.daka)
        val btnAddLog = view.findViewById<Button>(R.id.btnAdd)

        /*
          选择类别
         */
        val itemType = arrayOf("日常", "工作", "学习", "锻炼")
        // 初始化Spinner并设置Adapter
        val adapterType = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, // 默认的文本视图布局
            itemType
        )
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // 下拉列表的布局
        type.adapter = adapterType
        type.setSelection(0) //默认为日常

        /*
         选择开始和结束日期
         */
        startDate.setOnClickListener {
            showDatePickerDialog(startDate)
        }
        overDate.setOnClickListener {
            showDatePickerDialog(overDate)
        }

        /*
          选择优先级
         */
        val itemPriority = arrayOf("紧急且重要", "紧急但不重要", "不紧急但重要", "不紧急不重要")
        // 初始化Spinner并设置Adapter
        val adapterPriority = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, // 默认的文本视图布局
            itemPriority
        )
        adapterPriority.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // 下拉列表的布局
        priority.adapter= adapterPriority
        priority.setSelection(0) //默认为日常

        //添加
        btnAddLog.setOnClickListener {
            val titleA  = title.text.toString().trim()
            val typeA = type.selectedItem.toString()
            val startDateA = startDate.text.toString().trim()
            val overDateA = overDate.text.toString().trim()
            val descriptionA = description.text.toString().trim()
            val priorityA = priority.selectedItem.toString()
            val prove = daKa.checkedRadioButtonId
            var isProve = 0
            val selectedRadioButton = view.findViewById<RadioButton>(prove)
            val selectedText = selectedRadioButton.text
            when (selectedText.toString()) {
                "无" -> isProve = 0
                "拍照打卡" -> isProve = 1
            }
            //判断任务状态
            var status: String
            val currentDate = Calendar.getInstance()
            val startDateB = dateStringToCalendar(startDateA)
            val overDateB = dateStringToCalendar(overDateA)
            when {
                currentDate.before(startDateB) -> {
                    // 未开始
                    status = "Not Start"
                }
                currentDate.after(startDateB) && currentDate.before(overDateB) -> {
                    // 正在进行中（已开始，但未逾期）
                    status = "Ongoing"
                }
                currentDate.after(overDateB) -> {
                    // 已逾期
                    status = "Overdue"
                }
                else ->{
                    status = "error"
                }
            }
//            val isCancelled: Boolean = false // 任务是否被取消
//            val isOverdue: Boolean = LocalDate.now().isAfter(overDateA) // 当前日期是否超过结束日期
//            val needsRework: Boolean = false // 任务是否需要返工

            if (titleA.isNotEmpty() && status!="error" ) {
                //添加数据
                val db = Room.databaseBuilder(requireContext(), TodoDatabase::class.java, "tb_Todo")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                val todoDao = db.TodoDao()
                val todo = Todo(titleA,typeA,startDateA,overDateA,descriptionA,priorityA,isProve,status)
                todoDao.insertThing(todo)
                findNavController().popBackStack()
            } else {
                title.error = "请输入事件"
            }
        }
    }

    private fun showDatePickerDialog(etDate: TextView) {
        // 获取当前日期作为DatePickerDialog的默认日期
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        // 创建一个DatePickerDialog的实例
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                // 用户确认选择后的回调
                val selectedDate =  "${year}-${String.format("%02d", monthOfYear + 1)}-${String.format("%02d", dayOfMonth)}"
                etDate.text = selectedDate // 设置选中的日期到EditText
            },
            currentYear, // 初始化年份
            currentMonth, // 初始化月份
            currentDayOfMonth // 初始化日
        )
        datePickerDialog.show() // 显示DatePickerDialog
    }

    private fun dateStringToCalendar(dateString: String): Calendar? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return try {
            val date = dateFormat.parse(dateString)
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }
            calendar
        } catch (e: ParseException) {
            // 处理解析日期字符串失败的情况
            e.printStackTrace()
            null
        }
    }
}
