package com.example.splashscreen

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.Toast

class FavoritosActivity : AppCompatActivity() {
    private lateinit var databaseHelper: BaseDeDatos
    private lateinit var contenedorFavoritos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_favoritos)

        databaseHelper = BaseDeDatos(this)
        contenedorFavoritos = findViewById(R.id.contenedor_favoritos)

        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        contenedorFavoritos.removeAllViews()
        val favoritos = databaseHelper.obtenerFavoritos()

        if (favoritos.isEmpty()) {
            val textoVacio = TextView(this).apply {
                text = "No tienes mensajes favoritos."
                textSize = 18f
                setPadding(16, 16, 16, 16)
            }
            contenedorFavoritos.addView(textoVacio)
        } else {
            for ((id, texto, imagenUri) in favoritos) {
                mostrarFavoritoEnPantalla(id, texto, imagenUri)
            }
        }
    }

    private fun mostrarFavoritoEnPantalla(id: Int, texto: String, imagenUri: String?) {
        val diseñoMensaje = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 16, 16, 16)
        }

        val diseñoContenido = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
        }

        if (texto.isNotEmpty()) {
            val vistaTexto = TextView(this).apply {
                text = texto
                textSize = 16f
                setPadding(0, 0, 0, 8)
            }
            diseñoContenido.addView(vistaTexto)
        }

        imagenUri?.let {
            val vistaImagen = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500)
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageURI(Uri.parse(it))
            }
            diseñoContenido.addView(vistaImagen)
        }

        val botonQuitarFavorito = ImageButton(this).apply {
            setImageResource(R.drawable.ic_estrella_llena)
            background = null
            setOnClickListener {
                databaseHelper.actualizarFavorito(id, false)
                cargarFavoritos() // Recargar la lista después de quitarlo
                Toast.makeText(this@FavoritosActivity, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
            }
        }

        diseñoMensaje.addView(diseñoContenido)
        diseñoMensaje.addView(botonQuitarFavorito)
        contenedorFavoritos.addView(diseñoMensaje)
    }
}
