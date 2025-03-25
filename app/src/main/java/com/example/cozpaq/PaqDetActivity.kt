package com.example.cozpaq

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PaqDetActivity : AppCompatActivity() {
    private lateinit var textViewIdGuia: TextView
    private lateinit var textViewOrigen: TextView
    private lateinit var textViewDestino: TextView
    private lateinit var textViewEstado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paq_det)

        // Obtener referencias a los TextViews
        textViewIdGuia = findViewById(R.id.textViewIdGuia)
        textViewOrigen = findViewById(R.id.textViewOrigen)
        textViewDestino = findViewById(R.id.textViewDestino)
        textViewEstado = findViewById(R.id.textViewEstado)

        // Obtener el id_guia desde el Intent
        val idGuia = intent.getStringExtra("id_guia")

        if (!idGuia.isNullOrEmpty()) {
            // Mostrar el ID de la guía en el TextView
            textViewIdGuia.text = "ID: $idGuia"
            // Llamar al servidor para obtener más detalles de la guía
            obtenerDetallesGuia(idGuia)
        } else {
            // En caso de que no se reciba un id_guia válido
            Toast.makeText(this, "No se recibió el ID de guía", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerDetallesGuia(idGuia: String) {
        val url = "http://10.43.102.216/api/get_ID_paquetes.php" // URL de la API

        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.has("success") && jsonObject.getBoolean("success")) {
                        val datos = jsonObject.getJSONObject("datos")
                        val origen = datos.getString("origen")
                        val destino = datos.getString("destino")
                        val estado = datos.getString("estado")

                        // Mostrar los detalles en los TextViews
                        textViewOrigen.text = "Origen: $origen"
                        textViewDestino.text = "Destino: $destino"
                        textViewEstado.text = "Estado: $estado"
                    } else {
                        // Si no se encuentran los datos, mostrar mensaje
                        Toast.makeText(this, "Guía no encontrada", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Manejo de error en caso de que la respuesta no sea válida
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Manejo de error en caso de que falle la conexión
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("id_guia" to idGuia)  // Enviar el id_guia como parámetro
            }
        }

        // Agregar la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(request)
    }
}
