package com.example.todo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.room.Room
import com.example.todo.act.CameraActivity
import com.example.todo.data.TodoDatabase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale


class DetailFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var Id:Long = 1
        val title = view.findViewById<TextView>(R.id.text_title)
        val type = view.findViewById<TextView>(R.id.text_type)
        val startDate = view.findViewById<TextView>(R.id.text_startDate)
        val overDate = view.findViewById<TextView>(R.id.text_overDate)
        val description = view.findViewById<TextView>(R.id.text_description)
        val priority = view.findViewById<TextView>(R.id.text_priority)
        val finishWay = view.findViewById<TextView>(R.id.text_finishWay)
        val btnFinish = view.findViewById<Button>(R.id.btn_detail_finish)
        val finishRecord = view.findViewById<TextView>(R.id.text_finishRecord)
        val image = view.findViewById<ImageView>(R.id.daKaImageView)
        arguments?.let { args ->
            Id = args.getLong("todoId")
        }
        val db = Room.databaseBuilder(requireContext(), TodoDatabase::class.java, "tb_Todo")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        val todoDao = db.TodoDao()

        val aThing = todoDao.findThingById(Id)
        val photoPath = aThing.photoPath
        if(aThing.status == "Completed"){
            btnFinish.visibility = View.GONE
            finishRecord.visibility = view.visibility
            val formatTime = aThing.finishTime
            finishRecord.text = "${formatTime}打卡完成"
            if(aThing.prove == 1){

                image.visibility = view.visibility
                val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                if (storageDir.exists() && storageDir.isDirectory) {
                    val bitmap = handleImage(photoPath!!)
                    image.setImageBitmap(bitmap)
                }
            }
        }
        val id = aThing.id
        title.text = aThing.thing
        type.text = aThing.type
        startDate.text = aThing.startTime
        overDate.text = aThing.overTime
        description.text = aThing.description
        priority.text = aThing.priority
        finishWay.text = when(aThing.prove){
            0 -> "无"
            1 -> "拍照打卡"
            else -> "无"
        }
        btnFinish.setOnClickListener {
            if(aThing.prove == 1){
                val bundle = Bundle().apply {
                    putLong("todoId", id) // 将数据放入Bundle
                }
                startActivity(Intent(requireContext(), CameraActivity::class.java).putExtras(bundle))
            }else{
                todoDao.finishThingById(id,"2")
            }
        }
    }

    private fun handleImage(imageFilePath: String): Bitmap? {
        // 从指定路径下读取图片，并获取其EXIF信息
        val exifInterface = ExifInterface(imageFilePath)
        // 获取图片的旋转信息
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val degree = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        return BitmapFactory.decodeFile(imageFilePath, BitmapFactory.Options())?.let { bitmap ->

            val matrix = Matrix()
            matrix.reset()
            matrix.postRotate(degree.toFloat())

            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }

}