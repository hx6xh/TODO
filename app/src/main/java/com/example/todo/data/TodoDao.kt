package com.example.todo.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {
    // 添加一条数据
    @Insert
    fun insertThing(todo: Todo)

    // 根据id查找对应的数据
    @Query("select * from tb_Todo where id = :Id")
    fun findThingById(Id: Long): Todo

    // 完成打卡（普通）
    @Query("update tb_Todo set status = 'Completed',finish_time = :fTime where id = :Id")
    fun finishThingById(Id: Long,fTime:String)

    // 完成打卡（拍照打卡）
    @Query("update tb_Todo set status = 'Completed',photo_path= :photoPath, finish_time = :fTime  where id = :Id")
    fun finishThingUsingCamera(Id: Long, photoPath: String, fTime: String)

    // 根据id更新某条数据
    @Query("update tb_Todo set title = :title,type = :type,start_time = :startDate,over_time = :overDate,description = :description,priority = :priority,prove = :prove,status = :status where id = :Id")
    fun updateThingById(Id: Long,title:String,type:String,startDate:String,overDate:String,description:String,priority:String,prove:Int,status:String)

    // 根据id删除某项数据
    @Query("delete from tb_Todo where id = :Id")
    fun deleteThing(Id: Long)

    // 删除全部数据
    @Query("delete from tb_Todo")
    fun deleteAllThings()

    //查询所有未完成的数据
    @Query("select * from tb_Todo where status != 'Completed' order by id asc")
    fun queryAllThings():List<Todo>

    //查询所有完成了的数据
    @Query("select * from tb_Todo where status = 'Completed' order by id asc")
    fun queryFinishedThings():List<Todo>

    //查询已完成的数量
    @Query("SELECT COUNT(*) FROM tb_Todo WHERE status = 'Completed'")
    fun countCompletedTasks(): Int

    //查询未完成的数量
    @Query("SELECT COUNT(*) FROM tb_Todo WHERE status != 'Completed'")
    fun countUncompletedTasks(): Int

    //根据选择的开始时间和结束时间的时间段，查询里面已完成的数量
    @Query("SELECT COUNT(*) FROM tb_Todo WHERE status = 'Completed' AND finish_time >= :startDate AND finish_time <= :endDate")
    fun countCompletedTasksInTimeRange(startDate: String, endDate: String): Int

    @Query("SELECT COUNT(*) FROM tb_Todo WHERE status != 'Completed' AND over_time >= :startDate AND over_time <= :endDate")
    fun countUncompletedTasksInTimeRange(startDate: String, endDate: String): Int
}