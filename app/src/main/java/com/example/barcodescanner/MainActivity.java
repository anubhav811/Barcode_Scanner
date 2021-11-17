package com.example.barcodescanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.Query;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView yourScans;



    FirebaseAuth fAuth;
    FirebaseFirestore fStore;




    public void scanBarCode(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Scanning Now...");
        intentIntegrator.initiateScan();
    }



    private FirestoreRecyclerAdapter adapter;
    private RecyclerView scanList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        String userID=fAuth.getCurrentUser().getUid();
        Log.d("userid",userID);



        yourScans=findViewById(R.id.yourScans);
        yourScans.setVisibility(View.INVISIBLE);

        btn = findViewById(R.id.scanBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarCode();

            }
        });


        scanList=findViewById(R.id.recView);


        Query query = fStore.collection(userID);
        Log.d("queryfstore",query.toString());
        FirestoreRecyclerOptions<ScansModel> options = new FirestoreRecyclerOptions.Builder<ScansModel>()
                .setQuery(query,ScansModel.class)
                .build();

        adapter= new FirestoreRecyclerAdapter<ScansModel, ScansViewHolder>(options) {
            @NonNull
            @Override
            public ScansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single,parent,false);
                return new ScansViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ScansViewHolder holder, int position, @NonNull ScansModel model) {
                            if(model.getScan_res()!=null && model.getScan_time()!=null){
                                yourScans.setVisibility(View.VISIBLE);
                                holder.desc_tv.setText(model.getScan_res());
                                holder.time_tv.setText("Scanned on: " +model.getScan_time());
                            }

            }
        };

        scanList.setHasFixedSize(true);
        scanList.setLayoutManager(new LinearLayoutManager(this));
        scanList.setAdapter(adapter);

        }

         private class ScansViewHolder extends RecyclerView.ViewHolder{
        private TextView desc_tv;
        private TextView time_tv;
            public ScansViewHolder(@NonNull View view){
                super(view);
                desc_tv=view.findViewById(R.id.desc_tv);
                time_tv=view.findViewById(R.id.time_tv);

            }
        }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            if(result.getContents()!=null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("Scan Result");
                builder.setPositiveButton("Scan Again?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanBarCode();
                    }
                }).setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore fStore;
                        FirebaseAuth fAuth;
                        fStore = FirebaseFirestore.getInstance();
                        fAuth = FirebaseAuth.getInstance();
                        String userID=fAuth.getCurrentUser().getUid();


                        DocumentReference documentReference = fStore.collection(String.valueOf(userID)).document();
                        Map<String, String> userInfo = new HashMap<>();
                        userInfo.put("scan_res",String.valueOf(result.getContents()));
                        userInfo.put("scan_time",DateFormat.getDateTimeInstance().format(new Date()));
                        documentReference.set(userInfo);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                Toast.makeText(this,"Cant find anything", Toast.LENGTH_LONG).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);

        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOut:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                AlertDialog OptionDialog = builder.create();
                builder.setTitle("Sign Out ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //SIGN OUT CODE
                        FirebaseAuth.getInstance().signOut();//logout
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OptionDialog.dismiss();
                    }
                });
                builder.show();
            default:
                return super.onOptionsItemSelected(item);


        }

}
}