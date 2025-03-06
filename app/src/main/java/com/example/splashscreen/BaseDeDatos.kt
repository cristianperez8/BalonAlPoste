package com.example.splashscreen

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class BaseDeDatos(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MensajesDB"
        private const val DATABASE_VERSION = 7
        const val TABLE_MENSAJES = "mensajes"
        const val TABLE_FAVORITOS = "favoritos"
        const val COLUMN_ID = "id"
        const val COLUMN_TEXTO = "texto"
        const val COLUMN_IMAGEN_URI = "imagen_uri"
        const val COLUMN_USUARIO = "usuario"
        const val COLUMN_MENSAJE_ID = "mensaje_id"
        const val COLUMN_USUARIO_FAVORITO = "usuario_favorito"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            // Crear tabla de mensajes
            val createMensajesQuery = """
                CREATE TABLE IF NOT EXISTS $TABLE_MENSAJES (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_TEXTO TEXT,
                    $COLUMN_IMAGEN_URI TEXT,
                    $COLUMN_USUARIO TEXT NOT NULL
                )
            """.trimIndent()
            
            // Crear tabla de favoritos
            val createFavoritosQuery = """
                CREATE TABLE IF NOT EXISTS $TABLE_FAVORITOS (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_MENSAJE_ID INTEGER,
                    $COLUMN_USUARIO_FAVORITO TEXT NOT NULL,
                    FOREIGN KEY($COLUMN_MENSAJE_ID) REFERENCES $TABLE_MENSAJES($COLUMN_ID) ON DELETE CASCADE,
                    UNIQUE($COLUMN_MENSAJE_ID, $COLUMN_USUARIO_FAVORITO)
                )
            """.trimIndent()

            db.execSQL(createMensajesQuery)
            db.execSQL(createFavoritosQuery)
            Log.d("BaseDeDatos", "Tablas creadas exitosamente")
        } catch (e: Exception) {
            Log.e("BaseDeDatos", "Error al crear las tablas: ${e.message}")
            throw e
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITOS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_MENSAJES")
            onCreate(db)
            Log.d("BaseDeDatos", "Base de datos actualizada correctamente")
        } catch (e: Exception) {
            Log.e("BaseDeDatos", "Error al actualizar la base de datos: ${e.message}")
            throw e
        }
    }

    fun insertarMensaje(texto: String, imagenUri: String?, usuario: String): Long {
        var db: SQLiteDatabase? = null
        try {
            db = this.writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_TEXTO, texto.ifEmpty { " " })
                put(COLUMN_IMAGEN_URI, imagenUri)
                put(COLUMN_USUARIO, usuario)
            }
            
            val id = db.insertOrThrow(TABLE_MENSAJES, null, values)
            Log.d("BaseDeDatos", "Mensaje insertado correctamente con ID: $id")
            return id
        } catch (e: Exception) {
            Log.e("BaseDeDatos", "Error al insertar mensaje: ${e.message}")
            return -1
        } finally {
            db?.close()
        }
    }

    fun obtenerMensajes(): List<MensajeData> {
        val listaMensajes = mutableListOf<MensajeData>()
        var db: SQLiteDatabase? = null
        var cursor: android.database.Cursor? = null
        
        try {
            db = this.readableDatabase
            cursor = db.query(
                TABLE_MENSAJES,
                null,
                null,
                null,
                null,
                null,
                "$COLUMN_ID DESC"
            )

            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val texto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXTO))
                val imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN_URI))
                val usuario = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO))
                listaMensajes.add(MensajeData(id, texto, imagenUri, usuario))
            }
        } catch (e: Exception) {
            Log.e("BaseDeDatos", "Error al obtener mensajes: ${e.message}")
        } finally {
            cursor?.close()
            db?.close()
        }
        return listaMensajes
    }

    fun obtenerFavoritos(nombreUsuario: String): List<MensajeData> {
        val listaFavoritos = mutableListOf<MensajeData>()
        var db: SQLiteDatabase? = null
        var cursor: android.database.Cursor? = null
        
        try {
            db = this.readableDatabase
            val query = """
                SELECT m.* FROM $TABLE_MENSAJES m
                INNER JOIN $TABLE_FAVORITOS f ON m.$COLUMN_ID = f.$COLUMN_MENSAJE_ID
                WHERE f.$COLUMN_USUARIO_FAVORITO = ?
                ORDER BY m.$COLUMN_ID DESC
            """.trimIndent()
            
            cursor = db.rawQuery(query, arrayOf(nombreUsuario))

            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val texto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXTO))
                val imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN_URI))
                val usuario = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO))
                listaFavoritos.add(MensajeData(id, texto, imagenUri, usuario))
            }
        } catch (e: Exception) {
            Log.e("BaseDeDatos", "Error al obtener favoritos: ${e.message}")
        } finally {
            cursor?.close()
            db?.close()
        }
        return listaFavoritos
    }

    fun actualizarFavorito(mensajeId: Int, usuarioFavorito: String, esFavorito: Boolean): Boolean {
        var db: SQLiteDatabase? = null
        try {
            db = this.writableDatabase
            if (esFavorito) {
                val values = ContentValues().apply {
                    put(COLUMN_MENSAJE_ID, mensajeId)
                    put(COLUMN_USUARIO_FAVORITO, usuarioFavorito)
                }
                db.insertWithOnConflict(TABLE_FAVORITOS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
            } else {
                db.delete(TABLE_FAVORITOS, 
                    "$COLUMN_MENSAJE_ID = ? AND $COLUMN_USUARIO_FAVORITO = ?",
                    arrayOf(mensajeId.toString(), usuarioFavorito))
            }
            return true
        } catch (e: Exception) {
            Log.e("BaseDeDatos", "Error al actualizar favorito: ${e.message}")
            return false
        } finally {
            db?.close()
        }
    }

    fun obtenerEstadoFavorito(mensajeId: Int, usuario: String): Boolean {
        var db: SQLiteDatabase? = null
        var cursor: android.database.Cursor? = null
        try {
            db = this.readableDatabase
            cursor = db.query(
                TABLE_FAVORITOS,
                null,
                "$COLUMN_MENSAJE_ID = ? AND $COLUMN_USUARIO_FAVORITO = ?",
                arrayOf(mensajeId.toString(), usuario),
                null,
                null,
                null
            )
            return cursor.moveToFirst()
        } catch (e: Exception) {
            Log.e("BaseDeDatos", "Error al obtener estado favorito: ${e.message}")
            return false
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    fun puedeModificarMensaje(mensajeId: Int, usuario: String): Boolean {
        var db: SQLiteDatabase? = null
        var cursor: android.database.Cursor? = null
        try {
            db = this.readableDatabase
            cursor = db.query(
                TABLE_MENSAJES,
                null,
                "$COLUMN_ID = ? AND $COLUMN_USUARIO = ?",
                arrayOf(mensajeId.toString(), usuario),
                null,
                null,
                null
            )
            return cursor.moveToFirst()
        } catch (e: Exception) {
            Log.e("BaseDeDatos", "Error al verificar permisos: ${e.message}")
            return false
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    fun actualizarMensaje(id: Int, nuevoTexto: String, nuevaImagenUri: String?, usuario: String): Boolean {
        if (!puedeModificarMensaje(id, usuario)) {
            return false
        }

        var db: SQLiteDatabase? = null
        try {
            db = this.writableDatabase
            val valores = ContentValues().apply {
                put(COLUMN_TEXTO, nuevoTexto)
                if (nuevaImagenUri != null) {
                    put(COLUMN_IMAGEN_URI, nuevaImagenUri)
                }
            }
            val resultado = db.update(TABLE_MENSAJES, 
                valores, 
                "$COLUMN_ID = ? AND $COLUMN_USUARIO = ?",
                arrayOf(id.toString(), usuario)
            )
            return resultado > 0
        } catch (e: Exception) {
            Log.e("BaseDeDatos", "Error al actualizar mensaje: ${e.message}")
            return false
        } finally {
            db?.close()
        }
    }

    fun eliminarMensaje(id: Int, usuario: String): Boolean {
        if (!puedeModificarMensaje(id, usuario)) {
            return false
        }

        var db: SQLiteDatabase? = null
        try {
            db = this.writableDatabase
            val resultado = db.delete(TABLE_MENSAJES, 
                "$COLUMN_ID = ? AND $COLUMN_USUARIO = ?", 
                arrayOf(id.toString(), usuario)
            )
            return resultado > 0
        } catch (e: Exception) {
            Log.e("BaseDeDatos", "Error al eliminar mensaje: ${e.message}")
            return false
        } finally {
            db?.close()
        }
    }

    data class MensajeData(
        val id: Int,
        val texto: String,
        val imagenUri: String?,
        val usuario: String
    )
}
