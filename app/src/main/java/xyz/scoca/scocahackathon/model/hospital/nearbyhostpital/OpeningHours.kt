package xyz.scoca.scocahackathon.model.hospital.nearbyhostpital


import com.google.gson.annotations.SerializedName

data class OpeningHours(
    @SerializedName("open_now")
    val openNow: Boolean? = null
)