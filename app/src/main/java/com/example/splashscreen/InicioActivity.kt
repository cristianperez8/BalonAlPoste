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
import android.view.ViewGroup.LayoutParams

class InicioActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.inicio_activity)

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
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
        }
    }
}
