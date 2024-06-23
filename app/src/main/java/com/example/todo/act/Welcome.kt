package com.example.todo.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.todo.R

class Welcome: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val loginStatus = getLoginStatus()
        window.statusBarColor = ContextCompat.getColor(this, R.color.softYellow) //状态栏设置颜色

        object : CountDownTimer(1000, 1000) { // 3秒后结束，每隔1秒检查一次
            override fun onTick(millisUntilFinished: Long) {
                //
            }

            override fun onFinish() {
                if(loginStatus == 1){
                    startActivity(Intent(this@Welcome, MainActivity::class.java))
                }else{
                    startActivity(Intent(this@Welcome, Login::class.java))

                }
                finish() // 如果你想关闭当前Activity
            }
        }.start()

    }

    // 获取登录状态
    private fun getLoginStatus(): Int {
        val preferences = this.getSharedPreferences("Login", Context.MODE_PRIVATE)
        return preferences.getInt("loginStatus", 0)
    }

}