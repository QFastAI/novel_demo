package com.small.world.fiction.bean

import java.io.Serializable

data class CreateFictionBean(
    var itemType : Int = 1,
    var novel_id: String? = "",
    var tags: List<String>? = emptyList(),
    var requested_by: String? = "",
    var requested_at: String? = "",
    var request_details: RequestDetails? = RequestDetails(),
    var generation_details: GenerationDetails? = GenerationDetails(),
    var title: Title? = Title(),
    var outline: OutlineBean? = OutlineBean(),
    var cover: Cover? = Cover(),
    var trailer: Trailer? = Trailer()
)

data class RequestDetails(
    var targetAudience: String? = "",
    var plots: List<String>? = emptyList(),
    var roles: List<String>? = emptyList(),
    var themes: List<String>? = emptyList()
)

data class GenerationDetails(
    var outline_gen_prompt: String? = ""
): Serializable
//用户可能写，也可能不写
//写了按用户写的
//没写提示用户要不要自动创建
//如果自动创建，    * 会临时胡一个进去
//        * 具体格式：
//            * [标题生成中]可盐可甜、大男主、仙侠……
//            * 控制tag大概20个字符，后面省略
data class Title(
    var status: String? = "",
    var content: String? = ""
): Serializable

data class OutlineBean(
    var status: String? = "",
    var content: String? = ""
): Serializable

data class Cover(
    var status: String? = "",
    var url: String? = ""
): Serializable

data class Trailer(
    var status: String? = "",
    var url: String? = ""
): Serializable
