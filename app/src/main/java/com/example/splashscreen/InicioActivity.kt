package com.example.splashscreen

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.provider.MediaStore

class InicioActivity : AppCompatActivity() {
    private val SELECCIONAR_IMAGEN_REQUEST = 1
    private var uriImagen: Uri? = null
    private lateinit var databaseHelper: BaseDeDatos
    private lateinit var contenedorPrincipal: LinearLayout
    private var imagenSeleccionadaCallback: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.inicio_activity)

        databaseHelper = BaseDeDatos(this)
        contenedorPrincipal = findViewById(R.id.contenedorPrincipal)

        // Inicializar los botones y entrada de mensaje
        val botonSubir = findViewById<Button>(R.id.botonSubir)
        val entradaMensaje = findViewById<EditText>(R.id.entradaMensaje)
        val botonSeleccionarImagen = findViewById<Button>(R.id.botonSeleccionarImagen)
        val botonVerFavoritos = findViewById<Button>(R.id.botonVerFavoritos)

        // Configurar el botón de seleccionar imagen
        botonSeleccionarImagen.setOnClickListener {
            abrirSelectorImagenes()
        }

        // Configurar el botón de ver favoritos
        botonVerFavoritos.setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }

        // Configurar el botón de subir
        botonSubir.setOnClickListener {
            val mensaje = entradaMensaje.text.toString()
            if (mensaje.isNotEmpty() || uriImagen != null) {
                val imagenUriString = uriImagen?.toString()
                val id = databaseHelper.insertarMensaje(mensaje, imagenUriString).toInt()
                mostrarMensajeEnPantalla(id, mensaje, imagenUriString, false)
                entradaMensaje.text.clear()
                uriImagen = null
            } else {
                Toast.makeText(this, "Por favor, escribe un mensaje o selecciona una imagen", Toast.LENGTH_SHORT).show()
            }
        }

        // Cargar mensajes guardados
        cargarMensajesGuardados()
    }

    private fun abrirSelectorImagenes() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), SELECCIONAR_IMAGEN_REQUEST)
    }

    private fun cargarMensajesGuardados() {
        val mensajes = databaseHelper.obtenerMensajes()
        contenedorPrincipal.removeAllViews()
        for ((id, texto, imagenUri) in mensajes) {
            val esFavorito = databaseHelper.obtenerEstadoFavorito(id)
            mostrarMensajeEnPantalla(id, texto, imagenUri, esFavorito)
        }
    }

    private fun mostrarMensajeEnPantalla(id: Int, texto: String, imagenUri: String?, esFavorito: Boolean) {
        val diseñoMensaje = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
        }

        val diseñoContenido = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
        }

        val vistaTexto = TextView(this).apply {
            text = texto
            textSize = 16f
            setPadding(0, 0, 0, 8)
        }
        diseñoContenido.addView(vistaTexto)

        var vistaImagen: ImageView? = null
        if (!imagenUri.isNullOrEmpty()) {
            vistaImagen = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500)
                scaleType = ImageView.ScaleType.FIT_CENTER
                try {
                    setImageURI(Uri.parse(imagenUri))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            diseñoContenido.addView(vistaImagen)
        }

        // Botones de acción
        val botonEditar = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_edit)
            background = null
            setOnClickListener {
                mostrarDialogoEdicion(id, vistaTexto, vistaImagen, imagenUri)
            }
        }

        val botonFavorito = ImageButton(this).apply {
            setImageResource(if (esFavorito) R.drawable.ic_estrella_llena else R.drawable.ic_estrella_vacia)
            background = null
            tag = esFavorito
            
            setOnClickListener {
                val nuevoEstado = !(tag as Boolean)
                setImageResource(if (nuevoEstado) R.drawable.ic_estrella_llena else R.drawable.ic_estrella_vacia)
                databaseHelper.actualizarFavorito(id, nuevoEstado)
                tag = nuevoEstado
                Toast.makeText(
                    this@InicioActivity, 
                    if (nuevoEstado) "Añadido a favoritos" else "Eliminado de favoritos", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val botonEliminar = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_delete)
            background = null
            setOnClickListener {
                databaseHelper.eliminarMensaje(id)
                contenedorPrincipal.removeView(diseñoMensaje)
                Toast.makeText(this@InicioActivity, "Mensaje eliminado", Toast.LENGTH_SHORT).show()
            }
        }

        diseñoMensaje.addView(diseñoContenido)
        diseñoMensaje.addView(botonFavorito)
        diseñoMensaje.addView(botonEditar)
        diseñoMensaje.addView(botonEliminar)
        contenedorPrincipal.addView(diseñoMensaje)
    }

    private fun mostrarDialogoEdicion(id: Int, vistaTexto: TextView, vistaImagen: ImageView?, imagenUriActual: String?) {
        val dialogo = AlertDialog.Builder(this)
        dialogo.setTitle("Editar Mensaje")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val inputTexto = EditText(this).apply {
            setText(vistaTexto.text.toString())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
        }
        layout.addView(inputTexto)

        // Botón para actualizar el mensaje
        val botonActualizarMensaje = Button(this).apply {
            text = "Actualizar Mensaje"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            setOnClickListener {
                val nuevoTexto = inputTexto.text.toString()
                if (nuevoTexto.isNotEmpty()) {
                    databaseHelper.actualizarMensaje(id, nuevoTexto, imagenUriActual)
                    vistaTexto.text = nuevoTexto
                    Toast.makeText(this@InicioActivity, "Mensaje actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@InicioActivity, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
        }
        layout.addView(botonActualizarMensaje)

        // Botón para cambiar la imagen
        val botonSeleccionarImagen = Button(this).apply {
            text = "Cambiar Imagen"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                seleccionarNuevaImagen { nuevaUri ->
                    vistaImagen?.setImageURI(Uri.parse(nuevaUri))
                    databaseHelper.actualizarMensaje(id, vistaTexto.text.toString(), nuevaUri)
                    Toast.makeText(this@InicioActivity, "Imagen actualizada", Toast.LENGTH_SHORT).show()
                }
            }
        }
        layout.addView(botonSeleccionarImagen)

        dialogo.setView(layout)
        dialogo.setNegativeButton("Cerrar", null)
        dialogo.show()
    }

    private fun seleccionarNuevaImagen(callback: (String) -> Unit) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), SELECCIONAR_IMAGEN_REQUEST)
        imagenSeleccionadaCallback = callback
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECCIONAR_IMAGEN_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    
                    if (imagenSeleccionadaCallback != null) {
                        imagenSeleccionadaCallback?.invoke(uri.toString())
                        imagenSeleccionadaCallback = null
                    } else {
                        uriImagen = uri
                        Toast.makeText(this, "Imagen seleccionada correctamente", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al seleccionar la imagen", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }
}
