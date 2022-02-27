package xyz.scoca.scocahackathon.data.local

import android.content.Context

class ClientPreferences(context : Context) {
    companion object{
        private const val PREFERENCES_NAME = "ScocaPreferences"
        private const val ON_BOARDING_DONE = "OnBoardingDone"
    }
    private val sharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun setOnBoardingState(state : Boolean){
        with(sharedPreferences.edit()){
            putBoolean(ON_BOARDING_DONE,state)
            apply()
        }
    }

    fun isOnBoardingDone() : Boolean{
        return sharedPreferences.getBoolean(ON_BOARDING_DONE,false)
    }

}