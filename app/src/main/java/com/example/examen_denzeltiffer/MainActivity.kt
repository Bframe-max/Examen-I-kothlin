package com.example.examen_denzeltiffer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Declaramos las variables para los componentes de la UI (Interfaz de Usuario)
    private lateinit var etNombreCompleto: EditText
    private lateinit var etSalarioMensual: EditText
    private lateinit var btnCalcular: Button
    private lateinit var btnNuevo: Button
    private lateinit var btnSalir: Button
    private lateinit var tvResultadoINSS: TextView
    private lateinit var tvResultadoIR: TextView
    private lateinit var tvTotalDeducciones: TextView
    private lateinit var tvSalarioNeto: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enlazamos las variables con los componentes del XML usando su ID
        etNombreCompleto = findViewById(R.id.etNombreCompleto)
        etSalarioMensual = findViewById(R.id.etSalarioMensual)
        btnCalcular = findViewById(R.id.btnCalcular)
        btnNuevo = findViewById(R.id.btnNuevo)
        btnSalir = findViewById(R.id.btnSalir)
        tvResultadoINSS = findViewById(R.id.tvResultadoINSS)
        tvResultadoIR = findViewById(R.id.tvResultadoIR)
        tvTotalDeducciones = findViewById(R.id.tvTotalDeducciones)
        tvSalarioNeto = findViewById(R.id.tvSalarioNeto)

        // Configuramos los "listeners" para los botones.
        // Esto define qué acción se ejecuta cuando el usuario hace clic en cada botón.

        // 1. Botón Calcular
        btnCalcular.setOnClickListener {
            realizarCalculos()
        }

        // 2. Botón Nuevo
        btnNuevo.setOnClickListener {
            limpiarCampos()
        }

        // 3. Botón Salir
        btnSalir.setOnClickListener {
            finish() // Cierra la aplicación
        }
    }

    /**
     * Función principal que valida las entradas y ejecuta los cálculos.
     */
    private fun realizarCalculos() {
        val nombre = etNombreCompleto.text.toString()
        val salarioStr = etSalarioMensual.text.toString()

        // --- VALIDACIÓN DE ENTRADAS ---
        if (nombre.isBlank()) {
            Toast.makeText(this, "Por favor, ingrese el nombre completo.", Toast.LENGTH_SHORT).show()
            etNombreCompleto.requestFocus() // Pone el foco en el campo del nombre
            return
        }

        if (salarioStr.isBlank()) {
            Toast.makeText(this, "Por favor, ingrese el salario mensual.", Toast.LENGTH_SHORT).show()
            etSalarioMensual.requestFocus() // Pone el foco en el campo del salario
            return
        }

        // Convertimos el salario a un número. Usamos toDoubleOrNull para evitar errores si el texto no es un número.
        val salarioMensual = salarioStr.toDoubleOrNull()
        if (salarioMensual == null || salarioMensual <= 0) {
            Toast.makeText(this, "Por favor, ingrese un salario válido y mayor que cero.", Toast.LENGTH_SHORT).show()
            return
        }

        // --- CÁLCULOS DE LEY (NICARAGUA) ---

        // 1. Cálculo del INSS Laboral (7% del salario bruto)
        val inss = salarioMensual * 0.07

        // 2. Cálculo del IR (Impuesto sobre la Renta)
        // El IR se calcula sobre el salario después de deducir el INSS.
        val salarioNetoAntesDeIR = salarioMensual - inss
        val salarioAnual = salarioNetoAntesDeIR * 12

        val irAnual = calcularIR(salarioAnual)
        val irMensual = irAnual / 12

        // 3. Cálculo de Deducciones y Salario Neto
        val totalDeducciones = inss + irMensual
        val salarioNeto = salarioMensual - totalDeducciones

        // --- MOSTRAR RESULTADOS EN PANTALLA ---
        mostrarResultados(inss, irMensual, totalDeducciones, salarioNeto)
    }

    /**
     * Calcula el Impuesto sobre la Renta (IR) anual basado en la tabla progresiva de Nicaragua.
     */
    private fun calcularIR(salarioAnual: Double): Double {
        return when {
            salarioAnual <= 100000.00 -> 0.0 // Exento
            salarioAnual <= 200000.00 -> (salarioAnual - 100000.00) * 0.15
            salarioAnual <= 350000.00 -> ((salarioAnual - 200000.00) * 0.20) + 15000.00
            salarioAnual <= 500000.00 -> ((salarioAnual - 350000.00) * 0.25) + 45000.00
            else -> ((salarioAnual - 500000.00) * 0.30) + 82500.00
        }
    }

    /**
     * Formatea los números a moneda y los muestra en los TextViews correspondientes.
     */
    private fun mostrarResultados(inss: Double, ir: Double, deducciones: Double, neto: Double) {
        val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "NI")) // Formato para Córdoba (C$)

        tvResultadoINSS.text = "INSS Laboral: ${formatoMoneda.format(inss)}"
        tvResultadoIR.text = "IR Mensual: ${formatoMoneda.format(ir)}"
        tvTotalDeducciones.text = "Total Deducciones: ${formatoMoneda.format(deducciones)}"
        tvSalarioNeto.text = "Salario Neto a Recibir: ${formatoMoneda.format(neto)}"
    }

    /**
     * Limpia todos los campos de entrada y de resultados para un nuevo cálculo.
     */
    private fun limpiarCampos() {
        etNombreCompleto.text.clear()
        etSalarioMensual.text.clear()
        tvResultadoINSS.text = "INSS Laboral: C$ 0.00"
        tvResultadoIR.text = "IR Mensual: C$ 0.00"
        tvTotalDeducciones.text = "Total Deducciones: C$ 0.00"
        tvSalarioNeto.text = "Salario Neto a Recibir: C$ 0.00"
        etNombreCompleto.requestFocus() // Pone el cursor de nuevo en el primer campo
    }
}