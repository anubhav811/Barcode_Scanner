package com.example.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterFragment extends Fragment{
    public static final String TAG = "TAG";
    String userID;
    EditText mEmail,mPassword;
    Button btn_register;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ViewGroup root = (ViewGroup) inflater.inflate(R.layout.register_fragment,container,false);

            fAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();


            mEmail      = root.findViewById(R.id.et_email);
            mPassword   = root.findViewById(R.id.et_password);
            btn_register= root.findViewById(R.id.btn_register);

            btn_register.setOnClickListener(v -> {
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    return;
                }
                if(password.length() < 6){
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        FirebaseUser fUser = fAuth.getCurrentUser();
                        Toast.makeText(getActivity(), "User Created", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        startActivity(intent);

                    }
                    else {
                        Toast.makeText(getActivity(), "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            });
            return root;
        }
}