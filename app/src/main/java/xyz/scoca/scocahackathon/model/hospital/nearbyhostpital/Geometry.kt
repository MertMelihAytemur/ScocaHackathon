package xyz.scoca.scocahackathon.model.hospital.nearbyhostpital


import com.google.gson.annotations.SerializedName

data class Geometry(
    @SerializedName("location")
    val location: Location? = null,
    @SerializedName("viewport")
    val viewport: Viewport? = null
)