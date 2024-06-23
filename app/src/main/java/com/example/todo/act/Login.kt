package com.example.todo.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.todo.R


class Login : AppCompatActivity() {

    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var rememberCheckbox: CheckBox
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        val decorView = window.decorView
        val flags = decorView.systemUiVisibility
        decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // 初始化控件
        userNameEditText = findViewById(R.id.userName)
        passwordEditText = findViewById(R.id.password)
        rememberCheckbox = findViewById(R.id.rememberPassword)
        btnLogin = findViewById(R.id.btn_login)

        // 初始化用户名和密码
        userNameEditText.setText(R.string.defaultName)
        passwordEditText.setText(generateRandomPassword())

        // 登录成功
        btnLogin.setOnClickListener {
            if (checkInputs() && rememberCheckbox.isChecked) {
                saveLoginStatus(1)
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    // 生成8位随机密码
    private fun generateRandomPassword(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val pw = StringBuilder(8)
        for (i in 0 until 8) {
            pw.append(chars.random())
        }
        return pw.toString()
    }

    // 检查用户名和密码是否为空
    private fun checkInputs(): Boolean {
        if (userNameEditText.text.isEmpty() || passwordEditText.text.isEmpty()) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun saveLoginStatus(status: Int) {
        val sharedPreferences = this.getSharedPreferences("Login", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("loginStatus", status)
            apply()
        }
    }
}
