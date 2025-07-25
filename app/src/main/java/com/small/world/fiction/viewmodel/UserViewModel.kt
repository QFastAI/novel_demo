package com.small.world.fiction.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiso.qfast.base.BaseViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import com.small.world.fiction.resposotiry.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : BaseViewModel() {

    private val _addUserResult = MutableStateFlow<Result<String>?>(null)
    val addUserResult: StateFlow<Result<String>?> = _addUserResult

    private val _userList = MutableStateFlow<Result<List<FirebaseUser>>?>(null)
    @SuppressLint("RestrictedApi")
    val userList: StateFlow<Result<List<FirebaseUser>>?> = _userList

    fun addUser(@SuppressLint("RestrictedApi") user: FirebaseUser) {
        viewModelScope.launch {
            repository.addUser(user)
                .collect { result ->
                    _addUserResult.value = result
                }
        }
    }

    fun observeUsers() {
        viewModelScope.launch {
            repository.getUsers()
                .collect { result ->
                    _userList.value = result
                }
        }
    }
}
