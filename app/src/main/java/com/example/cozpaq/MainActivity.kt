package com.example.cozpaq

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usuario = findViewById<EditText>(R.id.etUser)
        val contraseña = findViewById<EditText>(R.id.etPassword)
        val botonLogear = findViewById<Button>(R.id.btnLogin)

        botonLogear.setOnClickListener {
            val userText = usuario.text.toString().trim()
            val passText = contraseña.text.toString().trim()

            if (userText.isNotEmpty() && passText.isNotEmpty()) {
                loginBDVolley(userText, passText)
            } else {
                Toast.makeText(this, "Escriba el usuario/contraseña.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loginBDVolley(usuario: String, contraseña: String) {
        val url = "http://10.43.102.216/api/get_login.php"

        val peticionPost = object : StringRequest(Method.POST, url, Response.Listener { response ->
            try {
                val respuesta = JSONObject(response)
                val success = respuesta.getBoolean("success")

                if (success) {
                    Toast.makeText(this, "Login exitoso.", Toast.LENGTH_LONG).show()
                    redirigirPaquetesActivity()
                } else {
                    val mensajeError = respuesta.optString("message", "Error desconocido")
                    Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
                }
            } catch (ex: Exception) {
                Log.e("LOGIN_ERROR", "Error en respuesta JSON: ${ex.message}")
                Toast.makeText(this, "Error en respuesta JSON.", Toast.LENGTH_LONG).show()
            }
        }, Response.ErrorListener { error ->
            Log.e("LOGIN_ERROR", "Error de conexión: ${error.message}")
            Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("usuario" to usuario, "contraseña" to contraseña)
            }
        }

        Volley.newRequestQueue(this).add(peticionPost)
    }

    private fun redirigirPaquetesActivity() {
        val intent = Intent(this, PaquetesActivity::class.java)
        startActivity(intent)
    }
}