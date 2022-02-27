package xyz.scoca.scocahackathon.model.bus.nearbybus


import com.google.gson.annotations.SerializedName

data class NearbyBus(
    @SerializedName("html_attributions")
    val htmlAttributions: List<Any>? = null,
    @SerializedName("next_page_token")
    val nextPageToken: String? = null,
    @SerializedName("results")
    val results: List<Result>? = null,
    @SerializedName("status")
    val status: String? = null
)