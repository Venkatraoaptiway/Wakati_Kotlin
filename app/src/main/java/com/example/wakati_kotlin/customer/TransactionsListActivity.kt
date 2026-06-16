package com.example.wakati_kotlin.customer

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wakati_kotlin.api.RetrofitClass.Companion.getRetrofit
import com.example.wakati_kotlin.adapter.TransactionAdapter
import com.example.wakati_kotlin.model.StatementRequest
import com.example.wakati_kotlin.model.TransactionResponse
import com.example.wakati_kotlin.R
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityTransactionsListBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class TransactionsListActivity : AppCompatActivity() {
    var binding: ActivityTransactionsListBinding? = null
    var sharedPreferences: SharedPreferences? = null
    var transactionAdapter: TransactionAdapter? = null
    var authToken: String? = null
    var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsListBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        StatusBarUtils.setStatusBar(this, true)
        binding!!.rvTransferHistory.layoutManager = LinearLayoutManager(this)
        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)
        val token = sharedPreferences!!.getString("token", "")
        authToken = "Bearer $token"
        userId = sharedPreferences!!.getString("user_id", "")
        serviceTransactions(authToken, userId)
        binding!!.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (transactionAdapter != null) {
                    transactionAdapter!!.filter.filter(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.statementsDownload.setOnClickListener { v: View? -> showStatementDialog() }
    }

    private fun showStatementDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_statement)
        val etFromDate = dialog.findViewById<EditText>(R.id.etFromDate)
        val etToDate = dialog.findViewById<EditText>(R.id.etToDate)
        val btnDownload = dialog.findViewById<TextView>(R.id.btnDownload)
        etFromDate.setOnClickListener { v: View? -> showDatePicker(etFromDate) }
        etToDate.setOnClickListener { v: View? -> showDatePicker(etToDate) }
        btnDownload.setOnClickListener { v: View? ->
            val fromDate = etFromDate.text.toString().trim { it <= ' ' }
            val toDate = etToDate.text.toString().trim { it <= ' ' }
            if (fromDate.isEmpty()) {
                etFromDate.error = "Select From Date"
                return@setOnClickListener
            }
            if (toDate.isEmpty()) {
                etToDate.error = "Select To Date"
                return@setOnClickListener
            }
            downloadStatement(authToken, userId, fromDate, toDate)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dialog =
            DatePickerDialog(this, { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val date = year.toString() + "-" + String.format(
                    "%02d",
                    month + 1
                ) + "-" + String.format("%02d", dayOfMonth)
                editText.setText(date)
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        dialog.show()
    }

    fun serviceTransactions(authToken: String?, userId: String?) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userId)
        val retrofitClass = getRetrofit()
        retrofitClass.getTransactions(object : Callback<TransactionResponse> {
            override fun onResponse(
                call: Call<TransactionResponse?>,
                response: Response<TransactionResponse?>
            ) {
                if (response.body() != null) {
                    val gson = Gson()
                    Log.d("TRANSACTION_RESPONSE", gson.toJson(response.body()))
                }
                if (response.isSuccessful && response.body() != null) {
                    val transactionResponse = response.body()
                    if (transactionResponse!!.data != null && !transactionResponse.data.isEmpty()) {
                        transactionAdapter = TransactionAdapter(
                            this@TransactionsListActivity,
                            transactionResponse.data
                        )
                        binding!!.rvTransferHistory.adapter = transactionAdapter
                    } else {
                        Toast.makeText(
                            this@TransactionsListActivity,
                            "No transactions found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<TransactionResponse?>, t: Throwable) {
                Toast.makeText(this@TransactionsListActivity, t.message, Toast.LENGTH_LONG).show()
            }
        }, authToken!!, jsonObject)
    }

    private fun downloadStatement(
        token: String?,
        userId: String?,
        fromDate: String,
        toDate: String
    ) {
        val request = StatementRequest(userId, fromDate, toDate)
        val retrofitClass = getRetrofit()
        retrofitClass.downloadStatement(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful && response.body() != null) {
                    saveFile(response.body())
                } else {
                    Toast.makeText(
                        this@TransactionsListActivity,
                        "Download Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(this@TransactionsListActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        }, token!!, request)
    }

    private fun saveFile(body: ResponseBody?) {
        try {
            val fileName = "Statement_" + System.currentTimeMillis() + ".csv"
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val file = File(downloadsDir, fileName)
            val inputStream = body!!.byteStream()
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
            Log.d("FILE_PATH", file.absolutePath)
            Toast.makeText(this, "Statement Downloaded Successfully", Toast.LENGTH_LONG).show()
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(
                this,
                "$packageName.provider",
                file
            )
            intent.setDataAndType(
                uri,
                "text/csv"
            )
            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            startActivity(
                Intent.createChooser(
                    intent,
                    "Open Statement"
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}