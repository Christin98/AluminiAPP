package com.project.major.alumniapp.utils;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.models.NotificationModel;
import com.project.major.alumniapp.models.User;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotification {

    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey =
            "key=" + "AAAABGwBjeE:APA91bEgSYnwLO8VGP3E5Qda_cqZD-DOOV1VqG_T-0dk64ygjeuKGKE-2E_w_wDb2LlPaqLV-K_2RjR5tQQBpZRFAqxAHmkn0eoSqwVfH6gmJ-SZQVEG9_PoAiyqPSixUsijZmVxkJb4";
    private String contentType = "application/json";

    String msg = "this is test message,.,,.,.";
    String title = "Alumni App";
    String token = "token";
    String icon = "https://firebasestorage.googleapis.com/v0/b/alumniconnect-a3500.appspot.com/o/logo.png?alt=media&token=3c1d7435-7467-4903-b4c8-e6c85556cdd9";
    String userid;
    Context context;

    JSONObject obj = null;
    JSONObject objData = null;
    RequestQueue requestQueue;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public FcmNotification(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(this.context);
    }

    public void sendNotification(Context context, DatabaseReference reference, String mediaId, String likesId, boolean feed) {
        this.context = context;
        reference.child(mediaId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (feed) {
                    userid = dataSnapshot.child("userId").getValue(String.class);
                    if (dataSnapshot.child("feed_image_url").getValue(String.class) != null) {
                        icon = dataSnapshot.child("feed_image_url").getValue(String.class);
                    } else {
                        icon = "https://firebasestorage.googleapis.com/v0/b/alumniconnect-a3500.appspot.com/o/logo.png?alt=media&token=3c1d7435-7467-4903-b4c8-e6c85556cdd9";
                    }
                    getToken(userid);
                } else {
                    userid = dataSnapshot.child("user_id").getValue(String.class);
                    getToken(userid);
                    if (dataSnapshot.child("url").getValue(String.class) != null) {
                        icon = dataSnapshot.child("url").getValue(String.class);
                    } else {
                        icon = "https://firebasestorage.googleapis.com/v0/b/alumniconnect-a3500.appspot.com/o/logo.png?alt=media&token=3c1d7435-7467-4903-b4c8-e6c85556cdd9";
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child(mediaId).child("likes").child(likesId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    String uid = dataSnapshot1.getValue(String.class);
                    if (!userid.equals(uid)) {
                        FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                msg = user.getUser_name() + " liked your post";
                                send();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        msg = "You liked your own post ";
                        send();
                        TastyToast.makeText(context, "You liked your own post", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void send() {
        try {
            obj = new JSONObject();
            objData = new JSONObject();

            obj.put("title", title);
            obj.put("message", msg);
            obj.put("icon",icon);
            obj.put("actionType", "new_like");
            objData.put("to", token);
            objData.put("data",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FCM_API, objData,
                response -> Log.e("!_@@_SUCESS", response + ""),
                error -> Log.e("!_@@_Errors--", error + "")) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        int socketTimeout = 1000 * 20;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 6, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    private void getToken(String userid) {
        FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                token = dataSnapshot.child("token").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                token = null;
            }
        });
    }

    public void commentNotification(String uid, String userName, String profile, String comment, String node, String mediaId) {
        FirebaseDatabase.getInstance().getReference("alumni_app").child(node).child(mediaId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (node.equals("Feeds")) {
                    userid = dataSnapshot.child("userId").getValue(String.class);
                    if (dataSnapshot.child("feed_image_url").getValue(String.class) != null) {
                        icon = dataSnapshot.child("feed_image_url").getValue(String.class);
                    } else {
                        icon = "https://firebasestorage.googleapis.com/v0/b/alumniconnect-a3500.appspot.com/o/logo.png?alt=media&token=3c1d7435-7467-4903-b4c8-e6c85556cdd9";
                    }
                    getToken(userid);
                } else if (node.equals("Events")){
                    userid = dataSnapshot.child("user_id").getValue(String.class);
                    getToken(userid);
                    if (dataSnapshot.child("url").getValue(String.class) != null) {
                        icon = dataSnapshot.child("url").getValue(String.class);
                    } else {
                        icon = "https://firebasestorage.googleapis.com/v0/b/alumniconnect-a3500.appspot.com/o/logo.png?alt=media&token=3c1d7435-7467-4903-b4c8-e6c85556cdd9";
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (uid.equals(userid)) {
            TastyToast.makeText(context, "You commented on your own post", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
        } else {
            try {
                obj = new JSONObject();
                objData = new JSONObject();

                obj.put("title", title);
                obj.put("message", comment);
                obj.put("body", userName);
                obj.put("icon", profile);
                obj.put("icon2",icon);
                obj.put("actionType", "new_comment");
                objData.put("to", token);
                objData.put("data",obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FCM_API, objData,
                response -> Log.e("!_@@_SUCESS", response + ""),
                error -> Log.e("!_@@_Errors--", error + "")) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        int socketTimeout = 1000 * 20;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 6, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void sendadmin(String id, FirebaseUser user, String type) {
        NotificationModel not1 = new NotificationModel(type, user.getDisplayName(), String.valueOf(System.currentTimeMillis()), user.getUid());
        FirebaseDatabase.getInstance().getReference("alumni_app").child("notification").child(id).push().setValue(not1);
        NotificationModel not2 = new NotificationModel(type, "You", String.valueOf(System.currentTimeMillis()), id);
        FirebaseDatabase.getInstance().getReference("alumni_app").child("notification").child(user.getUid()).push().setValue(not2);

        getToken(id);
        try {
            obj = new JSONObject();
            objData = new JSONObject();
            obj.put("title", title);
            obj.put("message", user.getDisplayName());
            obj.put("type", type);
            obj.put("actionType", "admin");
            objData.put("to", token);
            objData.put("data",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FCM_API, objData,
                response -> Log.e("!_@@_SUCESS", response + ""),
                error -> Log.e("!_@@_Errors--", error + "")) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        int socketTimeout = 1000 * 20;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 6, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }
}
