package com.example.wakati_kotlin.reciever

import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wakati_kotlin.R
import com.example.wakati_kotlin.adapter.DealerAdapter
import com.example.wakati_kotlin.api.RetrofitClass
import com.example.wakati_kotlin.databinding.ActivityDealersTotalListBinding
import com.example.wakati_kotlin.model.DealerModel
import com.example.wakati_kotlin.model.DealersResponse
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.model.UserData
import com.example.wakati_kotlin.model.UserListResponse
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DealersTotalListActivity : AppCompatActivity() {

    private var binding: ActivityDealersTotalListBinding? = null

    private lateinit var sharedPreferences: SharedPreferences

    private var userId: String? = null
    private var token: String = ""
    private var authToken: String = ""

    private var flag: String? = null


    private lateinit var adapter: DealerAdapter

    private var dealerList = ArrayList<DealerModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDealersTotalListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        StatusBarUtils.setStatusBar(this, true)

        flag = intent.getStringExtra("flag")

        Log.d("FLAG_1", flag ?: "")

        adapter = DealerAdapter(
            this, dealerList, flag ?: ""
        )

        binding!!.rvDealers.adapter = adapter



        binding!!.rvDealers.layoutManager = LinearLayoutManager(this)


        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)

        token = sharedPreferences.getString("token", "") ?: ""

        userId = sharedPreferences.getString("user_id", "") ?: ""

        authToken = "Bearer $token"

        Log.d("TOKEN", authToken)
        Log.d("USER_ID", userId!!)

        loadData()

        binding!!.btnBack.setOnClickListener {
            finish()
        }


    }

    private fun loadData() {

        when (flag) {

            "Total_Super_Dealers_Flow" -> {

                binding!!.heading.text = "Super Dealers"

                fetchSuperDealers()
                setupSwipe()
            }

            "Total_Dealers_Flow" -> {

                binding!!.heading.text = "Dealers"

                fetchDealers()
                setupSwipe()
            }

            "Total_Partner_Agent_Flow" -> {

                binding!!.heading.text = "Partner Agents"

                fetchPartnerAgents()
            }

            "Total_Partner_Agent_Dealers_Flow" -> {

                binding!!.heading.text = "Partner Agent Dealers"

                fetchPartnerAgentDealers()
            }

            "Total_PA_Super_Dealers_Flow" -> {

                binding!!.heading.text = "P A Super Dealer"

                fetchPASuperDealers()
                setupSwipe()
            }

            "Front_Desk_Flow" -> {

                binding!!.heading.text = "Front Desk"

                userId?.let { fetchFrontDesk(it) }
            }
        }
    }

    private fun fetchSuperDealers() {
        LoaderUtils.showLoader(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userId)
        jsonObject.addProperty("sub_category", "HURIMONEY")

        RetrofitClass.getRetrofit().getSuperDealers(

            object : Callback<UserListResponse> {

                override fun onResponse(
                    call: Call<UserListResponse>, response: Response<UserListResponse>
                ) {
                    LoaderUtils.hideLoader()

                    if (response.isSuccessful) {

                        updateList(response.body())
                    }
                }

                override fun onFailure(
                    call: Call<UserListResponse>, t: Throwable
                ) {
                    LoaderUtils.hideLoader()
                    Log.e("API_ERROR", t.message.toString())
                }
            }, authToken, jsonObject
        )
    }

    private fun fetchDealers() {
        LoaderUtils.showLoader(this)

        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userId)
        jsonObject.addProperty("sub_category", "HURIMONEY")

        RetrofitClass.getRetrofit().getDealers(

            object : Callback<UserListResponse> {

                override fun onResponse(
                    call: Call<UserListResponse>, response: Response<UserListResponse>
                ) {
                    LoaderUtils.hideLoader()

                    if (response.isSuccessful) {

                        updateList(response.body())
                    }
                }

                override fun onFailure(
                    call: Call<UserListResponse>, t: Throwable
                ) {
                    LoaderUtils.hideLoader()
                    Log.e("API_ERROR", t.message.toString())
                }
            }, authToken, jsonObject
        )
    }

    private fun fetchPartnerAgents() {
        LoaderUtils.showLoader(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userId)

        RetrofitClass.getRetrofit().getPartnerAgents(

            object : Callback<UserListResponse> {

                override fun onResponse(
                    call: Call<UserListResponse>, response: Response<UserListResponse>
                ) {
                    LoaderUtils.hideLoader()

                    if (response.isSuccessful) {

                        updateList(response.body())
                    }
                }

                override fun onFailure(
                    call: Call<UserListResponse>, t: Throwable
                ) {
                    LoaderUtils.hideLoader()
                    Log.e("API_ERROR", t.message.toString())
                }
            }, authToken, jsonObject
        )
    }

    private fun fetchPartnerAgentDealers() {
        LoaderUtils.showLoader(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userId)
        jsonObject.addProperty("sub_category", "PARTNER_AGENT")
        RetrofitClass.getRetrofit().getDealers(
            object : Callback<UserListResponse> {

                override fun onResponse(
                    call: Call<UserListResponse>, response: Response<UserListResponse>
                ) {
                    LoaderUtils.hideLoader()

                    if (response.isSuccessful) {

                        updateList(response.body())
                    }
                }

                override fun onFailure(
                    call: Call<UserListResponse>, t: Throwable
                ) {
                    LoaderUtils.hideLoader()
                    Log.e("API_ERROR", t.message.toString())
                }
            }, authToken, jsonObject
        )
    }

    private fun fetchPASuperDealers() {
        LoaderUtils.showLoader(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userId)
        jsonObject.addProperty("sub_category", "PARTNER_AGENT")

        RetrofitClass.getRetrofit().getSuperDealers(

            object : Callback<UserListResponse> {

                override fun onResponse(
                    call: Call<UserListResponse>, response: Response<UserListResponse>
                ) {
                    LoaderUtils.hideLoader()

                    if (response.isSuccessful) {

                        updateList(response.body())
                    }
                }

                override fun onFailure(
                    call: Call<UserListResponse>, t: Throwable
                ) {
                    LoaderUtils.hideLoader()
                    Log.e("API_ERROR", t.message.toString())
                }
            }, authToken, jsonObject
        )
    }


    private fun fetchFrontDesk(userId: String) {
        LoaderUtils.showLoader(this)
        val jsonObject = JsonObject()
//        jsonObject.addProperty("user_id", userId)

        RetrofitClass.getRetrofit().getFrontDesk(
            object : Callback<UserListResponse> {

                override fun onResponse(
                    call: Call<UserListResponse>, response: Response<UserListResponse>
                ) {
                    LoaderUtils.hideLoader()

                    if (response.isSuccessful) {

                        updateList(response.body())
                    }
                }


                override fun onFailure(
                    call: Call<UserListResponse>, t: Throwable
                ) {
                    LoaderUtils.hideLoader()
                }
            }, authToken, userId
        )
    }


    private fun updateList(
        response: UserListResponse?
    ) {

        val list = response?.content ?: response?.data ?: arrayListOf()

        Log.d(
            "LIST_SIZE", list.size.toString()
        )

        adapter.updateData(
            ArrayList(list)
        )
    }


    private fun setupSwipe() {

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false


            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder, direction: Int
            ) {

                val position = viewHolder.adapterPosition

                val item = dealerList[position]

                when (flag) {

                    "Total_Super_Dealers_Flow", "Total_PA_Super_Dealers_Flow" -> {

                        Toast.makeText(
                            this@DealersTotalListActivity,
                            "Block ${item.full_name}",
                            Toast.LENGTH_SHORT
                        ).show()

                        showBlockDialog(item)
                    }

                    "Total_Dealers_Flow", "Total_Partner_Agent_Dealers_Flow" -> {

                        Toast.makeText(
                            this@DealersTotalListActivity,
                            "Assign ${item.full_name}",
                            Toast.LENGTH_SHORT
                        ).show()

                        showAssignBottomSheet(item)
                    }
                }

                adapter.notifyItemChanged(position)
            }


            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView

                if (dX < 0) {

                    val paint = Paint()

                    val isBlockFlow =
                        flag == "Total_Super_Dealers_Flow" || flag == "Total_PA_Super_Dealers_Flow"

                    paint.color = if (isBlockFlow) Color.RED
                    else Color.parseColor("#2196F3")

                    c.drawRect(
                        itemView.right.toFloat() + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        paint
                    )

                    paint.color = Color.WHITE
                    paint.textSize = 40f

                    val text = if (isBlockFlow) "BLOCK"
                    else "ASSIGN"

                    c.drawText(
                        text, itemView.right - 250f, itemView.top + itemView.height / 2f, paint
                    )
                }

                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        }




        ItemTouchHelper(swipeHandler).attachToRecyclerView(
            binding!!.rvDealers
        )


    }


    private fun showBlockDialog(
        item: DealerModel
    ) {

        androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Block User").setMessage(
            "Are you sure you want to block ${item.full_name}?"
        ).setPositiveButton("Block") { _, _ ->

            Toast.makeText(
                this, "Blocked ${item.full_name}", Toast.LENGTH_SHORT
            ).show()

            // TODO:
            // call block api
        }.setNegativeButton("Cancel", null).show()
    }


    private fun showAssignBottomSheet(
        dealer: DealerModel
    ) {

        val dialog =
            com.google.android.material.bottomsheet.BottomSheetDialog(this)

        val view =
            layoutInflater.inflate(
                R.layout.bottom_assign_dealer,
                null
            )



        dialog.setContentView(view)

        dialog.show()
    }


    

}