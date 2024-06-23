package com.example.todo.finish

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.data.Todo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FinishedAdapter(private val finishedList: MutableList<Todo>, private val context: Context, private val onItemClickListener: OnFinishItemClickListener) : RecyclerView.Adapter<FinishedAdapter.ViewHolder>() {

    interface OnFinishItemClickListener {
        fun onItemClick(todoId: Long)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startDate: TextView = itemView.findViewById(R.id.startDate)
        val endDate: TextView = itemView.findViewById(R.id.endDate)
        val event: TextView = itemView.findViewById(R.id.finishedThing)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.finished_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val finishedThing = finishedList[position]
        holder.startDate.text = finishedThing.startTime
        holder.endDate.text = finishedThing.overTime
        holder.event.text = finishedThing.thing

        holder.itemView.setOnClickListener {
            val todoId = finishedList[position].id
            onItemClickListener.onItemClick(todoId)
        }
    }

    override fun getItemCount(): Int {
        return finishedList.size
    }

    fun updateTodo(newList: List<Todo>) {
        finishedList.clear()
        finishedList.addAll(newList)
        finishedList.sortBy {
            LocalDate.parse(it.finishTime)
        }
        notifyDataSetChanged()
    }



    //获取当前滑动项目在数据库中的ID
    override fun getItemId(position:Int): Long{
        return finishedList[position].id
    }
}