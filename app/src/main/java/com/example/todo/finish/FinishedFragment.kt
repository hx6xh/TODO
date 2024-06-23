package com.example.todo.finish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todo.R
import com.example.todo.data.TodoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FinishedFragment : Fragment() {
    private lateinit var finishedAdapter: FinishedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_finished, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.finishedRecyclerView)

        val db = Room.databaseBuilder(requireContext(), TodoDatabase::class.java, "tb_Todo")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        val todoDao = db.TodoDao()

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        finishedAdapter = FinishedAdapter(mutableListOf(),requireContext(),object : FinishedAdapter.OnFinishItemClickListener {
            override fun onItemClick(todoId: Long) {
                val bundle = Bundle().apply {
                    putLong("todoId", todoId) // 将数据放入Bundle
                }
                findNavController().navigate(R.id.action_finishedFragment_to_detailFragment,bundle)
            }
        })
        recyclerView.adapter = finishedAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        GlobalScope.launch {
            val finishList = todoDao.queryFinishedThings()
            withContext(Dispatchers.Main) {
                (recyclerView.adapter as? FinishedAdapter)?.updateTodo(finishList)
            }
        }

    }

}