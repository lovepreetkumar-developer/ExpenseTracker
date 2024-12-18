package com.expense.tracker.network

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExchangeRateViewModel : ViewModel() {
    var exchangeRates = mutableStateOf<ExchangeRateResponse?>(null)
    var errorMessage = mutableStateOf<String?>(null)

    fun fetchExchangeRates() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getExchangeRates()
                exchangeRates.value = response // Update the state
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}
