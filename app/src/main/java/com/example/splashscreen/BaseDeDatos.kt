package com.example.splashscreen

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDeDatos(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MensajesDB"
        private const val DATABASE_VERSION = 2
        const val TABLE_MENSAJES = "mensajes"
        const val COLUMN_ID = "id"
        const val COLUMN_TEXTO = "texto"
        const val COLUMN_IMAGEN_URI = "imagen_uri"
        const val COLUMN_ES_FAVORITO = "es_favorito"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_MENSAJES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TEXTO TEXT, " +
                "$COLUMN_IMAGEN_URI TEXT, " +
                "$COLUMN_ES_FAVORITO INTEGER DEFAULT 0)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_MENSAJES ADD COLUMN $COLUMN_ES_FAVORITO INTEGER DEFAULT 0")
        }
    }

    fun insertarMensaje(texto: String, imagenUri: String?): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TEXTO, texto)
            if (imagenUri != null) {
                put(COLUMN_IMAGEN_URI, imagenUri)
            }
            put(COLUMN_ES_FAVORITO, 0)
        }
        val id = db.insert(TABLE_MENSAJES, null, values)
        db.close()
        return id
    }

    fun obtenerMensajes(): List<Triple<Int, String, String?>> {
        val listaMensajes = mutableListOf<Triple<Int, String, String?>>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_MENSAJES, null, null, null, null, null, "$COLUMN_ID DESC")

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val texto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXTO))
            val imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN_URI))
            listaMensajes.add(Triple(id, texto, imagenUri))
        }
        cursor.close()
        return listaMensajes
    }

    fun obtenerFavoritos(): List<Triple<Int, String, String?>> {
        val listaFavoritos = mutableListOf<Triple<Int, String, String?>>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_MENSAJES, null, "$COLUMN_ES_FAVORITO = ?", arrayOf("1"), null, null, "$COLUMN_ID DESC")

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val texto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXTO))
            val imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN_URI))
            listaFavoritos.add(Triple(id, texto, imagenUri))
        }
        cursor.close()
        return listaFavoritos
    }

    fun actualizarFavorito(id: Int, esFavorito: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ES_FAVORITO, if (esFavorito) 1 else 0)
        }
        db.update(TABLE_MENSAJES, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun actualizarMensaje(id: Int, nuevoTexto: String, nuevaImagenUri: String?) {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put(COLUMN_TEXTO, nuevoTexto)
            if (nuevaImagenUri != null) {
                put(COLUMN_IMAGEN_URI, nuevaImagenUri)
            }
        }
        db.update(TABLE_MENSAJES, valores, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
    }

    fun eliminarMensaje(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_MENSAJES, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun obtenerEstadoFavorito(id: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_MENSAJES,
            arrayOf(COLUMN_ES_FAVORITO),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        
        return if (cursor.moveToFirst()) {
            val esFavorito = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ES_FAVORITO))
            cursor.close()
            esFavorito == 1
        } else {
            cursor.close()
            false
        }
    }
}
