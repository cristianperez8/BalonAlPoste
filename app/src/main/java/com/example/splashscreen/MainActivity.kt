package com.example.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navigateButton: Button = findViewById(R.id.navigate_button)
        val homeButton: Button = findViewById(R.id.inicio_button)
        val usernameInput: EditText = findViewById(R.id.username_input)

        navigateButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val intent = Intent(this@MainActivity, CreditActivity::class.java).apply {
                putExtra("USERNAME", username)
            }
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this@MainActivity, InicioActivity::class.java)
            startActivity(intent)
        }
    }
}
