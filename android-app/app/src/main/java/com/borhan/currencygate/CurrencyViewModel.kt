package com.borhan.currencygate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConversionResult(
    val convertedAmount: Double,
    val fromCurrency: String,
    val toCurrency: String,
    val rate: Double,
    val timestamp: String
)

data class CurrencyUiState(
    val loading: Boolean = false,
    val result: ConversionResult? = null,
    val error: String? = null
)

class CurrencyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState
    private var convertJob: Job? = null

    fun convert(amount: Double, from: String, to: String) {
        if (from.isBlank() || to.isBlank()) {
            _uiState.value = CurrencyUiState(error = "Currency codes cannot be empty")
            return
        }

        convertJob?.cancel()
        convertJob = viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                val response = CurrencyApiFactory.api.latest(
                    amount = amount,
                    from = from,
                    to = to
                )
                val toRate = response.rates[to]
                if (toRate == null) {
                    _uiState.value = CurrencyUiState(
                        loading = false,
                        error = "No rate received for $to"
                    )
                    return@launch
                }

                val normalizedRate = toRate / response.amount
                _uiState.value = CurrencyUiState(
                    loading = false,
                    result = ConversionResult(
                        convertedAmount = toRate,
                        fromCurrency = from,
                        toCurrency = to,
                        rate = normalizedRate,
                        timestamp = response.date
                    )
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = CurrencyUiState(
                    loading = false,
                    error = e.message ?: "Unknown API error"
                )
            }
        }
    }
}
