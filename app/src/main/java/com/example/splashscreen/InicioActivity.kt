package com.example.splashscreen

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.provider.MediaStore
import android.util.Log

class InicioActivity : AppCompatActivity() {
    private val SELECCIONAR_IMAGEN_REQUEST = 1
    private var uriImagen: Uri? = null
    private lateinit var databaseHelper: BaseDeDatos
    private lateinit var contenedorPrincipal: LinearLayout
    private var imagenSeleccionadaCallback: ((String) -> Unit)? = null
    private lateinit var nombreUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.inicio_activity)

        try {
            // Obtener y validar el nombre de usuario
            nombreUsuario = intent.getStringExtra("USERNAME")?.takeIf { it.isNotEmpty() } 
                ?: throw IllegalStateException("Nombre de usuario no válido")

            Log.d("InicioActivity", "Usuario recibido: $nombreUsuario")

            databaseHelper = BaseDeDatos(this)
            contenedorPrincipal = findViewById(R.id.contenedorPrincipal)

            // Mostrar el nombre de usuario
            Toast.makeText(this, "Bienvenido, $nombreUsuario", Toast.LENGTH_SHORT).show()

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
                try {
                    val intent = Intent(this, FavoritosActivity::class.java).apply {
                        putExtra("USERNAME", nombreUsuario)
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("InicioActivity", "Error al abrir favoritos: ${e.message}")
                    Toast.makeText(this, "Error al abrir favoritos", Toast.LENGTH_SHORT).show()
                }
            }

            // Configurar el botón de subir
            botonSubir.setOnClickListener {
                try {
                    val mensaje = entradaMensaje.text.toString().trim()
                    val imagenUriString = uriImagen?.toString()
                    
                    // Validar que al menos haya texto o imagen
                    if (mensaje.isEmpty() && imagenUriString == null) {
                        Toast.makeText(this, "Por favor, escribe un mensaje o selecciona una imagen", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    Log.d("InicioActivity", "Intentando insertar mensaje. Texto: $mensaje, Imagen: $imagenUriString, Usuario: $nombreUsuario")

                    // Intentar insertar el mensaje
                    val id = databaseHelper.insertarMensaje(mensaje, imagenUriString, nombreUsuario)
                    if (id != -1L) {
                        Log.d("InicioActivity", "Mensaje insertado con ID: $id")
                        mostrarMensajeEnPantalla(id.toInt(), mensaje, imagenUriString, false, nombreUsuario)
                        entradaMensaje.text.clear()
                        uriImagen = null
                        Toast.makeText(this, "Mensaje publicado correctamente", Toast.LENGTH_SHORT).show()
                        
                        // Recargar mensajes después de insertar
                        cargarMensajesGuardados()
                    } else {
                        Log.e("InicioActivity", "Error al insertar mensaje en la base de datos")
                        Toast.makeText(this, "Error al guardar el mensaje", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("InicioActivity", "Error al subir mensaje: ${e.message}")
                    e.printStackTrace()
                    Toast.makeText(this, "Error al subir el mensaje: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            // Cargar mensajes guardados
            cargarMensajesGuardados()

        } catch (e: Exception) {
            Log.e("InicioActivity", "Error en onCreate: ${e.message}")
            Toast.makeText(this, "Error al iniciar la actividad", Toast.LENGTH_SHORT).show()
            finish() // Cerrar la actividad si hay un error crítico
        }
    }

    private fun abrirSelectorImagenes() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), SELECCIONAR_IMAGEN_REQUEST)
    }

    private fun cargarMensajesGuardados() {
        try {
            val mensajes = databaseHelper.obtenerMensajes()
            contenedorPrincipal.removeAllViews()
            for (mensaje in mensajes) {
                val esFavorito = databaseHelper.obtenerEstadoFavorito(mensaje.id, nombreUsuario)
                mostrarMensajeEnPantalla(mensaje.id, mensaje.texto, mensaje.imagenUri, esFavorito, mensaje.usuario)
            }
        } catch (e: Exception) {
            Log.e("InicioActivity", "Error al cargar mensajes: ${e.message}")
            Toast.makeText(this, "Error al cargar los mensajes", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarMensajeEnPantalla(id: Int, texto: String, imagenUri: String?, esFavorito: Boolean, usuario: String) {
        val diseñoMensaje = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
        }

        val diseñoContenido = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
        }

        // Añadir el nombre de usuario
        val vistaUsuario = TextView(this).apply {
            text = "Por: $usuario"
            textSize = 14f
            setTextColor(resources.getColor(android.R.color.darker_gray))
            setPadding(0, 0, 0, 8)
        }
        diseñoContenido.addView(vistaUsuario)

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

        // Solo mostrar botones de edición y eliminación si el mensaje es del usuario actual
        if (usuario == nombreUsuario) {
            val botonEditar = ImageButton(this).apply {
                setImageResource(android.R.drawable.ic_menu_edit)
                background = null
                setOnClickListener {
                    mostrarDialogoEdicion(id, vistaTexto, vistaImagen, imagenUri)
                }
            }
            diseñoMensaje.addView(botonEditar)

            val botonEliminar = ImageButton(this).apply {
                setImageResource(android.R.drawable.ic_menu_delete)
                background = null
                setOnClickListener {
                    if (databaseHelper.eliminarMensaje(id, nombreUsuario)) {
                        contenedorPrincipal.removeView(diseñoMensaje)
                        Toast.makeText(this@InicioActivity, "Mensaje eliminado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@InicioActivity, "No tienes permiso para eliminar este mensaje", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            diseñoMensaje.addView(botonEliminar)
        }

        // El botón de favorito siempre está disponible para todos los usuarios
        val botonFavorito = ImageButton(this).apply {
            setImageResource(if (esFavorito) R.drawable.ic_estrella_llena else R.drawable.ic_estrella_vacia)
            background = null
            tag = esFavorito
            
            setOnClickListener {
                val nuevoEstado = !(tag as Boolean)
                if (databaseHelper.actualizarFavorito(id, nombreUsuario, nuevoEstado)) {
                    setImageResource(if (nuevoEstado) R.drawable.ic_estrella_llena else R.drawable.ic_estrella_vacia)
                    tag = nuevoEstado
                    Toast.makeText(
                        this@InicioActivity, 
                        if (nuevoEstado) "Añadido a favoritos" else "Eliminado de favoritos", 
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@InicioActivity, "Error al actualizar favoritos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        diseñoMensaje.addView(diseñoContenido)
        diseñoMensaje.addView(botonFavorito)
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
            text = "ACTUALIZAR MENSAJE"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            setOnClickListener {
                val nuevoTexto = inputTexto.text.toString()
                if (nuevoTexto.isNotEmpty()) {
                    if (databaseHelper.actualizarMensaje(id, nuevoTexto, imagenUriActual, nombreUsuario)) {
                        vistaTexto.text = nuevoTexto
                        Toast.makeText(this@InicioActivity, "Mensaje actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@InicioActivity, "No tienes permiso para editar este mensaje", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@InicioActivity, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
        }
        layout.addView(botonActualizarMensaje)

        // Botón para cambiar la imagen
        val botonSeleccionarImagen = Button(this).apply {
            text = "CAMBIAR IMAGEN"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                seleccionarNuevaImagen { nuevaUri ->
                    if (databaseHelper.actualizarMensaje(id, vistaTexto.text.toString(), nuevaUri, nombreUsuario)) {
                        vistaImagen?.setImageURI(Uri.parse(nuevaUri))
                        Toast.makeText(this@InicioActivity, "Imagen actualizada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@InicioActivity, "No tienes permiso para editar este mensaje", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        layout.addView(botonSeleccionarImagen)

        dialogo.setView(layout)
        dialogo.setNegativeButton("CERRAR", null)
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
