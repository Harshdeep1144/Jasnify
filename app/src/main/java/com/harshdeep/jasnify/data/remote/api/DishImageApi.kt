package com.harshdeep.jasnify.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

data class WikiThumbnailSource(
    val source: String?,
    val width: Int?,
    val height: Int?
)

data class WikiPageItem(
    val pageid: Long?,
    val title: String?,
    val thumbnail: WikiThumbnailSource?,
    val original: WikiThumbnailSource?
)

data class WikiQuery(
    val pages: Map<String, WikiPageItem>?
)

data class WikiSearchResponse(
    val query: WikiQuery?
)

interface DishImageApi {
    @Headers("User-Agent: JasnifyEventApp/1.0 (contact@jasnify.app)")
    @GET("w/api.php?action=query&format=json&generator=search&gsrlimit=5&prop=pageimages&piprop=thumbnail|original&pithumbsize=600")
    suspend fun searchDishImages(
        @Query("gsrsearch") dishQuery: String
    ): WikiSearchResponse
}
