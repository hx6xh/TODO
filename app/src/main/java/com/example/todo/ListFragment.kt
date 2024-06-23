package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todo.act.CameraActivity
import com.example.todo.data.TodoDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ListFragment : Fragment(){
    lateinit var todoAdapter: TodoAdapter
    private lateinit var popupWindowA: PopupWindow
    private lateinit var popupWindowB: PopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    private fun showPopupMenu(anchor: View) {
        // 计算弹出位置，使其出现在anchorView的右上角
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        val x = location[0] + anchor.width / 2
        val y = location[1] - popupWindowA.height / 2

        // 显示弹出菜单
        popupWindowA.showAtLocation(anchor, Gravity.TOP and Gravity.END, x, y)
    }
    private fun showPopupSort(anchor: View) {
        // 计算弹出位置，使其出现在左上角
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        val x = location[0] // 直接使用anchorView左侧位置
        val y = location[1] - popupWindowB.height / 2 // 维持y轴计算方式，确保向上对齐

        // 显示弹出菜单，
        popupWindowB.showAtLocation(anchor, Gravity.TOP or Gravity.START, x, y)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.fab)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val btnMenu = view.findViewById<ImageButton>(R.id.menu)
        val btnSort = view.findViewById<ImageButton>(R.id.sort)

        val db = Room.databaseBuilder(requireContext(), TodoDatabase::class.java, "tb_Todo")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        val todoDao = db.TodoDao()

        // 初始化弹出菜单
        val popupInflater = LayoutInflater.from(requireContext())
        val popupView = popupInflater.inflate(R.layout.popup_menu_layout, null)
        popupWindowA = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindowA.isFocusable = true
        //设置设置，归档事项，数据统计的页面
        popupView.findViewById<ViewGroup>(R.id.menu_item_setting).setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_settingFragment)
            popupWindowA.dismiss()
        }
        popupView.findViewById<ViewGroup>(R.id.menu_item_finished).setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_finishedFragment)
            popupWindowA.dismiss()
        }
        popupView.findViewById<ViewGroup>(R.id.menu_item_census).setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_censusFragment)
            popupWindowA.dismiss()
        }

        btnMenu.setOnClickListener { showPopupMenu(it) }

        // 初始化弹出排序选项
        val popupInflaterSort = LayoutInflater.from(requireContext())
        val popupViewSort = popupInflaterSort.inflate(R.layout.popup_sort_layout, null)
        popupWindowB = PopupWindow(popupViewSort, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindowB.isFocusable = true
        //排序
        popupViewSort.findViewById<TextView>(R.id.tv_sort_start_time).setOnClickListener {
            val todoList = todoDao.queryAllThings()
            (recyclerView.adapter as? TodoAdapter)?.sortBystart(todoList)
            popupWindowB.dismiss()
        }
        popupViewSort.findViewById<TextView>(R.id.tv_sort_end_time).setOnClickListener {
            val todoList = todoDao.queryAllThings()
            (recyclerView.adapter as? TodoAdapter)?.sortByend(todoList)
            popupWindowB.dismiss()
        }
        popupViewSort.findViewById<TextView>(R.id.tv_sort_priority).setOnClickListener {
            val todoList = todoDao.queryAllThings()
            (recyclerView.adapter as? TodoAdapter)?.sortByPriority(todoList)
            popupWindowB.dismiss()
        }

        btnSort.setOnClickListener { showPopupSort(it) }


        btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }



        recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        todoAdapter = TodoAdapter(mutableListOf(), requireContext(), object : TodoAdapter.OnItemClickListener {
            override fun onItemClick(todoId: Long) {

                val bundle = Bundle().apply {
                    putLong("todoId", todoId) // 将数据放入Bundle
                }
                findNavController().navigate(R.id.action_listFragment_to_detailFragment,bundle)
            }

            override fun finishItem(todoId: Long, prove: Int) {
                if(prove==1){
                    val bundle = Bundle().apply {
                        putLong("todoId", todoId) // 将数据放入Bundle
                    }
                    startActivity(Intent(requireContext(), CameraActivity::class.java).putExtras(bundle))
                }
                else{
                    Toast.makeText(requireContext(),"该事项已完成",Toast.LENGTH_SHORT).show()
                    //当前时间保存成yyyy-MM-dd HH:mm:ss格式
                    val now = Date(System.currentTimeMillis())
                    val st = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                    val fTime = st.format(now)
                    todoDao.finishThingById(todoId,fTime)
                    val updatedLogs = todoDao.queryAllThings() // 重新查询数据
                    todoAdapter.updateTodo(updatedLogs)
                }
            }
            override fun editItem(todoId: Long) {
                val bundle = Bundle().apply {
                    putLong("todoId", todoId) // 将数据放入Bundle
                }
                findNavController().navigate(R.id.action_listFragment_to_editFragment,bundle)
            }
            override fun deleteItem(todoId: Long) {
                todoDao.deleteThing(todoId)
                val updatedLogs = todoDao.queryAllThings() // 重新查询数据
                todoAdapter.updateTodo(updatedLogs)
            }
        })

        recyclerView.adapter = todoAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        GlobalScope.launch {
            val todoList = todoDao.queryAllThings()
            withContext(Dispatchers.Main) {
                (recyclerView.adapter as? TodoAdapter)?.updateTodo(todoList)
            }
        }

    }

}
