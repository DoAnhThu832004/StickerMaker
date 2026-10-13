package com.thupo.data.repository

import com.thupo.data.local.pref.AppPreferences
import com.thupo.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val appPreferences: AppPreferences
): UserPreferencesRepository {
    override fun isIntroDone(): Boolean = appPreferences.isIntroDone()
    override fun setIntroDone(done: Boolean) = appPreferences.setInTroDone(done)
}