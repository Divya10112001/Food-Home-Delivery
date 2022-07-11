package com.foodcart.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodcart.R
import com.foodcart.Connection
import com.foodcart.adapter.MenuAdapter
import com.foodcart.modal.MenuDatabase
import com.foodcart.modal.MenuEntity
import com.foodcart.modal.MenuFood
import com.foodcart.modal.orderentity
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class MenuActivity : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences
    private var id: Int = 0
    private var resname: String = ""

    val gson=Gson()
    val cartlist = arrayListOf<MenuFood>()
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        sharedPreferences =getSharedPreferences("FoodApp", Context.MODE_PRIVATE) as SharedPreferences
        var recycleraAdapter: MenuAdapter
        val recycler = findViewById<RecyclerView>(R.id.menu)
        val progressbar = findViewById<ProgressBar>(R.id.menuprogressbar)
        val cartorder = findViewById<Button>(R.id.cartbtn)
        val foodlist = arrayListOf<MenuFood>()
        val layoutManager = LinearLayoutManager(this)
         resname = intent.getStringExtra("foodname").toString()
         id = intent.getIntExtra("food_id",0)
        val toolbar = findViewById<Toolbar>(R.id.toolbarmenu)
        setSupportActionBar(toolbar)
        supportActionBar?.title = resname
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        cartorder.visibility = View.GONE
   val queue = Volley.newRequestQueue(this)
   val url = "http://13.235.250.119/v2/restaurants/fetch_result/$id"
     if (Connection().checkConnectivity(this)) {
     val jsonObjectRequest =
        object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
            println("Response is $it")
            try {
                progressbar.visibility = View.GONE
                val res = it.getJSONObject("data")
                val success = res.getBoolean("success")
                if (success) {
                    val data = res.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val foodJsonObject = data.getJSONObject(i)
                        val foodObject = MenuFood(
                            foodJsonObject.getString("id"),
                            foodJsonObject.getString("name"),
                            foodJsonObject.getInt("cost_for_one"),
                        )
                        foodlist.add(foodObject)
                        recycleraAdapter =
                            MenuAdapter(this, foodlist
                                ,object : MenuAdapter.OnItemClickListener {
                                    override fun onAddItemClick(foodItem: MenuFood) {
                                        cartlist.add(foodItem)
                                        if (cartlist.size > 0) {
                                            cartorder.visibility = View.VISIBLE
                                            MenuAdapter.isCartEmpty = false
                                        }
                                    }
                                    @SuppressLint("NotifyDataSetChanged")
                                    override fun onRemoveItemClick(foodItem: MenuFood) {
                                        cartlist.remove(foodItem)
                                        if (cartlist.size == 0) {
                                            Toast.makeText(
                                                this@MenuActivity,
                                                "Empty Cart",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            cartorder.visibility = View.GONE
                                            MenuAdapter.isCartEmpty = true
                                        }
                                    }
                                })
                        recycler.layoutManager = layoutManager
                        recycler.adapter = recycleraAdapter
                        cartorder.setOnClickListener {
                            proceedToCart()
                        }

                    }

                } else {
                    Toast.makeText(
                        this,
                        "Some Error Occurred !!!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } catch (e: JSONException) {
                Toast.makeText(
                    this,
                    "Some Unexpected Error Occurred !!$e",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("TAG", "error: $e")
            }
        }, Response.ErrorListener {
            //Here error will be handled
            Toast.makeText(
                this,
                "Volley error occurred !!",
                Toast.LENGTH_SHORT
            ).show()
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "bb2347be92b5c0"
                return headers
            }
        }
      queue.add(jsonObjectRequest)
    }
}


     fun proceedToCart() {
         val items = gson.toJson(cartlist)
             val async = ItemsOfCart(this, id.toString(), items, 1).execute()
             val result = async.get()
             if (result) {
               intent = Intent(this, CartActivity::class.java)
                 intent.putExtra("resId", id)
                 intent.putExtra("resName", resname)
                 startActivity(intent)
             } else {
                 Toast.makeText(this, "Some unexpected error", Toast.LENGTH_SHORT)
                     .show()
             }
         }

}

class ItemsOfCart(
context: Context,
val restaurantId:String,
val fooditems :String,
val mode: Int
) : AsyncTask<Void, Void, Boolean>() {
val db = Room.databaseBuilder(context, MenuDatabase::class.java, "res-db").build()
override fun doInBackground(vararg params: Void?): Boolean {
    when (mode) {
        1 -> {
            db.orderDao().insertOrder(orderentity(restaurantId,fooditems))
            db.close()
            return true
        }

        2 -> {
            db.orderDao().deleteOrder(orderentity(restaurantId,fooditems))
            db.close()
            return true
        }
    }
         return false
  }
}


