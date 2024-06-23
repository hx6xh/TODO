package com.example.todo.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar


@Entity("tb_Todo")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "title")
    var thing: String,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "start_time")
    var startTime: String,

    @ColumnInfo(name = "over_time")
    var overTime: String,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "priority")
    var priority: String,

    @ColumnInfo(name = "prove")
    var prove: Int,

    @ColumnInfo(name = "status")
    var status: String,

    @ColumnInfo(name = "photo_path")
    var photoPath: String?,

    @ColumnInfo(name = "finish_time")
    var finishTime: String?

) {
    constructor(title: String,
                type: String,
                startTime: String,
                overTime: String,
                description: String?,
                priority: String,
                prove: Int,
                status: String) : this(0,title,type,startTime,overTime,description,priority,prove,status,null,null)
}