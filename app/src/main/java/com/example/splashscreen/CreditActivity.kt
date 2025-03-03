package com.example.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class CreditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.credit_activity)

        val username = intent.getStringExtra("USERNAME") ?: "Usuario"
        val version = "1"
        val nombreApp = "Balón al Poste"

        val creditText: TextView = findViewById(R.id.credit_text)
        creditText.text = "$username, estás usando la versión $version de $nombreApp."

        val contactButton: Button = findViewById(R.id.boton_contactar)
        contactButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("alpostebalon@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Consulta de la app $nombreApp")
                putExtra(Intent.EXTRA_TEXT, "¡Comentanos tus inquietudes!")
            }
            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(emailIntent)
            } else {
                Toast.makeText(this, "No hay aplicaciones de correo disponibles.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
