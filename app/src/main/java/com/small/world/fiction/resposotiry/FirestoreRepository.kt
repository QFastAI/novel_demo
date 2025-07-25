package com.small.world.fiction.resposotiry

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseUser
import com.small.world.fiction.bean.Chapter
import com.small.world.fiction.bean.CreateFictionBean
import com.small.world.fiction.firestore.FirestoreManager
import kotlinx.coroutines.flow.Flow

class FirestoreRepository {

    fun createFiction(uuid:String,fiction: CreateFictionBean): Flow<Result<String>> {
        return FirestoreManager.createNovel(uuid,fiction)
    }

    fun createChapter(uuid:String,fiction: Chapter): Flow<Result<String>> {
        return FirestoreManager.createChapter(uuid,fiction)
    }

    fun getChapterData(): Flow<Result<List<Chapter>>> {
        return FirestoreManager.getChapterData()
    }

    fun getBookShellData(): Flow<Result<List<CreateFictionBean>>> {
        return FirestoreManager.getBookShellData()
    }

    fun addUser(@SuppressLint("RestrictedApi") user: FirebaseUser): Flow<Result<String>> {
        return FirestoreManager.addUser(user)
    }

    @SuppressLint("RestrictedApi")
    fun getUsers(): Flow<Result<List<FirebaseUser>>> {
        return FirestoreManager.getUsers()
    }
}