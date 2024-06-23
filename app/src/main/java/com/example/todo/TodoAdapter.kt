package com.example.todo

import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.data.Todo
import java.text.SimpleDateFormat
import java.time.LocalDate

import java.util.Date
import java.util.concurrent.TimeUnit


class TodoAdapter(private val todoList: MutableList<Todo>, private val context: Context, private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(todoId: Long)
        fun finishItem(todoId: Long, prove:Int)
        fun editItem(todoId: Long)
        fun deleteItem(todoId: Long)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textDate: TextView = itemView.findViewById(R.id.textDate)
        var textEvent: TextView = itemView.findViewById(R.id.textThing)
        var btnFinish: ImageButton = itemView.findViewById(R.id.btn_finish)
        var btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)
        var btnDelete: ImageButton = itemView.findViewById(R.id.btn_remove)
        var rootView: ViewGroup = itemView.findViewById(R.id.container)

        var gestureDetector: GestureDetector = GestureDetector(rootView.context, object:
        GestureDetector.OnGestureListener{
            override fun onDown(e: MotionEvent): Boolean {
                return false
            }
            override fun onShowPress(e: MotionEvent) {
            }
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val todoId = todoList[position].id
                onItemClickListener.onItemClick(todoId)
                Log.d("ada","click $todoId")
                return true
            }
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                return false
            }

            override fun onLongPress(e: MotionEvent) {
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                vX: Float,
                vY: Float
            ): Boolean {
                Log.d("adapter", "fling " + vX.toString())
                if (vX > 1000) {
                    val anime = ObjectAnimator.ofFloat(rootView, "translationX", rootView.translationX, 0.0f)
                    anime.duration= 200
                    anime.start()
                }
                else if (vX < -1000) {
                    val anime = ObjectAnimator.ofFloat(rootView, "translationX", rootView.translationX, -(btnDelete.width+btnFinish.width+btnEdit.width).toFloat())
                    anime.duration= 200
                    anime.start()
                }
                return true
            }
        })

        init {
            rootView.setOnTouchListener(object :OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    event?.let { gestureDetector.onTouchEvent(it) }
                    return true
                }
            })
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = todoList[position]
        holder.textDate.text = item.overTime
        holder.textEvent.text = item.thing
        val currentDate = Date() // 获取当前日期
        val endDate = SimpleDateFormat("yyyy-MM-dd").parse(item.overTime)

        val overdueColor = ContextCompat.getColor(holder.itemView.context, R.color.overdue_color)
        val nearDeadlineColor = ContextCompat.getColor(holder.itemView.context, R.color.near_deadline_color)
        val notStartedColor = ContextCompat.getColor(holder.itemView.context, R.color.not_started_color)

        val rootView: ViewGroup = holder.itemView.findViewById(R.id.container)

        holder.rootView.setBackgroundColor(
            when {
                isOverdue(endDate!!) -> overdueColor
                isNearDeadline(endDate, thresholdInDays = 7) -> nearDeadlineColor
                else -> notStartedColor
            }
        )

        holder.btnFinish.setOnClickListener {
            val todoId = todoList[position].id // 获取被点击条目的ID
            val prove = todoList[position].prove
            onItemClickListener.finishItem(todoId, prove)

            Log.d("ada","finish $position")
        }

        holder.btnEdit.setOnClickListener {
            val todoId = todoList[position].id // 获取被点击条目的ID
            onItemClickListener.editItem(todoId)
            Log.d("ada","edit $position")
        }

        holder.btnDelete.setOnClickListener {
            val todoId = todoList[position].id // 获取被点击条目的ID
            onItemClickListener.deleteItem(todoId)
            Log.d("ada","delete $position")
            notifyItemMoved(position, todoList.size - 1)
        }
    }


    private fun isOverdue(endDate: Date): Boolean{
        return endDate.before(Date())
    }

    private fun isNearDeadline(endDate: Date, thresholdInDays: Int): Boolean {
        val diffInMilliseconds = Math.abs(endDate.time - Date().time)
        val diff = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds)
        return diff <= thresholdInDays
    }


    override fun getItemCount(): Int {
        return todoList.size
    }

    fun updateTodo(newTodoList: List<Todo>) {
        todoList.clear()
        todoList.addAll(newTodoList)
        notifyDataSetChanged()
    }

    fun sortBystart(newTodoList: List<Todo>){
        todoList.clear()
        todoList.addAll(newTodoList)
        if (todoList.isNotEmpty()) {
            todoList.sortBy {
                LocalDate.parse(it.startTime)
            }
        }
        notifyDataSetChanged()
    }

    fun sortByend(newTodoList: List<Todo>){
        todoList.clear()
        todoList.addAll(newTodoList)
        if (todoList.isNotEmpty()) {
            todoList.sortBy {
                LocalDate.parse(it.overTime)
            }
        }
        notifyDataSetChanged()
    }
    //按照优先级排序，紧急重要>紧急不重要>不紧急重要>不紧急不重要
    fun sortByPriority(newTodoList: List<Todo>){
        todoList.clear()
        todoList.addAll(newTodoList)
        if (todoList.isNotEmpty()) {
            todoList.sortBy {
                when (it.priority) {
                    "紧急且重要" -> 1
                    "紧急但不重要" -> 2
                    "不紧急但重要" -> 3
                    else -> 4
                }
            }
        }
        notifyDataSetChanged()
    }

    fun addTodo(todo: Todo) {
        todoList.add(todo)
        notifyDataSetChanged()
    }
    //获取当前滑动项目在数据库中的ID
    override fun getItemId(position:Int): Long{
        val removedThing = todoList[position]
        return removedThing.id
    }
}

