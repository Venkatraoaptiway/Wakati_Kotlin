package com.example.wakati_kotlin.reciever

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.Loader
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wakati_kotlin.R
import com.example.wakati_kotlin.adapter.AssignSuperDealerAdapter
import com.example.wakati_kotlin.adapter.DealerAdapter
import com.example.wakati_kotlin.api.RetrofitClass
import com.example.wakati_kotlin.databinding.ActivityDealersTotalListBinding
import com.example.wakati_kotlin.model.AssignDealerRequest
import com.example.wakati_kotlin.model.BlockUserRequest
import com.example.wakati_kotlin.model.DealerModel
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.model.UserListResponse
import com.example.wakati_kotlin.partnerAgent.PartnerAgentHomeActivity
import com.example.wakati_kotlin.utils.GlobalToast
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.utils.ToastType
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
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

    private var selectedDealer: DealerModel? = null

    private lateinit var adapter: DealerAdapter

    private var dealerList = ArrayList<DealerModel>()

    private var isUnAssignFlow = false

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

            "Assign_Dealers_Flow" -> {

                binding!!.heading.text = "Dealers"

                fetchDealers()
                setupSwipe()
            }

            "Assign_PA_Dealers_Flow" -> {

                binding!!.heading.text = "P A Dealers"

                fetchPADealers()
                setupSwipe()

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


    private fun fetchPADealers() {
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
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

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

                        showBlockDialog(item)
                    }

                    "Assign_Dealers_Flow", "Assign_PA_Dealers_Flow" -> {

                        if (direction == ItemTouchHelper.LEFT) {

                            isUnAssignFlow = false
                            showAssignBottomSheet(item)

                        } else {

                            isUnAssignFlow = true
                            showUnAssignDialog(item)
                        }
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

                    // LEFT = ASSIGN

                    val paint = Paint()

                    paint.color = Color.parseColor("#2196F3")

                    c.drawRect(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        paint
                    )

                    paint.color = Color.WHITE
                    paint.textSize = 40f

                    c.drawText(
                        "ASSIGN", itemView.right - 250f, itemView.top + itemView.height / 2f, paint
                    )

                } else if (dX > 0) {

                    // RIGHT = UNASSIGN
                    val paint = Paint()

                    paint.color = Color.GRAY

                    c.drawRect(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        itemView.left + dX,
                        itemView.bottom.toFloat(),
                        paint
                    )

                    paint.color = Color.WHITE
                    paint.textSize = 40f

                    c.drawText(
                        "UNASSIGN", itemView.left + 50f, itemView.top + itemView.height / 2f, paint
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

        AlertDialog.Builder(this).setTitle("Block Super Dealer").setMessage(
            "Are you sure you want to block ${item.full_name}?"
        ).setPositiveButton("Yes") { _, _ ->

            blockSuperDealer(
                item.user_id ?: ""
            )
        }.setNegativeButton("Cancel", null).show()
    }


    private fun blockSuperDealer(
        superDealerId: String
    ) {

        LoaderUtils.showLoader(this)

        val request = BlockUserRequest(
            userId = superDealerId, status = "BLOCKED"
        )

        RetrofitClass.getRetrofit().blockSuperDealer(

            object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>, response: Response<LoginResponse>
                ) {

                    LoaderUtils.hideLoader()
                    if (flag == "Total_Super_Dealers_Flow") {
                        val intent =
                            Intent(this@DealersTotalListActivity, RecieverHomeActivity::class.java)
                        startActivity(intent)

                    } else {
                        val intent = Intent(
                            this@DealersTotalListActivity, PartnerAgentHomeActivity::class.java
                        )
                        startActivity(intent)
                    }

                    Toast.makeText(
                        this@DealersTotalListActivity, "Super Dealer Blocked", Toast.LENGTH_SHORT
                    ).show()

                }

                override fun onFailure(
                    call: Call<LoginResponse>, t: Throwable
                ) {
                    LoaderUtils.hideLoader()
                }
            }, authToken, request
        )
    }


    private fun showAssignBottomSheet(
        dealer: DealerModel
    ) {

        selectedDealer = dealer

        val dialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(
            R.layout.bottom_assign_dealer, null
        )

        val rvSuperDealers = view.findViewById<RecyclerView>(
            R.id.rvSuperDealers
        )

        rvSuperDealers.layoutManager = LinearLayoutManager(this)

        dialog.setContentView(view)

        loadSuperDealersForAssign(
            rvSuperDealers, dialog
        )

        dialog.show()
    }


    private fun showUnAssignDialog(
        dealer: DealerModel
    ) {

        selectedDealer = dealer

        val dialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(
            R.layout.bottom_assign_dealer, null
        )

        val rvSuperDealers = view.findViewById<RecyclerView>(
            R.id.rvSuperDealers
        )

        rvSuperDealers.layoutManager = LinearLayoutManager(this)

        dialog.setContentView(view)

        loadSuperDealersForAssign(
            rvSuperDealers, dialog
        )

        dialog.show()
    }

    private fun loadSuperDealersForAssign(
        recyclerView: RecyclerView, dialog: BottomSheetDialog
    ) {

        LoaderUtils.showLoader(this)


        val jsonObject = JsonObject()

        jsonObject.addProperty(
            "user_id", userId
        )

        if (flag == "Assign_Dealers_Flow") {
            jsonObject.addProperty(
                "sub_category", "HURIMONEY"
            )
        } else {
            jsonObject.addProperty(
                "sub_category", "PARTNER_AGENT"
            )

        }

        RetrofitClass.getRetrofit().getSuperDealers(

            object : Callback<UserListResponse> {

                override fun onResponse(
                    call: Call<UserListResponse>, response: Response<UserListResponse>
                ) {

                    LoaderUtils.hideLoader()


                    if (response.isSuccessful) {

                        val list = response.body()?.data ?: arrayListOf()

                        recyclerView.adapter = AssignSuperDealerAdapter(
                            this@DealersTotalListActivity, ArrayList(list)
                        ) { superDealer ->

                            dialog.dismiss()

                            showAssignConfirmDialog(
                                selectedDealer?.user_id ?: "",
                                superDealer.user_id ?: "",
                                superDealer.full_name ?: ""
                            )
                        }
                    }
                }

                override fun onFailure(
                    call: Call<UserListResponse>, t: Throwable
                ) {
                    LoaderUtils.hideLoader()


                }
            }, authToken, jsonObject
        )
    }


    private fun assignDealer(
        dealerId: String, superDealerId: String
    ) {

        LoaderUtils.showLoader(this)

        val request = AssignDealerRequest(
            superDealerId = superDealerId,
            receiverId = userId ?: "",
            dealerIds = arrayListOf(dealerId),
            action = "assign"

        )

        Log.d(
            "ASSIGN_REQUEST", Gson().toJson(request)
        )

        RetrofitClass.getRetrofit().assignDealer(

            object : Callback<LoginResponse> {


                override fun onResponse(
                    call: Call<LoginResponse>, response: Response<LoginResponse>
                ) {

                    LoaderUtils.hideLoader()


                    val loginResponse = response.body()!!

                    val apiResponse = response.body()

                    if (apiResponse != null) {

//                       GlobalToast(
//                            this@DealersTotalListActivity,
//                            apiResponse.message ?: "",
//                            Toast.LENGTH_LONG
//                        ).show()

                        GlobalToast.show(
                            this@DealersTotalListActivity,
                            apiResponse.message ?: "",
                            ToastType.SUCCESS
                        );


                        Log.d("assign_dealers_responce", apiResponse.message ?: "")

                        showSuccessDialog(
                            apiResponse.message ?: "Success"
                        )
                    }


                    if (flag == "Assign_Dealers_Flow") {
                        val intent = Intent(
                            this@DealersTotalListActivity, RecieverHomeActivity::class.java
                        )
                        startActivity(intent)

                    } else {
                        val intent = Intent(
                            this@DealersTotalListActivity, PartnerAgentHomeActivity::class.java
                        )
                        startActivity(intent)
                    }

                    showSuccessDialog(
                        "Dealer Assigned Successfully"
                    )

                    Log.d("responce ", loginResponse.message.toString())

                    fetchDealers()
                }

                override fun onFailure(
                    call: Call<LoginResponse>, t: Throwable
                ) {

                    LoaderUtils.hideLoader()

                    GlobalToast.show(
                        this@DealersTotalListActivity, t.message ?: "", ToastType.ERROR
                    );


                    Log.e(
                        "ASSIGN_ERROR", t.message ?: ""
                    )
                }
            }, authToken, request
        )
    }


    private fun unAssignDealer(
        dealerId: String, superDealerId: String
    ) {

        LoaderUtils.showLoader(this)

        val request = AssignDealerRequest(
            superDealerId = superDealerId,
            receiverId = userId ?: "",
            dealerIds = arrayListOf(dealerId),
            action = "unassign"

        )

        Log.d(
            "Un_ASSIGN_REQUEST", Gson().toJson(request)
        )

        RetrofitClass.getRetrofit().assignDealer(

            object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>, response: Response<LoginResponse>
                ) {

                    LoaderUtils.hideLoader()


                    val apiResponse = response.body()

                    if (apiResponse != null) {

                        GlobalToast.show(
                            this@DealersTotalListActivity,
                            apiResponse.message ?: "",
                            ToastType.SUCCESS
                        );


                        Log.d("unassign_dealers", apiResponse.message ?: "")

                    }

                    if (flag == "Assign_Dealers_Flow") {
                        val intent = Intent(
                            this@DealersTotalListActivity, RecieverHomeActivity::class.java
                        )
                        startActivity(intent)

                    } else {
                        val intent = Intent(
                            this@DealersTotalListActivity, PartnerAgentHomeActivity::class.java
                        )
                        startActivity(intent)
                    }

                    showSuccessDialog(
                        "Dealer UnAssigned Successfully"
                    )

                    Log.d("responce ", response.message())

                    fetchDealers()
                }

                override fun onFailure(
                    call: Call<LoginResponse>, t: Throwable
                ) {

                    LoaderUtils.hideLoader()


                    GlobalToast.show(
                        this@DealersTotalListActivity, t.message ?: "", ToastType.ERROR
                    );
                    Log.e(
                        "Un_ASSIGN_ERROR", t.message ?: ""
                    )
                }
            }, authToken, request
        )
    }


    private fun showAssignConfirmDialog(
        dealerId: String, superDealerId: String, superDealerName: String
    ) {

        val dialog = Dialog(this)

        dialog.setContentView(
            R.layout.dialog_assign
        )

        val txtTitle = dialog.findViewById<TextView>(R.id.txtTitle)

        val txtDealer = dialog.findViewById<TextView>(R.id.txtDealer)

        if (isUnAssignFlow) {
            txtTitle.text = "UnAssign Dealer"
        } else {
            txtTitle.text = "Assign Dealer"
        }

        txtDealer.text = superDealerName

        dialog.findViewById<Button>(R.id.btnConfirm).setOnClickListener {

            dialog.dismiss()

            if (isUnAssignFlow) {

                unAssignDealer(
                    dealerId, superDealerId
                )

            } else {

                assignDealer(
                    dealerId, superDealerId
                )
            }
        }

        dialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun showSuccessDialog(
        message: String
    ) {

        val dialog = Dialog(this)

        dialog.setContentView(
            R.layout.dialog_success
        )

        dialog.findViewById<TextView>(
            R.id.txtMessage
        ).text = message

        dialog.findViewById<Button>(
            R.id.btnClose
        ).setOnClickListener {

            dialog.dismiss()

            if (flag == "Assign_Dealers_Flow") {

                startActivity(
                    Intent(
                        this, RecieverHomeActivity::class.java
                    )
                )

            } else {

                startActivity(
                    Intent(
                        this, PartnerAgentHomeActivity::class.java
                    )
                )
            }

            finish()
        }

        dialog.show()
    }

}