package xyz.scoca.scocahackathon.network.nearby

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url
import xyz.scoca.scocahackathon.model.bus.nearbybus.NearbyBus
import xyz.scoca.scocahackathon.model.hospital.nearbyhostpital.NearbyHospital
import xyz.scoca.scocahackathon.model.mall.nearbymall.NearbyMall
import xyz.scoca.scocahackathon.model.park.nearbyparks.NearbyPark

interface IGoogleApiService {
    @GET
    fun getNearbyPark(@Url url : String) : Call<NearbyPark>

    @GET
    fun getNearbyHospital(@Url url : String) : Call<NearbyHospital>

    @GET
    fun getNearbyMall(@Url url : String) : Call<NearbyMall>

    @GET
    fun getNearbyBus(@Url url : String) : Call<NearbyBus>
}