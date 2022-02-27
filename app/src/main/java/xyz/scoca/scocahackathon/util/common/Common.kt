package xyz.scoca.scocahackathon.util.common

import xyz.scoca.scocahackathon.network.nearby.IGoogleApiService
import xyz.scoca.scocahackathon.network.nearby.RetrofitClient

object Common {
    private const val GOOGLE_API_URL = "https://maps.googleapis.com/"

    val googleApiService : IGoogleApiService
        get() = RetrofitClient.getClient(GOOGLE_API_URL)
            .create(IGoogleApiService::class.java)
}