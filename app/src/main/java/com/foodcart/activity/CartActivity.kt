package com.foodcart.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodcart.R
import com.example.foodcart.databinding.ActivityCartBinding
import com.foodcart.adapter.CartItemAdapter
import com.foodcart.adapter.MenuAdapter.Companion.isCartEmpty
import com.foodcart.modal.*
import com.google.firebase.database.DatabaseError
import com.google.gson.Gson
import com.razorpay.Checkout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    private var resId: Int = 0
    private var resName: String = ""
    private var orderlist=ArrayList<MenuFood>()
    lateinit var binding :ActivityCartBinding
    lateinit var cartItemAdapter: CartItemAdapter
    lateinit var rview: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rview=findViewById(R.id.rview)

        init()
        setupToolbar()
        setUpCartList()
        placeOrder()
    }


    private fun init() {
        resId = intent.getIntExtra("resId", 0)
        resName = intent.getStringExtra("resName").toString()
        binding.restname.text=resName
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbarcart)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpCartList() {
        val dbList= GetItemsFromDBAsync(applicationContext).execute().get()
        for (element in dbList) {
            orderlist.addAll(
                Gson().fromJson(element.foodItems, Array<MenuFood>::class.java).asList()
            )
        }
        binding.pbc.visibility = View.GONE
        if (orderlist.isEmpty()) {
            binding.cart.visibility = View.GONE
            binding.ll0.visibility=View.GONE
            binding.txt.visibility = View.VISIBLE
            binding.rview.visibility=View.GONE

        } else {
            binding.cart.visibility = View.VISIBLE
            binding.txt.visibility = View.GONE
            binding.rview.visibility=View.VISIBLE

        }
        cartItemAdapter = CartItemAdapter(orderlist, this)
        val mLayoutManager = LinearLayoutManager(this)
        rview.layoutManager = mLayoutManager
        rview.adapter = cartItemAdapter
        cartItemAdapter.notifyDataSetChanged()

        var sum = 0
        for (i in 0 until orderlist.size) {
            sum += orderlist[i].foodprice
        }
        val total = "Place Order(Total: Rs. $sum)"
        binding.orderbtn.text = total
    }


    private fun placeOrder() {

        binding.orderbtn.setOnClickListener {
            binding.pbc.visibility = View.VISIBLE
            binding.cart.visibility = View.INVISIBLE
            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/place_order/fetch_result"
            val jsonParams = JSONObject()
            jsonParams.put(
                "user_id",
                this.getSharedPreferences("FoodApp", Context.MODE_PRIVATE).getString(
                    "user_id"," "
                ) as String
            )
            jsonParams.put("restaurant_id", resId.toString())
            var sum = 0
            for (i in 0 until orderlist.size) {
                sum += orderlist[i].foodprice
            }
            jsonParams.put("total_cost", sum.toString())
            val foodArray = JSONArray()
            for (i in 0 until orderlist.size) {
                val foodId = JSONObject()
                foodId.put("food_item_id", orderlist[i].food_id)
                foodArray.put(i, foodId)
            }
            jsonParams.put("food", foodArray)
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val clearCart =
                                ClearDBAsync(applicationContext, resId.toString()).execute().get()
                            isCartEmpty = true
                            val dialog = Dialog(
                                this,
                                android.R.style.Theme_Black_NoTitleBar_Fullscreen
                            )
                            dialog.setContentView(R.layout.order_placed_dialog)
                            dialog.show()
                            dialog.setCancelable(false)
                            val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                            btnOk.setOnClickListener {
                                dialog.dismiss()
                                startActivity(Intent(this, MainActivity::class.java))
                                ActivityCompat.finishAffinity(this)
                            }
                        } else {
                            binding.cart.visibility = View.VISIBLE
                            Toast.makeText(this, "Some Error occurred", Toast.LENGTH_SHORT)
                                .show()
                        }

                    } catch (e: Exception) {
                        binding.cart.visibility = View.VISIBLE
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    binding.cart.visibility = View.VISIBLE
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "bb2347be92b5c0"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
            val sh=getSharedPreferences("user", MODE_PRIVATE)
            val name = sh?.getString("Name"," ").toString()
            val email = sh?.getString("Email"," ").toString()
            val finalamount = Math.round((sum).toFloat() * 100)
            val checkout = Checkout()
            checkout.setKeyID("rzp_test_7dcCypx4XYYSIQ")
            try {
                jsonParams.put("name", name)// to put name
                jsonParams.put("description", "Test payment")// put description
                jsonParams.put("theme.color", "")// to set theme color
                jsonParams.put("currency", "INR")// put the currency
                jsonParams.put("amount", finalamount)// put amount
                jsonParams.put("prefill.email", email)// put email
                checkout.open(this, jsonParams) // open razorpay to checkout activity
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        fun onCancelled(error: DatabaseError) {
        }

        @Override
        fun onPaymentSuccess(s: String) {
            // this method is called on payment success.
            Toast.makeText(
                this,
                "Payment successful :" + s,
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(this, CartActivity::class.java))
        }

        @Override
        fun onPaymentError(s: String) {
            // on payment failed.
            Toast.makeText(
                this,
                "Payment Failed due to error : " + s,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    class GetItemsFromDBAsync(val context: Context) : AsyncTask<Void, Void, List<orderentity>>() {
        override fun doInBackground(vararg params: Void?): List<orderentity> {
            val db = Room.databaseBuilder(context, MenuDatabase::class.java, "res-db").build()
            return db.orderDao().getAllOrders()
        }

    }

    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, MenuDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        if (ClearDBAsync(applicationContext, resId.toString()).execute().get()) {
            isCartEmpty = true
            onBackPressed()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        ClearDBAsync(applicationContext, resId.toString()).execute().get()
        isCartEmpty = true
        super.onBackPressed()
    }
}
