package com.example.splashscreen

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast

class FavoritosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_favoritos)

        val contenedorFavoritos: LinearLayout = findViewById(R.id.contenedor_favoritos)
        
        // Recuperar y mostrar los mensajes favoritos guardados
        val preferenciasCompartidas = getSharedPreferences("Favoritos", MODE_PRIVATE)
        val favoritos = preferenciasCompartidas.all.entries.groupBy { 
            it.key.substringBeforeLast("_") 
        }
        
        favoritos.forEach { (idMensaje, entradas) ->
            val diseñoMensaje = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(16, 16, 16, 16)
            }

            // Obtener texto e imagen
            val texto = entradas.find { it.key.endsWith("_texto") }?.value as? String
            val uriImagenString = entradas.find { it.key.endsWith("_imagen") }?.value as? String

            // Añadir texto si existe
            texto?.let {
                val vistaTexto = TextView(this).apply {
                    this.text = it
                    setPadding(0, 0, 0, 8)
                }
                diseñoMensaje.addView(vistaTexto)
            }

            // Añadir imagen si existe
            uriImagenString?.let {
                try {
                    val uriImagen = Uri.parse(it)
                    val vistaImagen = ImageView(this).apply {
                        setImageURI(uriImagen)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            500
                        )
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                    diseñoMensaje.addView(vistaImagen)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }

            contenedorFavoritos.addView(diseñoMensaje)
        }
    }
} 