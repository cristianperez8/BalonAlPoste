package com.example.splashscreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.provider.MediaStore
<<<<<<< HEAD
import android.view.ViewGroup.LayoutParams

class InicioActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
=======
import android.widget.ImageButton

class InicioActivity : AppCompatActivity() {
    private val SELECCIONAR_IMAGEN_REQUEST = 1
    private var uriImagen: Uri? = null
    private var contadorMensajes = 0
>>>>>>> 4939a4b (LISTO PARA ENVIAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.inicio_activity)

<<<<<<< HEAD
        val messageInput: EditText = findViewById(R.id.message_input)
        val uploadButton: Button = findViewById(R.id.upload_button)
        val selectImageButton: Button = findViewById(R.id.selecionar_imagen)
        val contentContainer: LinearLayout = findViewById(R.id.content_container)

        // Manejar la selección de imágenes
        selectImageButton.setOnClickListener {
            openGallery()
        }

        // Manejar la subida de mensajes e imágenes
        uploadButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                val textView = TextView(this).apply {
                    text = message
                    setPadding(16, 16, 16, 16)
                }
                contentContainer.addView(textView)
                messageInput.text.clear()
            }

            // Agregar la imagen seleccionada con tamaño máximo y ajustada
            imageUri?.let {
                val imageView = ImageView(this).apply {
                    setImageURI(it)
                    setPadding(16, 16, 16, 16)
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500).apply {
                        setMargins(16, 16, 16, 16)
                    }
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
                contentContainer.addView(imageView)
                imageUri = null
=======
        val entradaMensaje: EditText = findViewById(R.id.entrada_mensaje)
        val botonSubir: Button = findViewById(R.id.boton_subir)
        val botonSeleccionarImagen: Button = findViewById(R.id.boton_seleccionar_imagen)
        val contenedorPrincipal: LinearLayout = findViewById(R.id.contenedor_principal)
        val botonVerFavoritos: Button = findViewById(R.id.boton_ver_favoritos)

        // Manejar la selección de imágenes
        botonSeleccionarImagen.setOnClickListener {
            abrirGaleria()
        }

        // Configurar el botón de ver favoritos
        botonVerFavoritos.setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }

        // Manejar la subida de mensajes e imágenes
        botonSubir.setOnClickListener {
            val mensaje = entradaMensaje.text.toString()
            if (mensaje.isNotEmpty() || uriImagen != null) {
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
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                    )
                }

                // Guardar la URI de la imagen actual
                val uriImagenActual = uriImagen?.toString()

                if (mensaje.isNotEmpty()) {
                    val vistaTexto = TextView(this).apply {
                        text = mensaje
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                    diseñoContenido.addView(vistaTexto)
                }

                // Crear la vista de imagen si hay una imagen
                uriImagen?.let {
                    val vistaImagen = ImageView(this).apply {
                        setImageURI(it)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            500
                        ).apply {
                            setMargins(0, 8, 0, 0)
                        }
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                    diseñoContenido.addView(vistaImagen)
                }

                val botonEstrella = ImageButton(this).apply {
                    setImageResource(R.drawable.ic_estrella_vacia)
                    background = null
                    val idMensaje = "mensaje_${contadorMensajes++}"
                    tag = idMensaje
                    setOnClickListener { vista ->
                        val esFavorito = vista.tag as? String == idMensaje + "_favorito"
                        if (esFavorito) {
                            setImageResource(R.drawable.ic_estrella_vacia)
                            vista.tag = idMensaje
                            // Eliminar de favoritos
                            getSharedPreferences("Favoritos", MODE_PRIVATE).edit()
                                .remove("${idMensaje}_texto")
                                .remove("${idMensaje}_imagen")
                                .apply()
                        } else {
                            setImageResource(R.drawable.ic_estrella_llena)
                            vista.tag = idMensaje + "_favorito"
                            // Guardar en favoritos
                            getSharedPreferences("Favoritos", MODE_PRIVATE).edit().apply {
                                if (mensaje.isNotEmpty()) {
                                    putString("${idMensaje}_texto", mensaje)
                                }
                                uriImagenActual?.let { uri ->
                                    putString("${idMensaje}_imagen", uri)
                                }
                            }.apply()
                        }
                    }
                }

                diseñoMensaje.addView(diseñoContenido)
                diseñoMensaje.addView(botonEstrella)
                contenedorPrincipal.addView(diseñoMensaje)
                entradaMensaje.text.clear()
                uriImagen = null
>>>>>>> 4939a4b (LISTO PARA ENVIAR)
            }
        }
    }

<<<<<<< HEAD
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
=======
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECCIONAR_IMAGEN_REQUEST)
>>>>>>> 4939a4b (LISTO PARA ENVIAR)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
<<<<<<< HEAD
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
=======
        if (requestCode == SELECCIONAR_IMAGEN_REQUEST && resultCode == Activity.RESULT_OK) {
            uriImagen = data?.data
>>>>>>> 4939a4b (LISTO PARA ENVIAR)
        }
    }
}
