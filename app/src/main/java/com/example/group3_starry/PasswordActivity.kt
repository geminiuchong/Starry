package com.example.group3_starry

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PasswordActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val passwordToggle = findViewById<ImageView>(R.id.passwordVisibilityToggle)
        val nextButton = findViewById<ImageView>(R.id.nextButton)

        // 密码可见性切换功能
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // 显示密码
                passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_visibility) // 更新为可见图标
            } else {
                // 隐藏密码
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_visibility_off) // 更新为不可见图标
            }
            passwordInput.setSelection(passwordInput.text.length) // 保持光标位置
        }

        // 点击右下角箭头
        nextButton.setOnClickListener {
            val password = passwordInput.text.toString()

            // 校验密码是否符合规则
            if (password.length >= 6) {
                // 跳转到 Sign Up Activity
                val intent = Intent(this, SignUpActivity::class.java) // 确保引用实际存在的 SignUpActivity
                intent.putExtra("password", password) // 将密码传递给下一步
                startActivity(intent)
            } else {
                // 显示错误提示
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
