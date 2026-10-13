package com.thupo.domain.usecase

import com.thupo.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class PrepareInitialDataUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return runCatching {
            !userPreferencesRepository.isIntroDone()
        }
    }
}