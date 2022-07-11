package com.foodcart.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.foodcart.R
import com.foodcart.Connection
import com.foodcart.activity.LoginActivity
import com.foodcart.adapter.HomeAdapter
import com.foodcart.food
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment() {
    lateinit var recyclerHome: RecyclerView
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recycleraAdapter: HomeAdapter
    lateinit var layoutManager: LinearLayoutManager
    val foodlist = arrayListOf<food>()
    var ratingComparator = Comparator<food> { f1, f2 ->
        if (f1.foodrating.compareTo(f2.foodrating, true) == 0) {
            f1.foodname.compareTo(f2.foodname, true)
        } else {
            f1.foodrating.compareTo(f2.foodrating, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerHome = view.findViewById(R.id.homerecyclerview)
        progressBar = view.findViewById(R.id.homeprogressbar)
        progresslayout = view.findViewById(R.id.homeprogresslayout)
        layoutManager = LinearLayoutManager(activity)
        progressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (Connection().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    println("Response is $it")
                    try {
                        progresslayout.visibility = View.GONE
                        val res = it.getJSONObject("data")
                        val success = res.getBoolean("success")
                        if (success) {
                            val data = res.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val foodJsonObject = data.getJSONObject(i)
                                val foodObject = food(
                                    foodJsonObject.getInt("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("rating"),
                                    foodJsonObject.getString("cost_for_one"),
                                    foodJsonObject.getString("image_url")
                                )
                                foodlist.add(foodObject)
                                recycleraAdapter =
                                    HomeAdapter(activity as Context, foodlist)
                                recyclerHome.adapter = recycleraAdapter
                                recyclerHome.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred !!!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Unexpected Error Occurred !!$e",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("TAG", "error: $e")
                    }
                }, Response.ErrorListener {
                    //Here error will be handled
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred !!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "bb2347be92b5c0"
                        return headers
                    }

                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id =item.itemId
        if(id == R.id.action_sort) {
            Collections.sort(foodlist, ratingComparator)//sort the list in ascending order
            foodlist.reverse()
        }
        recycleraAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }
}




