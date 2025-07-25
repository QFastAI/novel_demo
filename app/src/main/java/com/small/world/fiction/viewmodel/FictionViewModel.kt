package com.small.world.fiction.viewmodel

import androidx.lifecycle.viewModelScope
import com.aiso.qfast.base.BaseViewModel
import com.aiso.qfast.utils.LogUtils
import com.aiso.qfast.utils.toJson
import com.small.world.fiction.bean.BookShellBean
import com.small.world.fiction.bean.Chapter
import com.small.world.fiction.bean.CreateFictionBean
import com.small.world.fiction.resposotiry.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class FictionViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
): BaseViewModel() {

    private val _createFictionResult = MutableStateFlow<Result<String>?>(null)
    val createFictionResult: StateFlow<Result<String>?> = _createFictionResult

    private val _createChapterResult = MutableStateFlow<Result<String>?>(null)
    val createChapterResult: StateFlow<Result<String>?> = _createChapterResult

    private val _bookShellData = MutableStateFlow<Result<List<CreateFictionBean>>?>(null)
    val bookShellData: StateFlow<Result<List<CreateFictionBean>>?> = _bookShellData

    private val _chapterData = MutableStateFlow<Result<List<Chapter>>?>(null)
    val chapterData: StateFlow<Result<List<Chapter>>?> = _chapterData

    fun createFiction(uuid: String,novel: CreateFictionBean) {
        viewModelScope.launch {
            repository.createFiction(uuid,novel)
                .collect { result ->
                    _createFictionResult.value = result
                }
        }
    }

    fun createChapter(uuid: String,chapter: Chapter) {
        viewModelScope.launch {
            repository.createChapter(uuid,chapter)
                .collect { result ->
                    _createChapterResult.value = result
                }
        }
    }

    fun getBookShellData() {
        viewModelScope.launch {
            repository.getBookShellData()
                .collect { result ->
                    LogUtils.d("getBookShellData===${result.toJson()}")
                    _bookShellData.emit(result)
                }
        }
    }

    fun getChapterData() {
        viewModelScope.launch {
            repository.getChapterData()
                .collect { result ->
                    LogUtils.d("getChapterData===${result.toJson()}")
                    _chapterData.emit(result)
                }
        }
    }

}