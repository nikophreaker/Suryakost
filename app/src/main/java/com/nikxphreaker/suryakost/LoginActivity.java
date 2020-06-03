package com.nikxphreaker.suryakost;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nikxphreaker.suryakost.Network.ConnectivityReceiver;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    MaterialEditText email,password;
    Button login;
    TextView signup,forgot;
    private Snackbar snackbar = null;

    FirebaseAuth auth;
    FirebaseUser firebaseuser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseuser != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
    super.onCreate(saveInstanceState);
    setContentView(R.layout.activity_login);
    //network.registerDefaultNetworkCallback();
       // jaringanTest();
        checkConnection();
    auth = FirebaseAuth.getInstance();
    email = findViewById(R.id.email);
    password = findViewById(R.id.password);
    login = findViewById(R.id.login);
    signup = findViewById(R.id.signup);
    forgot = findViewById(R.id.forgot);

        login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();
            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(LoginActivity.this, R.string.mustfill, Toast.LENGTH_SHORT).show();
            } else {
                auth.signInWithEmailAndPassword(txt_email,txt_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task){
                                if (task.isSuccessful()){
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, R.string.loginfail, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    });

    signup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    });

    forgot.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    });
    }
    boolean doubleback=false;
    @Override
    public void onBackPressed(){
        if(doubleback){
            super.onBackPressed();
            return;
        }
        this.doubleback=true;
        Toast.makeText(this,R.string.doubleback,Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleback=false;
            }
        },2000);
    }

    public void jaringanTest() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://www.google.com";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.logins), "Not Connected on Internet", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String myResponse = response.body().string();
                        Snackbar snackbar2 = Snackbar
                                .make(findViewById(R.id.logins), "Connected on Internet", Snackbar.LENGTH_SHORT);
                        snackbar2.show();
//                   LoginActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                           mTextViewResult.setText(myResponse);
//                        }
//                    });
                }
            }
        });
    }


    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    public void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "Tidak ada layanan internet";
            color = Color.RED;
        } else {
            message = "Terhubung dengan internet";
            color = Color.WHITE;
        }
        snackbar = Snackbar
                .make(findViewById(R.id.logins), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
