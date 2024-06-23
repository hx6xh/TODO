package com.example.todo.act

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.todo.R
import com.example.todo.data.TodoDatabase
import java.io.File
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * CameraActivity 类负责初始化和管理相机功能。
 * 它在创建时检查权限并启动相机，同时提供拍照的功能。
 */
class CameraActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var btn_take: Button
    private lateinit var imageCapture: ImageCapture
    var id: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        //接收bundle数据putExtras
        val receivedBundle = intent.extras
        id = receivedBundle?.getLong("todoId")!!

        // 初始化视图组件
        previewView = findViewById(R.id.preview)
        btn_take = findViewById(R.id.btn_takePhoto)

        // 初始化相机
        cameraExecutor = Executors.newSingleThreadExecutor()

        // 检查是否所有权限都已授予，如果是则启动相机，否则请求权限
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // 设置拍照按钮点击事件
        btn_take.setOnClickListener {
            takePhoto()
        }
    }

    // 启动相机
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // 构建预览配置并绑定到PreviewView
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // 初始化化捕捉界面
            imageCapture = ImageCapture.Builder()
                //优化捕获速度，可能降低图片质量
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                //设置初始的旋转角度
                .setTargetRotation(Surface.ROTATION_0)
                .build()
            // 选择后置摄像头
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // 解绑之前的相机并绑定新的相机配置
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }


    // 实现拍照
    private fun takePhoto() {
        val imageCapture = imageCapture
        val db = Room.databaseBuilder(this, TodoDatabase::class.java, "tb_Todo")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        val todoDao = db.TodoDao()

        // 获取相机提供者实例
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path +
                "/CameraX_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(System.currentTimeMillis()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("takePhoto", "Photo capture failed: ${exc.message}", exc)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "拍照打卡成功"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    val now = Date(System.currentTimeMillis())
                    val st = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                    val fTime = st.format(now)
                    todoDao.finishThingUsingCamera(id, file.absolutePath,fTime)
                    val handler = Handler(Looper.getMainLooper())

                    handler.postDelayed({
                        val intent = Intent(this@CameraActivity, MainActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    }, 1000L)

                }
            })
    }


    /**
     * allPermissionsGranted 方法检查是否所有必需的权限都已被授予。
     * 如果所有权限都被授予，返回 true，否则为 false。
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // 定义常量：请求码和所需权限
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    /**
     * onDestroy 方法负责在活动销毁时关闭相机执行器。
     */
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}