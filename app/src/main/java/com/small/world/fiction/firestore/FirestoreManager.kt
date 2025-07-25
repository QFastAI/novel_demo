@file:Suppress("UNCHECKED_CAST")

package com.small.world.fiction.firestore

import android.annotation.SuppressLint
import com.aiso.qfast.utils.LogUtils
import com.aiso.qfast.utils.toJson
import com.blankj.utilcode.util.GsonUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.small.world.fiction.bean.Chapter
import com.small.world.fiction.bean.Cover
import com.small.world.fiction.bean.CreateFictionBean
import com.small.world.fiction.bean.GenerationDetails
import com.small.world.fiction.bean.OutlineBean
import com.small.world.fiction.bean.RequestDetails
import com.small.world.fiction.bean.Title
import com.small.world.fiction.bean.Trailer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID

object FirestoreManager {
    @SuppressLint("StaticFieldLeak")
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val fictionsCollection = firestore.collection("qf_novels")
    private val chapterCollection = firestore.collection("qf_chapters")

    fun createNovel(uuid:String,novel:CreateFictionBean): Flow<Result<String>> = callbackFlow {
        val document = fictionsCollection.document(uuid)
        trySend(Result.success("正在添加..."))
        document.set(novel)
            .addOnSuccessListener {
                trySend(Result.success("添加成功"))
                close()
            }
            .addOnFailureListener {
                trySend(Result.failure(it))
                close()
            }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    fun createChapter(uuid: String,chapter: Chapter): Flow<Result<String>> = callbackFlow {
        val document = chapterCollection.document(uuid)
        trySend(Result.success("正在添加..."))
        document.set(chapter)
            .addOnSuccessListener {
                trySend(Result.success("添加成功"))
                close()
            }
            .addOnFailureListener {
                trySend(Result.failure(it))
                close()
            }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    fun getChapterData(): Flow<Result<List<Chapter>>> = callbackFlow {
        var listener:ListenerRegistration? = null
        listener = chapterCollection.addSnapshotListener { querySnapshot, e ->
            if(e != null){
                trySend(Result.failure(e)).isSuccess
                return@addSnapshotListener
            }
            val resultList = mutableListOf<Chapter>()

            querySnapshot?.documents?.forEach { doc ->
                val fiction = GsonUtils.fromJson(doc.data.toJson(), Chapter::class.java)
                if(fiction != null){
                    resultList.add(fiction)
                }
                trySend(Result.success(resultList)).isSuccess
            }
        }
        awaitClose {
            listener.remove()
        }
     }

    fun getBookShellData(): Flow<Result<List<CreateFictionBean>>> = callbackFlow {
        var listener:ListenerRegistration? = null
        listener = fictionsCollection.addSnapshotListener { querySnapshot, e ->
            if(e != null){
                trySend(Result.failure(e)).isSuccess
                return@addSnapshotListener
            }
            val resultList = mutableListOf<CreateFictionBean>()

            querySnapshot?.documents?.forEach { doc ->
                val docId = doc.id // 文档 ID、
                val itemType = (doc.getLong("itemType") ?: 1).toInt()
                val tags = doc.get("tags") as? List<String> ?: emptyList()
                val requested_by = doc.getString("requested_by") ?: ""
                val requested_at = doc.getString("requested_at") ?: ""

                val requestDetailsMap = doc.get("request_details") as? Map<String, Any>
                val requestDetails = requestDetailsMap?.let {
                    RequestDetails(
                        targetAudience = it["targetAudience"] as? String ?: "",
                        plots = it["plots"] as? List<String> ?: emptyList(),
                        roles = it["roles"] as? List<String> ?: emptyList(),
                        themes = it["themes"] as? List<String> ?: emptyList()
                    )
                }

                val generationDetailsMap = doc.get("generation_details") as? Map<String, Any>
                val generationDetails = generationDetailsMap?.let {
                    GenerationDetails(
                        outline_gen_prompt = it["outline_gen_prompt"] as? String ?: ""
                    )
                }

                val titleMap = doc.get("title") as? Map<String, Any>
                val title = titleMap?.let {
                    Title(
                        status = it["status"] as? String ?: "",
                        content = it["content"] as? String ?: ""
                    )
                }

                val outlineMap = doc.get("outline") as? Map<String, Any>
                val outline = outlineMap?.let {
                    OutlineBean(
                        status = it["status"] as? String ?: "",
                        content = it["content"] as? String ?: ""
                    )
                }

                val coverMap = doc.get("cover") as? Map<String, Any>
                val cover = coverMap?.let {
                    Cover(
                        status = it["status"] as? String ?: "",
                        url = it["url"] as? String ?: ""
                    )
                }

                val trailerMap = doc.get("trailer") as? Map<String, Any>
                val trailer = trailerMap?.let {
                    Trailer(
                        status = it["status"] as? String ?: "",
                        url = it["url"] as? String ?: ""
                    )
                }

                // 构造最终 CreateFictionBean
                val fiction = CreateFictionBean(
                    itemType = itemType,
                    novel_id = docId,
                    tags = tags,
                    requested_by = requested_by,
                    requested_at = requested_at,
                    request_details = requestDetails,
                    generation_details = generationDetails,
                    title = title,
                    outline = outline,
                    cover = cover,
                    trailer = trailer
                )

                resultList.add(fiction)
            }

            trySend(Result.success(resultList)).isSuccess
        }
        awaitClose{
            listener.remove()
        }
    }.flowOn(Dispatchers.IO)

    // 添加用户
    fun addUser(@SuppressLint("RestrictedApi") user: FirebaseUser): Flow<Result<String>> = callbackFlow {
        trySend(Result.success("正在添加..."))
        usersCollection.add(user)
            .addOnSuccessListener { docRef ->
                trySend(Result.success("添加成功，ID: ${docRef.id}"))
                close() // 记得关闭流
            }
            .addOnFailureListener { e ->
                trySend(Result.failure(e))
                close() // 出错也要关闭
            }

        awaitClose {} // 保持 callbackFlow 正常运行直到关闭
    }.flowOn(Dispatchers.IO)


    // 获取所有用户
    @SuppressLint("RestrictedApi")
    fun getUsers(): Flow<Result<List<FirebaseUser>>> = callbackFlow {
        val listener = usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error)).isSuccess
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val users = snapshot.documents.mapNotNull {
                    it.toObject(FirebaseUser::class.java)
                }
                trySend(Result.success(users)).isSuccess
            } else {
                trySend(Result.success(emptyList())).isSuccess
            }
        }

        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)
}
