package com.example.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast

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
            val username = usernameInput.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(this, "Por favor, introduce un nombre de usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val intent = Intent(this@MainActivity, CreditActivity::class.java).apply {
                    putExtra("USERNAME", username)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al abrir créditos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        homeButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(this, "Por favor, introduce un nombre de usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val intent = Intent(this, InicioActivity::class.java).apply {
                    putExtra("USERNAME", username)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al abrir inicio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
