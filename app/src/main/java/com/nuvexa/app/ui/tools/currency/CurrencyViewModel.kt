package com.nuvexa.app.ui.tools.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvexa.app.data.local.entity.CurrencyRateEntity
import com.nuvexa.app.data.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
) : ViewModel() {

    val rates: StateFlow<List<CurrencyRateEntity>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { repository.seedDefaultsIfEmpty() }
    }

    fun setRate(code: String, rate: Double) {
        viewModelScope.launch { repository.setRate(code, "USD", rate) }
    }
}
