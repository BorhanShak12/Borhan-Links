package com.borhan.currencygate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                CurrencyGateScreen()
            }
        }
    }
}

@Composable
private fun CurrencyGateScreen(viewModel: CurrencyViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var amount by remember { mutableStateOf("1") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Currency Gate – Live Calculator",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Fetches current exchange rates from open market data endpoints and calculates conversion instantly.",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = fromCurrency,
                onValueChange = { fromCurrency = it.uppercase() },
                label = { Text("From") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = toCurrency,
                onValueChange = { toCurrency = it.uppercase() },
                label = { Text("To") },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                val parsedAmount = amount.toDoubleOrNull()
                if (parsedAmount != null) {
                    viewModel.convert(parsedAmount, fromCurrency.trim(), toCurrency.trim())
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate with Live Rate")
        }

        if (uiState.loading) {
            CircularProgressIndicator()
        }

        uiState.result?.let {
            Text(
                text = "Result: %.4f %s".format(it.convertedAmount, it.toCurrency),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Live rate: 1 %s = %.6f %s".format(it.fromCurrency, it.rate, it.toCurrency)
            )
            Text("Last updated: ${it.timestamp}")
        }

        uiState.error?.let {
            Text(
                text = "Error: $it",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
