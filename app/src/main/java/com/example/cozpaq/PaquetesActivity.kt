package com.example.cozpaq

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.google.zxing.integration.android.IntentIntegrator

class PaquetesActivity : AppCompatActivity() {

    private lateinit var editTextGuia: EditText
    private lateinit var buttonBuscar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var btnEscanear: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paquetes)

        editTextGuia = findViewById(R.id.editTextGuia)
        buttonBuscar = findViewById(R.id.buttonBuscar)
        progressBar = findViewById(R.id.progressBar)
        btnEscanear = findViewById(R.id.btnEscanear)

        buttonBuscar.setOnClickListener {
            val idGuia = editTextGuia.text.toString().trim()

            if (idGuia.isEmpty()) {
                Toast.makeText(this, "Ingresa un ID de guía", Toast.LENGTH_SHORT).show()
            } else {
                buscarPaquete(idGuia)  // Llamamos al método con el ID de guía
            }
        }

        btnEscanear.setOnClickListener {
            iniciarEscaneoQR()
        }
    }

    private fun buscarPaquete(idGuia: String) {
        progressBar.visibility = View.VISIBLE

        // URL para realizar la búsqueda del paquete
        val url = "http://10.43.102.216/api/verificar_paquete.php"

        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                progressBar.visibility = View.GONE
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.has("success") && jsonObject.getBoolean("success")) {
                        // Si la respuesta es exitosa, redirigir a la siguiente actividad
                        val intent = Intent(this, PaqDetActivity::class.java)
                        intent.putExtra("id_guia", idGuia)  // Pasamos el id_guia al siguiente activity
                        startActivity(intent)
                    } else {
                        // Mostrar un mensaje si no se encuentra el paquete
                        Toast.makeText(this, "Paquete no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                progressBar.visibility = View.GONE
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

    private fun iniciarEscaneoQR() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea el código QR")
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show()
            } else {
                editTextGuia.setText(result.contents)  // Poner el ID escaneado en el EditText
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
