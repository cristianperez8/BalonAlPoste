package com.example.splashscreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditarMensajeActivity : AppCompatActivity() {
    private lateinit var editTextMensaje: EditText
    private lateinit var imageViewMensaje: ImageView
    private lateinit var buttonGuardar: Button
    private lateinit var buttonSeleccionarImagen: Button

    private var idMensaje: Int = -1
    private var imagenUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_mensaje)

        editTextMensaje = findViewById(R.id.editTextMensaje)
        imageViewMensaje = findViewById(R.id.imageViewMensaje)
        buttonGuardar = findViewById(R.id.buttonGuardar)
        buttonSeleccionarImagen = findViewById(R.id.buttonSeleccionarImagen)

        // Obtener datos del intent
        idMensaje = intent.getIntExtra("idMensaje", -1)
        val textoMensaje = intent.getStringExtra("textoMensaje")
        imagenUri = intent.getStringExtra("imagenUri")

        editTextMensaje.setText(textoMensaje)
        if (imagenUri != null) {
            imageViewMensaje.setImageURI(Uri.parse(imagenUri))
        }

        // Botón para cambiar la imagen
        buttonSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        // Botón para guardar cambios
        buttonGuardar.setOnClickListener {
            val nuevoTexto = editTextMensaje.text.toString()
            val resultadoIntent = Intent()
            resultadoIntent.putExtra("idMensaje", idMensaje)
            resultadoIntent.putExtra("nuevoTexto", nuevoTexto)
            resultadoIntent.putExtra("nuevaImagenUri", imagenUri)
            setResult(Activity.RESULT_OK, resultadoIntent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                imagenUri = uri.toString()
                imageViewMensaje.setImageURI(uri)
            }
        }
    }
}
