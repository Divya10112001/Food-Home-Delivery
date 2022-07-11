package com.foodcart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodcart.R
import com.foodcart.modal.MenuFood
import kotlinx.coroutines.NonDisposableHandle.parent

class CartItemAdapter(private val cartList: ArrayList<MenuFood>, val context: Context) :
    RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CartViewHolder {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.historyfoodrecycler, p0, false)
        return CartViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(p0: CartViewHolder, p1: Int) {
        val cartObject = cartList[p1]
        p0.itemName.text = cartObject.foodname
        val cost = "Rs. ${cartObject.foodprice}"
        p0.itemCost.text = cost
    }


    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.foodname)
        val itemCost: TextView = view.findViewById(R.id.foodrate)
    }
}