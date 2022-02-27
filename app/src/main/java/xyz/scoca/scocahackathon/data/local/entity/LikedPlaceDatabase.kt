package xyz.scoca.scocahackathon.data.local.entity

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import xyz.scoca.scocahackathon.model.PlaceData

@Database(entities = [PlaceData::class], version = 1, exportSchema = false)
abstract class LikedPlaceDatabase : RoomDatabase(){
    abstract fun likedPlaceDao() : LikedPlaceDao

    companion object{
        @Volatile // rights to this field are immediately made visible to other threads
        private var INSTANCE : LikedPlaceDatabase? = null

        fun getDatabase(context : Context) : LikedPlaceDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LikedPlaceDatabase::class.java,
                    "likedPlaceDatabase"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }


}