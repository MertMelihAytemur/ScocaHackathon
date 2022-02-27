package xyz.scoca.scocahackathon.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "place_table")
data class PlaceData(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val place_avarage_density : Double = 0.0,
    val place_current_density : Double = 0.0,
    val place_name : String = "",
    val place_point : Double = 0.0,
    val place_total : Double  = 0.0,
    val dateAndTime : String = ""
) :Parcelable
