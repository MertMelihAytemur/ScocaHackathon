package xyz.scoca.scocahackathon.ui.home.adapter

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import xyz.scoca.scocahackathon.R
import xyz.scoca.scocahackathon.databinding.ItemLikedPlaceBinding
import xyz.scoca.scocahackathon.model.PlaceData
import xyz.scoca.scocahackathon.util.extension.OnItemClickListener

class LikedPlaceAdapter(
    private val context: Context,
    private val placeList : List<PlaceData>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<LikedPlaceAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding : ItemLikedPlaceBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(){
            val itemPosition = placeList[adapterPosition]
            itemBinding.tvPlaceName.text = itemPosition.place_name
            itemBinding.tvPlaceTotal.text = itemPosition.place_total.toString()
            itemBinding.tvPlaceDensity.text = itemPosition.place_current_density.toString()
            itemBinding.tvPlacePoint.text = itemPosition.place_point.toString()
            itemBinding.tvDateAndTime.text = itemPosition.dateAndTime

            //Expand CardView
            itemBinding.cardView.setOnClickListener {
                if (itemBinding.hiddenLayout.visibility == View.GONE) {
                    TransitionManager.beginDelayedTransition(
                        itemBinding.hiddenLayout,
                        AutoTransition()
                    )
                    itemBinding.hiddenLayout.visibility = View.VISIBLE
                    itemBinding.ivAngelUp.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    TransitionManager.beginDelayedTransition(
                        itemBinding.hiddenLayout,
                        AutoTransition()
                    )
                    itemBinding.hiddenLayout.visibility = View.GONE
                    itemBinding.ivAngelUp.setImageResource(R.drawable.ic_arrow_up)
                }
            }
            itemBinding.btnDelete.setOnClickListener {
                onItemClickListener.onClick(adapterPosition)
            }

            val placeCurrentDensity = itemPosition.place_current_density
            val placeAverageDensity = itemPosition.place_avarage_density
            val placeTotalCapacity = itemPosition.place_total

            when(calculateDensity(placeCurrentDensity,placeTotalCapacity)){
                in 0.0..33.3 -> {
                    itemBinding.ivPlaceDensity.setImageResource(R.drawable.car_density_green)
                    itemBinding.tvPlaceDensity.setTextColor(ContextCompat.getColor(context,R.color.spotify_green))
                }
                in 33.3..66.6 -> {
                    itemBinding.ivPlaceDensity.setImageResource(R.drawable.car_density_orange)
                    itemBinding.tvPlaceDensity.setTextColor(ContextCompat.getColor(context,R.color.carrot_orange))
                }
                in 66.6..100.0 -> {
                    itemBinding.ivPlaceDensity.setImageResource(R.drawable.car_density_red)
                    itemBinding.tvPlaceDensity.setTextColor(ContextCompat.getColor(context,R.color.red))
                }
                else -> {
                    itemBinding.ivPlaceDensity.setImageResource(R.drawable.car_density_red)
                    itemBinding.tvPlaceDensity.setTextColor(ContextCompat.getColor(context,R.color.red))
                }
            }

            when(calculateDensity(placeAverageDensity,placeTotalCapacity)){
                in 0.0..33.3 -> {
                    itemBinding.ivAverageDensity.setImageResource(R.drawable.car_density_green)
                }
                in 33.3..66.6 -> {
                    itemBinding.ivAverageDensity.setImageResource(R.drawable.car_density_orange)
                }
                in 66.6..100.0 -> {
                    itemBinding.ivAverageDensity.setImageResource(R.drawable.car_density_red)
                }
                else -> {
                    itemBinding.ivAverageDensity.setImageResource(R.drawable.car_density_red)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemLikedPlaceBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = placeList.size

    private fun calculateDensity(currentDensity : Double , total : Double) : Double{
        return (currentDensity / total) * 100
    }

}