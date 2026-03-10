package com.abdullahnadeem.minecraftandroid.data.mapper

import android.util.Log
import com.abdullahnadeem.minecraftandroid.data.model.CatalogDto
import com.abdullahnadeem.minecraftandroid.data.model.CategoryDto
import com.abdullahnadeem.minecraftandroid.data.model.VideoDto
import com.abdullahnadeem.minecraftandroid.domain.model.Category
import com.abdullahnadeem.minecraftandroid.domain.model.Video
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull

private const val TAG = "CatalogMapper"

private val catalogJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    isLenient = true
}

fun parseCatalogPayload(payload: String): CatalogDto {
    val root = catalogJson.parseToJsonElement(payload)
    Log.d(TAG, "Root JSON type: ${root::class.simpleName}")
    if (root is JsonObject) {
        Log.d(TAG, "Root keys: ${root.keys.take(20)}")
    }
    if (root is JsonArray) {
        Log.d(TAG, "Root array size: ${root.size}")
    }
    val categories = when (root) {
        is JsonArray -> parseCategoriesArray(root)
        is JsonObject -> parseCatalogObject(root)
        else -> emptyList()
    }
    Log.d(TAG, "parseCatalogPayload result: ${categories.size} categories")
    return CatalogDto(categories = categories)
}

fun CatalogDto.toDomainCategories(): List<Category> = categories.map { category ->
    Category(
        id = category.id,
        title = category.title,
        thumbnailUrl = category.thumbnailUrl,
        videos = category.videos.map { video ->
            Video(
                id = video.id,
                title = video.title,
                description = video.description,
                thumbnailUrl = video.thumbnailUrl,
                youtubeUrl = video.youtubeUrl,
                categoryId = video.categoryId,
                categoryTitle = video.categoryTitle,
                durationText = video.durationText
            )
        }
    )
}

private fun parseCatalogObject(root: JsonObject): List<CategoryDto> {
    Log.d(TAG, "parseCatalogObject: root keys = ${root.keys.toList()}")
    val categoryArrays = listOf("channels", "categories", "data", "items", "results")
        .mapNotNull(root::get)
        .mapNotNull { it as? JsonArray }

    val explicitCategories = categoryArrays
        .firstOrNull { array -> array.any { it is JsonObject && looksLikeCategory(it) } }
        ?.let(::parseCategoriesArray)
        .orEmpty()
    Log.d(TAG, "explicitCategories from keys ${listOf("categories","data","items","results")}: ${explicitCategories.size}")

    if (explicitCategories.isNotEmpty()) {
        return explicitCategories
    }

    val directVideos = listOf("videos", "entries", "clips", "items", "results")
        .mapNotNull(root::get)
        .mapNotNull { it as? JsonArray }
        .firstOrNull { array -> array.any { it is JsonObject && looksLikeVideo(it) } }
        ?.mapIndexedNotNull { index, element ->
            val videoObject = element as? JsonObject ?: return@mapIndexedNotNull null
            parseVideo(videoObject, index, null, null)
        }
        .orEmpty()
    Log.d(TAG, "directVideos found: ${directVideos.size}")

    if (directVideos.isNotEmpty()) {
        return groupVideosIntoCategories(directVideos)
    }

    return if (looksLikeCategory(root)) {
        listOf(parseCategory(root, 0))
    } else {
        emptyList()
    }
}

private fun parseCategoriesArray(array: JsonArray): List<CategoryDto> {
    Log.d(TAG, "parseCategoriesArray: ${array.size} elements")
    array.take(2).forEachIndexed { i, el ->
        if (el is JsonObject) Log.d(TAG, "  element[$i] keys: ${el.keys.take(15)}")
    }
    return array.mapIndexedNotNull { index, element ->
        val obj = element as? JsonObject ?: return@mapIndexedNotNull null
        when {
            looksLikeCategory(obj) -> parseCategory(obj, index)
            looksLikeVideo(obj) -> null
            else -> null
        }
    }.ifEmpty {
        val videos = array.mapIndexedNotNull { index, element ->
            val obj = element as? JsonObject ?: return@mapIndexedNotNull null
            if (looksLikeVideo(obj)) parseVideo(obj, index, null, null) else null
        }
        groupVideosIntoCategories(videos)
    }
}

