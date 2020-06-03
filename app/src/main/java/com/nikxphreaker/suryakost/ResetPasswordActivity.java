package com.nikxphreaker.suryakost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ResetPasswordActivity extends AppCompatActivity {
    MaterialEditText email;
    TextView signin;
    Button sent;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = findViewById(R.id.email);
        signin = findViewById(R.id.signin);
        sent = findViewById(R.id.sent);

        firebaseAuth = FirebaseAuth.getInstance();

        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sent_email = email.getText().toString();

                if (sent_email.equals("")){
                    Toast.makeText(ResetPasswordActivity.this, R.string.mustfill, Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(sent_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, R.string.checkemail, Toast.LENGTH_SHORT).show();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
    public void onBackPressed(){
        Intent intent = new Intent(ResetPasswordActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
