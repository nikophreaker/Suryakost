package com.nikxphreaker.suryakost;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikxphreaker.suryakost.Model.Kamar;
import com.nikxphreaker.suryakost.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    private String title = "Suryakost";
    MaterialEditText username, email, password, repassword, oldpassword;
    Button signup;
    TextView signin;

    FirebaseAuth auth;
    FirebaseUser fuser;
    DatabaseReference reference, references;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBarTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        oldpassword = findViewById(R.id.oldpassword);
        repassword = findViewById(R.id.retype);
        signup = findViewById(R.id.signup);
        auth = FirebaseAuth.getInstance();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userss = dataSnapshot.getValue(User.class);
                username.setText(userss.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        references = FirebaseDatabase.getInstance().getReference("Kamar");
        email.setText(fuser.getEmail());
        // Get auth credentials from the user for re-authentication
        final String txt_username = username.getText().toString();
        final String txt_email = email.getText().toString();
        final String txt_oldpassword = oldpassword.getText().toString();
        final String txt_password = password.getText().toString();
        final FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();

// Prompt the user to re-provide their sign-in credentials
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_oldpassword.equals("") && txt_password.equals("") && txt_email.equals(fuser.getEmail())){
                    HashMap<String, String> hashmap = new HashMap<>();
                    hashmap.put("username", txt_username);
                    hashmap.put("search", txt_username.toLowerCase());
                } else if (!txt_oldpassword.equals("") && !txt_password.equals("")){
                HashMap<String, String> hashmap = new HashMap<>();
                hashmap.put("username", txt_username);
                hashmap.put("search", txt_username.toLowerCase());
                final AuthCredential credential = EmailAuthProvider
                        .getCredential(users.getEmail(), txt_oldpassword);
                users.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull final Task<Void> task) {
                                if (task.isSuccessful()) {
                                    users.updatePassword(txt_password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(EditProfileActivity.this, "Password updated", Toast.LENGTH_SHORT);
                                            } else {
                                                Toast.makeText(EditProfileActivity.this, "Error password not updated", Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });
                                }
                                users.updateEmail(txt_email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EditProfileActivity.this, "User email address updated.", Toast.LENGTH_SHORT);
                                                }else {
                                                    Toast.makeText(EditProfileActivity.this, "Gagal Update Email", Toast.LENGTH_SHORT);
                                                }
                                            }
                                        });
                            }
                        });
                }
            }
        });


    }
    private void setActionBarTitle (String title){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}
