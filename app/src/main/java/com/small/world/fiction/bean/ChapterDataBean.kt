package com.small.world.fiction.bean

data class Chapter(
    var chapter_id: String? = "",
    var novel_id: String? = "",
    var prev_chapter_id: String? = "",
    var chapter_num: Int? = 0,
    var requested_by: String? = "",
    var requested_at: String? = "",
    var request_details: ChapterRequestDetails? = ChapterRequestDetails(),
    var generation_details: ChapterGenerationDetails? = ChapterGenerationDetails(),
    var title: ContentStatus? = ContentStatus(),
    var content: ContentStatus? = ContentStatus(),
    var cover: UrlStatus? = UrlStatus(),
    var audiobook: UrlStatus? = UrlStatus(),
    var videobook: UrlStatus? = UrlStatus(),
    var trailer: UrlStatus? = UrlStatus(),
    var next_chapter_choices: ChoicesStatus? = ChoicesStatus(),
    var review: Review? = Review()
)

data class ChapterRequestDetails(
    var user_comment: String? = "",
    var generated_comment: String? = ""
)

data class ChapterGenerationDetails(
    var chapter_type: String? = "innovative",//默认原创
    var content_gen_prompt: String? = ""
)

data class ContentStatus(
    var status: String? = "",
    var content: String? = ""
)

data class UrlStatus(
    var status: String? = "",
    var url: String? = ""
)

data class ChoicesStatus(
    var status: String? = "",
    var choices: List<String>? = emptyList()
)

data class Review(
    var like_count: Int? = 0,
    var neutral_count: Int? = 0,
    var dislike_count: Int? = 0
)

