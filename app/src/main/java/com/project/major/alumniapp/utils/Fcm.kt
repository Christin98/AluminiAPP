
package com.project.major.alumniapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject

class Fcm(var context: Context?, var topics: String?, var title: String?, var msg: String?, var url: String?) {
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
            "key=" + "AAAABGwBjeE:APA91bEgSYnwLO8VGP3E5Qda_cqZD-DOOV1VqG_T-0dk64ygjeuKGKE-2E_w_wDb2LlPaqLV-K_2RjR5tQQBpZRFAqxAHmkn0eoSqwVfH6gmJ-SZQVEG9_PoAiyqPSixUsijZmVxkJb4"
    private val contentType = "application/json"
    val requestQueue: RequestQueue = Volley.newRequestQueue(this.context)

    fun init() {
    FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + topics)


    val topic = "/topics/" + topics //topic has to match what the receiver subscribed to

    val notification = JSONObject()
    val notificationBody = JSONObject()

    try {
        notificationBody.put("title", title)
        notificationBody.put("message", msg) //Enter your notification message
        notificationBody.put("icon", url)
        notificationBody.put("actionType", "new_post")
        notification.put("to", topic)
        notification.put("data", notificationBody)
        Log.e("TAG", "try")
    } catch (e: JSONException) {
        Log.e("TAG", "onCreate: " + e.message)
    }

    sendNotification(notification)
}
    private fun sendNotification(notification: JSONObject) {
        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, FCM_API, notification,
                Response.Listener { response ->
                    Log.i("TAG", "onResponse: $response")
                },
                Response.ErrorListener {
                    Toast.makeText(this.context, "Request error", Toast.LENGTH_LONG).show()
                    Log.i("TAG", "onErrorResponse: Didn't work")
                }) {

            @Throws(AuthFailureError::class)
            override fun  getHeaders() : Map<String, String>  {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        val socketTimeout = 1000 * 20 // 60 seconds
        val policy: RetryPolicy = DefaultRetryPolicy(socketTimeout, 6, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        jsonObjectRequest.setRetryPolicy(policy)
        requestQueue.add(jsonObjectRequest)
    }
}




