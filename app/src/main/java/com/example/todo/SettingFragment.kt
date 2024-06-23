package com.example.todo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.todo.act.Login
import com.example.todo.data.TodoDatabase


class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val switchNight = view.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switch_night)
        val deleteAll: TextView = view.findViewById(R.id.deleteAllData)
        val about: TextView = view.findViewById(R.id.about)
        val logout: Button = view.findViewById(R.id.logout)

        //夜间模式模块
        // 获取当前的夜间模式状态
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        // 根据当前模式设置开关状态
        switchNight.isChecked = when (currentNightMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO  -> false
            else -> false
        }
        switchNight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 开启黑夜模式
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // 日间模式(默认)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        //删除数据模块
        deleteAll.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("删除确认")
                .setMessage("您确定要删除所有数据吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认") { _, _ ->
                    val db = Room.databaseBuilder(requireContext(), TodoDatabase::class.java, "tb_Todo")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                    val todoDao = db.TodoDao()
                    todoDao.deleteAllThings()
                    Toast.makeText(requireContext(), "数据已全部删除", Toast.LENGTH_SHORT).show()
                }
                .create()
                .show()
        }

        //关于模块
        val aboutMessage = """
        软件名称：TODO
        版本号：1.0.0
        发布日期：2024年6月3日
        作者：何祥
        """.trimIndent()
        about.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("关于")
                .setMessage(aboutMessage)
                .setPositiveButton("关闭", null)
                .create()
                .show()
        }

        //退出登录模块
        logout.setOnClickListener {
            startActivity(Intent(requireContext(), Login::class.java))
            requireActivity().finish()
        }
    }

}