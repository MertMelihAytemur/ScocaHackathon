package xyz.scoca.scocahackathon.model.hospital.nearbyhostpital


import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lng")
    val lng: Double? = null
)