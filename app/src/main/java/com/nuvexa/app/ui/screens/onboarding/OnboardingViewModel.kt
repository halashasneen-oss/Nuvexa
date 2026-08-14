package com.nuvexa.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvexa.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            settingsRepository.setOnboardingComplete(true)
            onDone()
        }
    }
}
