package com.nikxphreaker.suryakost;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class TambahAdminActivity extends AppCompatActivity {
    private String title = "Suryakost";
    MaterialEditText username,email,password,repassword;
    Button signup;
    TextView signin;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_tambah_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBarTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.retype);
        signup = findViewById(R.id.signup);
        auth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_repassword = repassword.getText().toString();

                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password ) || TextUtils.isEmpty(txt_repassword)){
                    Toast.makeText(TambahAdminActivity.this, R.string.mustfill, Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6){
                    Toast.makeText(TambahAdminActivity.this, R.string.mustpass, Toast.LENGTH_SHORT).show();
                } else if (!txt_repassword.equals(txt_password)){
                    Toast.makeText(TambahAdminActivity.this, R.string.mustre, Toast.LENGTH_SHORT).show();
                } else {
                    register(txt_username, txt_email, txt_password);
                }
            }
        });
    }

    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void register (final String username, String email, String password){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashmap = new HashMap<>();
                            hashmap.put("id", userid);
                            hashmap.put("username", username);
                            hashmap.put("imageURL", "default");
                            hashmap.put("status", "offline");
                            hashmap.put("level", "admin");
                            hashmap.put("search", username.toLowerCase());
                        startActivity(new Intent(TambahAdminActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(TambahAdminActivity.this, R.string.cantregist,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
