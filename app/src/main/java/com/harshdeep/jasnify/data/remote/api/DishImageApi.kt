package com.harshdeep.jasnify.data.remote.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

@Keep
data class WikiThumbnailSource(
    @SerializedName("source") val source: String?,
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?
)

@Keep
data class WikiPageItem(
    @SerializedName("pageid") val pageid: Long?,
    @SerializedName("title") val title: String?,
    @SerializedName("thumbnail") val thumbnail: WikiThumbnailSource?,
    @SerializedName("original") val original: WikiThumbnailSource?
)

@Keep
data class WikiQuery(
    @SerializedName("pages") val pages: Map<String, WikiPageItem>?
)

@Keep
data class WikiSearchResponse(
    @SerializedName("query") val query: WikiQuery?
)

@Keep
interface DishImageApi {
    @Headers("User-Agent: JasnifyEventApp/1.0 (contact@jasnify.app)")
    @GET("w/api.php?action=query&format=json&generator=search&gsrlimit=5&prop=pageimages&piprop=thumbnail|original&pithumbsize=600")
    suspend fun searchDishImages(
        @Query("gsrsearch") dishQuery: String
    ): WikiSearchResponse
}
