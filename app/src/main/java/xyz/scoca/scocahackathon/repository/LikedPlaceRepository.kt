package xyz.scoca.scocahackathon.repository

import androidx.lifecycle.LiveData
import xyz.scoca.scocahackathon.data.local.entity.LikedPlaceDao
import xyz.scoca.scocahackathon.model.PlaceData

class LikedPlaceRepository(
    private val likedPlaceDao: LikedPlaceDao
) {
    val readAllPlaceData: LiveData<List<PlaceData>> = likedPlaceDao.readAllPlaceData()

    suspend fun addPlace(placeData: PlaceData) {
        likedPlaceDao.addPlace(placeData)
    }

    suspend fun deletePlace(placeData: PlaceData) {
        likedPlaceDao.deletePlace(placeData)
    }

}