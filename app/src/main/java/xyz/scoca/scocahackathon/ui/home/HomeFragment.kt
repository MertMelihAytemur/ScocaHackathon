package xyz.scoca.scocahackathon.ui.home

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.scoca.scocahackathon.MainActivity
import xyz.scoca.scocahackathon.R
import xyz.scoca.scocahackathon.databinding.FragmentHomeBinding
import xyz.scoca.scocahackathon.model.PlaceData
import xyz.scoca.scocahackathon.model.bus.nearbybus.NearbyBus
import xyz.scoca.scocahackathon.model.hospital.nearbyhostpital.NearbyHospital
import xyz.scoca.scocahackathon.model.mall.nearbymall.NearbyMall
import xyz.scoca.scocahackathon.model.park.nearbyparks.NearbyPark
import xyz.scoca.scocahackathon.network.nearby.IGoogleApiService
import xyz.scoca.scocahackathon.ui.home.adapter.PlaceDataAdapter
import xyz.scoca.scocahackathon.util.common.Common
import xyz.scoca.scocahackathon.util.extension.OnItemClickListener
import xyz.scoca.scocahackathon.util.extension.snack
import xyz.scoca.scocahackathon.viewmodel.LikedPlaceViewModel
import java.io.IOException
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: LikedPlaceViewModel
    private lateinit var mMap: GoogleMap

    //Place List
    private lateinit var placeList: ArrayList<PlaceData>

    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    private lateinit var mLastLocation: Location
    private var mMarker: Marker? = null

    //Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    //Nearby place api service
    private lateinit var googleApiService: IGoogleApiService

    //Search Destination
    private lateinit var addressList: List<Address>

    //Navigation Drawer
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var headerView: View

    companion object {
        private const val LOCATION_PERMISSION_CODE: Int = 1000
        private const val REQUEST_CHECK_SETTING: Int = 0x1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(LikedPlaceViewModel::class.java)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fMapLocation) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        placeList = ArrayList()
        googleApiService = Common.googleApiService
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNavigationDrawer()
        initListeners()

        //Request runtime permission
        requestRuntimePermission()
        searchDestination()
        binding.ivRefresh.setOnClickListener {
            refresh()
        }
    }

    private fun refresh() {
        mMap.clear()
        requestRuntimePermission()
    }

    private fun initListeners() {
        binding.chipParks.setOnClickListener {
            placeList.clear()
            getParkData()
        }

        binding.chipBus.setOnClickListener {
            placeList.clear()
            getBusData()
        }

        binding.chipHospital.setOnClickListener {
            placeList.clear()
            getHospitalData()
        }

        binding.chipMall.setOnClickListener {
            placeList.clear()
            getMallData()
        }
    }

    private fun setNavigationDrawer() {
        binding.navView.inflateMenu(R.menu.nav_menu)
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        headerView = navView.getHeaderView(0)

        toggle = ActionBarDrawerToggle(
            activity as MainActivity,
            drawerLayout,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_sync -> refresh()
                R.id.nav_history -> findNavController().navigate(R.id.action_homeFragment_to_likedPlaceFragment)
                R.id.nav_feedback -> findNavController().navigate(R.id.action_homeFragment_to_feedBackFragment)
                R.id.nav_about_us -> openScocaWebsite()
            }
            true
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ), LOCATION_PERMISSION_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ), LOCATION_PERMISSION_CODE
                )
                return false
            }
        }
        return true
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest.create()

        locationRequest = LocationRequest()
        locationRequest.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000  //set the interval in which you want to get locations.
        locationRequest.fastestInterval =
            60000  // if a location is available sooner you can get it (i.e. another app is using the location services).
        locationRequest.smallestDisplacement = 10f

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        builder.setAlwaysShow(true)

        val task = LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(builder.build())

        task.addOnCompleteListener {
            try {
                val response = task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        val resolvableApiException = e as ResolvableApiException
                        try {
                            resolvableApiException.startResolutionForResult(
                                requireActivity(),
                                REQUEST_CHECK_SETTING
                            )
                        } catch (e: IntentSender.SendIntentException) {

                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                mLastLocation = p0.locations[p0.locations.size - 1] //get last location
                if (mMarker != null) {
                    mMarker!!.remove()
                }
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Your location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder))
                mMarker = mMap.addMarker(markerOptions)

                //Move camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                findAllPlaces()
                getBusData()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                        if (checkLocationPermission()) {
                            buildLocationRequest()
                            buildLocationCallBack()

                            fusedLocationProviderClient =
                                LocationServices.getFusedLocationProviderClient(activity as MainActivity)
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper()!!
                            )
                            mMap.isMyLocationEnabled = true
                        }
                } else
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //init Google play services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
            }
        } else
            mMap.isBuildingsEnabled = true
        //Enable zoom control
        mMap.uiSettings.isZoomControlsEnabled = true

    }

    private fun requestRuntimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                buildLocationRequest()
                buildLocationCallBack()

                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(activity as MainActivity)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()!!
                )
            }
        } else {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity as MainActivity)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()!!
            )
        }
    }

    private fun findNearbyPark(park: String) {
        //Build url request on location
        val url = getPlaceUrl(latitude, longitude, park)
        googleApiService.getNearbyPark(url)
            .enqueue(object : Callback<NearbyPark> {
                override fun onResponse(call: Call<NearbyPark>, response: Response<NearbyPark>) {
                    //val currentPlace = response.body()!!
                    if (response.isSuccessful) {
                        for (i in 0 until (response.body()?.results!!.size)) {
                            val markerOptions = MarkerOptions()
                            val googlePlace = response.body()!!.results!![i]
                            val lat = googlePlace.geometry?.location?.lat
                            val lng = googlePlace.geometry?.location?.lng

                            val placeName = googlePlace.name
                            val latLng = LatLng(lat!!, lng!!)

                            markerOptions.position(latLng)
                            markerOptions.title(placeName)

                            if (park == "parking") {
                                markerOptions.icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.map_park)
                                )
                            }
                            mMap.addMarker(markerOptions)
                            //Move camera
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        latitude,
                                        longitude
                                    ), 15f
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<NearbyPark>, t: Throwable) {
                    snack(requireView(), t.message.toString())
                }
            })
    }

    private fun findNearbyHospital(hospital: String) {
        //Build url request on location
        val url = getPlaceUrl(latitude, longitude, hospital)
        googleApiService.getNearbyHospital(url)
            .enqueue(object : Callback<NearbyHospital> {
                override fun onResponse(
                    call: Call<NearbyHospital>,
                    response: Response<NearbyHospital>
                ) {
                    //val currentPlace = response.body()!!
                    if (response.isSuccessful) {
                        for (i in 0 until (response.body()?.results!!.size)) {
                            val markerOptions = MarkerOptions()
                            val googlePlace = response.body()!!.results!![i]
                            val lat = googlePlace.geometry?.location?.lat
                            val lng = googlePlace.geometry?.location?.lng

                            val placeName = googlePlace.name
                            val latLng = LatLng(lat!!, lng!!)

                            markerOptions.position(latLng)
                            markerOptions.title(placeName)

                            if (hospital == "hospital") {
                                markerOptions.icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.map_hospital)
                                )
                            }
                            mMap.addMarker(markerOptions)
                            //Move camera
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        latitude,
                                        longitude
                                    ), 15f
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<NearbyHospital>, t: Throwable) {
                    snack(requireView(), t.message.toString())
                }
            })
    }

    private fun findNearbyMall(mall: String) {
        val url = getPlaceUrl(latitude, longitude, mall)
        googleApiService.getNearbyMall(url)
            .enqueue(object : Callback<NearbyMall> {
                override fun onResponse(call: Call<NearbyMall>, response: Response<NearbyMall>) {
                    //val currentPlace = response.body()!!
                    if (response.isSuccessful) {
                        for (i in 0 until (response.body()?.results!!.size)) {
                            val markerOptions = MarkerOptions()
                            val googlePlace = response.body()!!.results!![i]
                            val lat = googlePlace.geometry?.location?.lat
                            val lng = googlePlace.geometry?.location?.lng

                            val placeName = googlePlace.name
                            val latLng = LatLng(lat!!, lng!!)

                            markerOptions.position(latLng)
                            markerOptions.title(placeName)

                            if (mall == "shopping_mall") {
                                markerOptions.icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.map_shopping)
                                )
                            }
                            mMap.addMarker(markerOptions)
                            //Move camera
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        latitude,
                                        longitude
                                    ), 15f
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<NearbyMall>, t: Throwable) {
                    snack(requireView(), t.message.toString())
                }

            })
    }

    private fun findNearbyBus(bus: String) {
        val url = getPlaceUrl(latitude, longitude, bus)
        googleApiService.getNearbyBus(url)
            .enqueue(object : Callback<NearbyBus> {
                override fun onResponse(call: Call<NearbyBus>, response: Response<NearbyBus>) {
                    //val currentPlace = response.body()!!
                    if (response.isSuccessful) {
                        for (i in 0 until (response.body()?.results!!.size)) {
                            val markerOptions = MarkerOptions()
                            val googlePlace = response.body()!!.results!![i]
                            val lat = googlePlace.geometry?.location?.lat
                            val lng = googlePlace.geometry?.location?.lng

                            val placeName = googlePlace.name
                            val latLng = LatLng(lat!!, lng!!)

                            markerOptions.position(latLng)
                            markerOptions.title(placeName)

                            if (bus == "bus_station") {
                                markerOptions.icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.map_bus)
                                )
                            }
                            mMap.addMarker(markerOptions)
                            //Move camera
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        latitude,
                                        longitude
                                    ), 15f
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<NearbyBus>, t: Throwable) {
                    snack(requireView(), t.message.toString())
                }
            })
    }

    private fun getPlaceUrl(latitude: Double, longitude: Double, location: String): String {
        val googlePlaceUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googlePlaceUrl.append("?location=${latitude}%2C${longitude}")
        googlePlaceUrl.append("&radius=4000") // 4 km
        googlePlaceUrl.append("&type=${location}")
        googlePlaceUrl.append("&key=AIzaSyCrJER5ymCvt6Lm6hWeZc8IL3cEQmR5eHQ")

        Log.d("URL_DEBUG", googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }

    private fun searchDestination() {
        binding.svDestination.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val location = binding.svDestination.query.toString()
                val geocoder = Geocoder(activity as MainActivity)

                try {
                    mMap.clear()
                    addressList = geocoder.getFromLocationName(location, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val address: Address = addressList[0]
                latitude = address.latitude
                longitude = address.longitude

                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title(location)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder))

                mMap.addMarker(markerOptions)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                findAllPlaces()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
    }

    private fun setRecyclerViewAdapter(placeList: ArrayList<PlaceData>) {
        val mLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPlaceData.layoutManager = mLayoutManager
        binding.rvPlaceData.adapter =
            PlaceDataAdapter(requireContext(), placeList, object : OnItemClickListener {
                override fun onClick(position: Int) {
                    val placeAverageDensity = placeList[position].place_avarage_density
                    val placeCurrentDensity = placeList[position].place_avarage_density
                    val placeName = placeList[position].place_name
                    val placePoint = placeList[position].place_point
                    val placeTotal = placeList[position].place_total
                    val dateAndTime = getDateAndTime()

                    val placeData = PlaceData(
                        0,
                        placeAverageDensity,
                        placeCurrentDensity,
                        placeName,
                        placePoint,
                        placeTotal,
                        dateAndTime
                    )
                    savePlace(placeData)
                }
            })
    }

    private fun savePlace(placeData: PlaceData) {
        placeData.let {
            viewModel.addPlace(it)
            snack(requireView(), " ${placeData.place_name} Saved")
        }
    }

    private fun findAllPlaces() {
        findNearbyBus("bus_station")
        findNearbyHospital("hospital")
        findNearbyMall("shopping_mall")
        findNearbyPark("parking")
    }

    private fun getBusData() {
        binding.progressBar.visibility = View.VISIBLE
        val databaseReference = FirebaseDatabase.getInstance().getReference("bus")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                placeList = ArrayList()
                for (postSnapshot: DataSnapshot in snapshot.children) {
                    val placeData: PlaceData? = postSnapshot.getValue(PlaceData::class.java)
                    if (placeData != null) {
                        placeList.add(placeData)
                    }
                    setRecyclerViewAdapter(placeList)
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                snack(requireView(), error.message)
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun getParkData() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("park")
        binding.progressBar.visibility = View.VISIBLE
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                placeList = ArrayList()
                for (postSnapshot: DataSnapshot in snapshot.children) {
                    val placeData: PlaceData? = postSnapshot.getValue(PlaceData::class.java)
                    if (placeData != null) {
                        placeList.add(placeData)
                    }
                    setRecyclerViewAdapter(placeList)
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                snack(requireView(), error.message)
            }
        })
    }

    private fun getMallData() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("shopping-mall")
        binding.progressBar.visibility = View.VISIBLE
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                placeList = ArrayList()
                for (postSnapshot: DataSnapshot in snapshot.children) {
                    val placeData: PlaceData? = postSnapshot.getValue(PlaceData::class.java)
                    if (placeData != null) {
                        placeList.add(placeData)
                    }
                    setRecyclerViewAdapter(placeList)
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                snack(requireView(), error.message)
            }
        })
    }

    private fun getHospitalData() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("hospital")
        binding.progressBar.visibility = View.VISIBLE
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                placeList = ArrayList()
                for (postSnapshot: DataSnapshot in snapshot.children) {
                    val placeData: PlaceData? = postSnapshot.getValue(PlaceData::class.java)
                    if (placeData != null) {
                        placeList.add(placeData)
                    }
                    setRecyclerViewAdapter(placeList)
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                snack(requireView(), error.message)
            }
        })
    }

    private fun getDateAndTime(): String {
        val calendar = Calendar.getInstance()

        val minute = calendar.get(Calendar.MINUTE)
        val hour = calendar.get(Calendar.HOUR)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)


        calendar.set(year, month, day, hour, minute)
        return setDateAndTime(calendar.timeInMillis)
    }

    private fun setDateAndTime(time: Long): String {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireContext())
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext())

        return dateFormat.format(date) + " " + timeFormat.format(date)
    }

    private fun openScocaWebsite(){
        val openUrl = Intent(Intent.ACTION_VIEW)
        openUrl.data = Uri.parse("https://megaverse.software/mega.php/")
        startActivity(openUrl)
    }
}