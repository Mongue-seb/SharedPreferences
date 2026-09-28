package com.example.practica05

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SharedPreferencesScreen()
        }
    }
}

@Composable
fun SharedPreferencesScreen() {
    // 1. Obtener el Contexto y la instancia de SharedPreferences
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("MisDatosPersonales", Context.MODE_PRIVATE)
    }

    // 2. Estados de la interfaz
    var nombre by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var mensajeEstado by remember { mutableStateOf("Esperando acción...") }

    // Función para leer los datos guardados
    fun cargarDatos() {
        nombre = sharedPreferences.getString("KEY_NOMBRE", "") ?: ""
        matricula = sharedPreferences.getString("KEY_MATRICULA", "") ?: ""
    }

    // 3. Cargar automáticamente al abrir la app (Lifecycle)
    LaunchedEffect(Unit) {
        cargarDatos()
        if (nombre.isNotEmpty() || matricula.isNotEmpty()) {
            mensajeEstado = "Datos recuperados de la sesión anterior"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Práctica 5: Persistencia",
            fontSize = 24.sp,
            style = MaterialTheme.typography.titleLarge
        )

        // Campos de texto solicitados
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = matricula,
            onValueChange = { matricula = it },
            label = { Text("Matrícula") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = mensajeEstado,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Fila para los botones principales
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botón Guardar
            Button(
                onClick = {
                    sharedPreferences.edit().apply {
                        putString("KEY_NOMBRE", nombre)
                        putString("KEY_MATRICULA", matricula)
                        apply() // apply() guarda de forma asíncrona (recomendado)
                    }
                    mensajeEstado = "Datos guardados exitosamente"
                    Toast.makeText(context, "Guardado en disco", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }

            // Botón Cargar (manual)
            Button(
                onClick = {
                    cargarDatos()
                    mensajeEstado = "Datos recargados desde el disco"
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cargar")
            }
        }

        // Botón Borrar (Limpieza total)
        OutlinedButton(
            onClick = {
                sharedPreferences.edit().clear().apply() // clear() borra todo el archivo XML
                nombre = ""
                matricula = ""
                mensajeEstado = "Memoria borrada. Listo para nuevos datos."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Borrar Datos", color = MaterialTheme.colorScheme.error)
        }
    }
}