private fun parseCategory(obj: JsonObject, index: Int): CategoryDto {
    val title = obj.pickString("channelTitle", "title", "name", "label", "category") ?: "Category ${index + 1}"
    val id = obj.pickString("id", "slug", "key") ?: slugify(title, "category-$index")
    val videosArray = obj.pickArray("videos", "items", "entries", "children")
    val videos = videosArray?.mapIndexedNotNull { videoIndex, element ->
        val videoObject = element as? JsonObject ?: return@mapIndexedNotNull null
        parseVideo(videoObject, videoIndex, id, title)
    }.orEmpty()

    return CategoryDto(
        id = id,
        title = title,
        thumbnailUrl = obj.pickString(
            "channelThumbnail",
            "thumbnail",
            "thumbnailUrl",
            "thumbnail_url",
            "image",
            "imageUrl",
            "image_url",
            "cover",
            "coverUrl"
        ) ?: videos.firstNotNullOfOrNull { it.thumbnailUrl },
        videos = videos
    )
}

private fun parseVideo(
    obj: JsonObject,
    index: Int,
    fallbackCategoryId: String?,
    fallbackCategoryTitle: String?
): VideoDto {
    val title = obj.pickString("title", "name", "videoTitle") ?: "Video ${index + 1}"
    val categoryTitle = obj.pickString("category", "categoryTitle", "section")
        ?: fallbackCategoryTitle
        ?: "Uncategorized"
    val categoryId = obj.pickString("categoryId", "category_id")
        ?: fallbackCategoryId
        ?: slugify(categoryTitle, "uncategorized")
    val youtubeUrl = obj.pickString("youtubeUrl", "youtube_url", "url", "link", "watchUrl", "videoUrl")
    val id = obj.pickString("id", "videoId", "video_id", "slug")
        ?: youtubeUrl?.let { slugify(it, "video-$index") }
        ?: slugify(title, "video-$index")

    return VideoDto(
        id = id,
        title = title,
        description = obj.pickString("description", "summary", "details"),
        thumbnailUrl = obj.pickString(
            "videoThumbnail",
            "thumbnail",
            "thumbnailUrl",
            "thumbnail_url",
            "image",
            "imageUrl",
            "image_url",
            "poster",
            "posterUrl"
        ),
        youtubeUrl = youtubeUrl,
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        durationText = obj.pickString("duration", "durationText", "length")
    )
}

private fun groupVideosIntoCategories(videos: List<VideoDto>): List<CategoryDto> = videos
    .groupBy { it.categoryId to it.categoryTitle }
    .map { (categoryKey, items) ->
        val thumbnail = items.firstNotNullOfOrNull { it.thumbnailUrl }
        CategoryDto(
            id = categoryKey.first,
            title = categoryKey.second,
            thumbnailUrl = thumbnail,
            videos = items
        )
    }
    .sortedBy { it.title.lowercase() }

private fun looksLikeCategory(obj: JsonObject): Boolean {
    val hasCategoryTitle = obj.pickString("channelTitle", "title", "name", "label", "category") != null
    val hasNestedVideos = obj.pickArray("videos", "items", "entries", "children") != null
    return hasCategoryTitle && hasNestedVideos
}

private fun looksLikeVideo(obj: JsonObject): Boolean {
    val hasTitle = obj.pickString("title", "name", "videoTitle") != null
    val hasUrl = obj.pickString("youtubeUrl", "youtube_url", "url", "link", "watchUrl", "videoUrl") != null
    return hasTitle || hasUrl
}

private fun JsonObject.pickString(vararg keys: String): String? = keys.firstNotNullOfOrNull { key ->
    this[key]?.primitiveContentOrNull()
}

private fun JsonObject.pickArray(vararg keys: String): JsonArray? = keys.firstNotNullOfOrNull { key ->
    this[key] as? JsonArray
}

private fun JsonElement.primitiveContentOrNull(): String? = when (this) {
    is JsonPrimitive -> {
        contentOrNull
            ?: intOrNull?.toString()
            ?: doubleOrNull?.toString()
            ?: booleanOrNull?.toString()
    }
    JsonNull -> null
    else -> null
}

private fun slugify(input: String, fallback: String): String {
    val slug = input
        .trim()
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
    return slug.ifBlank { fallback }
}
