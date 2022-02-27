package xyz.scoca.scocahackathon.model.mall.nearbymall


import com.google.gson.annotations.SerializedName

data class NearbyMall(
    @SerializedName("html_attributions")
    val htmlAttributions: List<Any>? = null,
    @SerializedName("next_page_token")
    val nextPageToken: String? = null,
    @SerializedName("results")
    val results: List<Result>? = null,
    @SerializedName("status")
    val status: String? = null
)