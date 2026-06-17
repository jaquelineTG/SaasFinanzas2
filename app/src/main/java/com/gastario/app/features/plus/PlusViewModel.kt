package com.gastario.app.features.plus

import androidx.lifecycle.ViewModel
import com.gastario.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class PlusViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel()

{



}