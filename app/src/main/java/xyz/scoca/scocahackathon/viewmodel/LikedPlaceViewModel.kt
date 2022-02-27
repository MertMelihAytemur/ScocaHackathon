package xyz.scoca.scocahackathon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.scoca.scocahackathon.data.local.entity.LikedPlaceDatabase
import xyz.scoca.scocahackathon.model.PlaceData
import xyz.scoca.scocahackathon.repository.LikedPlaceRepository

class LikedPlaceViewModel(
    application: Application
) : AndroidViewModel(application) { //it provides relation between Repository and UI

    val readAllPlaceData: LiveData<List<PlaceData>>
    private val repository : LikedPlaceRepository

    init {
        val likedPlaceDao = LikedPlaceDatabase.getDatabase(application).likedPlaceDao()
        repository = LikedPlaceRepository(likedPlaceDao)
        readAllPlaceData = repository.readAllPlaceData
    }

    fun addPlace(placeData: PlaceData){
        viewModelScope.launch(Dispatchers.IO){
            repository.addPlace(placeData)
        }
    }

    fun deletePlace(placeData: PlaceData){
        viewModelScope.launch(Dispatchers.IO){
            repository.deletePlace(placeData)
        }
    }
}