package com.example.todo

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.todo.data.TodoDatabase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar


class EditFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var Id:Long = 1
        arguments?.let { args ->
            Id = args.getLong("todoId")
        }
        val title = view.findViewById<TextView>(R.id.et_edit_title)
        val type = view.findViewById<Spinner>(R.id.sp_edit_type)
        val startDate = view.findViewById<TextView>(R.id.et_edit_startDate)
        val overDate = view.findViewById<TextView>(R.id.et_edit_overDate)
        val description = view.findViewById<EditText>(R.id.et_edit_description)
        val priority = view.findViewById<Spinner>(R.id.sp_edit_priority)
        val prove = view.findViewById<RadioGroup>(R.id.edit_daka)
        val noNeed = view.findViewById<RadioButton>(R.id.edit_notNeed)
        val photo = view.findViewById<RadioButton>(R.id.edit_photo)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        val db = Room.databaseBuilder(requireContext(), TodoDatabase::class.java, "tb_Todo")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        val todoDao = db.TodoDao()

        /*
        显示原始数据
         */
        val aThing = todoDao.findThingById(Id)
        title.text = aThing.thing
        //选择类别
        val itemType = arrayOf("日常", "工作", "学习", "锻炼")
        val adapterType = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, // 默认的文本视图布局
            itemType
        )
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // 下拉列表的布局
        type.adapter = adapterType
        when (aThing.type) {
            "日常" -> type.setSelection(0)
            "工作" -> type.setSelection(1)
            "学习" -> type.setSelection(2)
            "锻炼" -> type.setSelection(3)
            else -> type.setSelection(0)
        }

        startDate.text = aThing.startTime
        overDate.text = aThing.overTime

        // 选择开始和结束日期
        startDate.setOnClickListener {
            showDatePickerDialog(startDate)
        }
        overDate.setOnClickListener {
            showDatePickerDialog(overDate)
        }

        description.setText(aThing.description)
        //选择优先级
        val itemPriority = arrayOf("紧急且重要", "紧急但不重要", "不紧急但重要", "不紧急不重要")
        val adapterPriority = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, // 默认的文本视图布局
            itemPriority
        )
        adapterPriority.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // 下拉列表的布局
        priority.adapter= adapterPriority
        when (aThing.priority) {
            "紧急且重要" -> priority.setSelection(0)
            "紧急但不重要" -> priority.setSelection(1)
            "不紧急但重要" -> priority.setSelection(2)
            "不紧急不重要" -> priority.setSelection(3)
            else -> priority.setSelection(0)
        }
        when(aThing.prove){
            0 -> noNeed.isChecked = true
            1 -> photo.isChecked = true
            else -> noNeed.isChecked = true
        }

        /*
        保存修改后的数据
         */
        btnSave.setOnClickListener {
            val titleA = title.text.toString().trim()
            val typeA = type.selectedItem.toString()
            val startDateA = startDate.text.toString().trim()
            val overDateA = overDate.text.toString().trim()
            val descriptionA = description.text.toString().trim()
            val priorityA = priority.selectedItem.toString()
            val daKa = prove.checkedRadioButtonId
            var isProve = 0
            val selectedRadioButton = view.findViewById<RadioButton>(daKa)
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

                else -> {
                    status = "error"
                }
            }

            if (titleA.isNotEmpty() && status != "error") {
                //添加数据

                todoDao.updateThingById(
                    Id,
                    titleA,
                    typeA,
                    startDateA,
                    overDateA,
                    descriptionA,
                    priorityA,
                    isProve,
                    status
                )
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