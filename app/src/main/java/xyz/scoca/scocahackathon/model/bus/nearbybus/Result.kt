package xyz.scoca.scocahackathon.model.bus.nearbybus


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("business_status")
    val businessStatus: String? = null,
    @SerializedName("geometry")
    val geometry: Geometry? = null,
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("icon_background_color")
    val iconBackgroundColor: String? = null,
    @SerializedName("icon_mask_base_uri")
    val iconMaskBaseUri: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("photos")
    val photos: List<Photo>? = null,
    @SerializedName("place_id")
    val placeİd: String? = null,
    @SerializedName("plus_code")
    val plusCode: PlusCode? = null,
    @SerializedName("rating")
    val rating: Double? = null,
    @SerializedName("reference")
    val reference: String? = null,
    @SerializedName("scope")
    val scope: String? = null,
    @SerializedName("types")
    val types: List<String>? = null,
    @SerializedName("user_ratings_total")
    val userRatingsTotal: Int? = null,
    @SerializedName("vicinity")
    val vicinity: String? = null
)