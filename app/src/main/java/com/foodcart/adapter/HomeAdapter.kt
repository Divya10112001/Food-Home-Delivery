package com.foodcart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodcart.R
import com.foodcart.activity.MenuActivity
import com.foodcart.food
import com.squareup.picasso.Picasso

class HomeAdapter(val context: Context, val fooditemList: ArrayList<food>) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerhome, parent, false)
        return HomeViewHolder(view)
    }
    override fun getItemCount(): Int {
        return fooditemList.size
    }
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val food = fooditemList[position]
        holder.txtfoodname.text = food.foodname
        holder.txtfoodPrice.text = food.foodprice
        holder.txtfoodRating.text = food.foodrating
        Picasso.get().load(food.foodimg).error(R.drawable.ic_baseline_fastfood_24).into(holder.foodimage)
        holder.homeContent.setOnClickListener {
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("food_id", food.food_id)
            intent.putExtra("foodname",holder.txtfoodname.text.toString())
            context.startActivity(intent)
        }
    }
    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtfoodname = view.findViewById<TextView>(R.id.foodnametxt)
        val txtfoodPrice = view.findViewById<TextView>(R.id.foodpricetxt)
        val txtfoodRating = view.findViewById<TextView>(R.id.foodrating)
        val foodimage = view.findViewById<ImageView>(R.id.foodimg)
        val homeContent =view.findViewById<RelativeLayout>(R.id.homeContent)
    }
}