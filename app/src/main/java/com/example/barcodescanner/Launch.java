package com.example.barcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;


public class Launch extends AppCompatActivity {

    FirebaseFirestore fstore;
    FirebaseAuth fAuth;



    public void getJoke(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://us-central1-dadsofunny.cloudfunctions.net/DadJokes/random/jokes";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String setup = response.get("setup").toString();
                            String punchline = response.get("punchline").toString();
                            Toast.makeText(getApplicationContext(),setup,Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),punchline,Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        getJoke();


        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                fAuth = FirebaseAuth.getInstance();
                fstore= FirebaseFirestore.getInstance();
                if (fAuth.getCurrentUser() != null) {
                    DocumentReference df = fstore.collection(fAuth.getCurrentUser().getUid()).document();
                    df.get().addOnSuccessListener(documentSnapshot -> {
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    });
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }, 3200);



    }

}