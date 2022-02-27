package xyz.scoca.scocahackathon.data.local.entity

import androidx.lifecycle.LiveData
import androidx.room.*
import xyz.scoca.scocahackathon.model.PlaceData

@Dao
interface LikedPlaceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) //If there is a new same data , ignore that.
    suspend fun addPlace(placeData: PlaceData)

    @Query("SELECT *FROM place_table ORDER BY place_name ASC")
    fun readAllPlaceData() : LiveData<List<PlaceData>>

    @Delete
    suspend fun deletePlace(placeData: PlaceData)


}