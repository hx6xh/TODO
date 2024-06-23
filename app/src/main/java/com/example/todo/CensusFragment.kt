package com.example.todo

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.room.Room
import com.example.todo.data.TodoDatabase
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate


class CensusFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_census, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = Room.databaseBuilder(requireContext(), TodoDatabase::class.java, "tb_Todo")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        val todoDao = db.TodoDao()
        var completedTasksCount = todoDao.countCompletedTasks()
        var uncompletedTasksCount = todoDao.countUncompletedTasks()
        val pieChart = view.findViewById<PieChart>(R.id.pieChart)
        setupPieChart(completedTasksCount,uncompletedTasksCount,pieChart)

        val btnS = view.findViewById<ImageButton>(R.id.sDate)
        val textS = view.findViewById<TextView>(R.id.textsDate)
        val btnE = view.findViewById<ImageButton>(R.id.eDate)
        val textE = view.findViewById<TextView>(R.id.texteDate)
        val btnQuery = view.findViewById<Button>(R.id.btnQuery)
        btnS.setOnClickListener {
            showDatePickDialog(textS)
        }
        btnE.setOnClickListener {
            showDatePickDialog(textE)
        }
        btnQuery.setOnClickListener {
            if (textS.text.toString().isNotEmpty() && textE.text.toString().isNotEmpty()){
                val start = textS.text.toString()
                val end = textE.text.toString()
                completedTasksCount = todoDao.countCompletedTasksInTimeRange(start,end)
                uncompletedTasksCount = todoDao.countUncompletedTasksInTimeRange(start,end)
                Log.d("ada","$completedTasksCount and $uncompletedTasksCount")
                setupPieChart(completedTasksCount,uncompletedTasksCount,pieChart)
            }else{
                Log.d("ada",textE.text.toString())
            }
        }

    }

    private fun showDatePickDialog(text:TextView){
        val datePickerDialog = DatePickerDialog(requireContext())
        datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
            // 处理选择的日期
            val selectedDate =  "${year}-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
            text.text = selectedDate
        }
        datePickerDialog.show()
    }
    private fun setupPieChart(completedTasksCount: Int, uncompletedTasksCount: Int, pieChart: PieChart) {
        // 创建数据条目列表
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(uncompletedTasksCount.toFloat(), "未完成"))
        entries.add(PieEntry(completedTasksCount.toFloat(), "已完成"))
        // 配置图表样式
        with(pieChart) {
            // 设置是否显示描述文字
            description.isEnabled = false
            // 设置是否可以旋转图表
            isRotationEnabled = true
            // 动画
            animateY(1400)
            // 数据集
            val dataSet = PieDataSet(entries, "任务状态")
            // 设置颜色
            dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
            // 数据入口
            data = PieData(dataSet)
            // 图例
            legend.isEnabled = true
            // 更新图表
            invalidate()
        }
    }

}