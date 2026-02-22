package com.borhan.currencygate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun convert(amount: Double, from: String, to: String) {
        if (from.isBlank() || to.isBlank()) {
            _uiState.value = CurrencyUiState(error = "Currency codes cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            runCatching {
                CurrencyApiFactory.api.latest(
                    amount = amount,
                    from = from,
                    to = to
                )
            }.onSuccess { response ->
                val toRate = response.rates[to]
                if (toRate == null) {
                    _uiState.value = CurrencyUiState(
                        loading = false,
                        error = "No rate received for $to"
                    )
                    return@onSuccess
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
            }.onFailure {
                _uiState.value = CurrencyUiState(
                    loading = false,
                    error = it.message ?: "Unknown API error"
                )
            }
        }
    }
}
