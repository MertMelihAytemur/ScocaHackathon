package xyz.scoca.scocahackathon.model.park.nearbyparks


import com.google.gson.annotations.SerializedName

data class Viewport(
    @SerializedName("northeast")
    val northeast: Northeast? = null,
    @SerializedName("southwest")
    val southwest: Southwest? = null
)