package com.thupo.domain.repository

interface UserPreferencesRepository {
    fun isIntroDone(): Boolean
    fun setIntroDone(done: Boolean)
}