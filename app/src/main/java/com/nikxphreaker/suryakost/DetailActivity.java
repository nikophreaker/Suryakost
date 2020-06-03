package com.nikxphreaker.suryakost;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikxphreaker.suryakost.Model.User;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private String title = "Suryakost";
    Button sewa;
    TextView nomor2,lokasi2,fasilitas2,luas2,harga2,id_kamar2;
    FirebaseUser firebaseuser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        sewa = findViewById(R.id.sewa);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBarTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id_kamar2 = findViewById(R.id.id_kamar);
        id_kamar2.setVisibility(View.INVISIBLE);
        nomor2 = findViewById(R.id.no_kamar);
        lokasi2 = findViewById(R.id.lokasi);
        fasilitas2 = findViewById(R.id.fasilitas);
        luas2 = findViewById(R.id.luas);
        harga2 = findViewById(R.id.harga);

        Intent intent = getIntent();
        final String id_kamar = intent.getStringExtra("id_kamar");
        final String gambar = intent.getStringExtra("gambar");
        final String nama = intent.getStringExtra("nomor");
        final String lokasi = intent.getStringExtra("lokasi");
        final String fasilitas = intent.getStringExtra("fasilitas");
        final String luas = intent.getStringExtra("luas");
        final String harga = intent.getStringExtra("harga");
        setData(id_kamar,gambar,nama,lokasi,fasilitas,luas,harga);

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getApplicationContext() == null) {
                    return;
                }
                User user = dataSnapshot.getValue(User.class);
                if(user.getLevel().equals("admin")){
                    sewa.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        sewa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, PemesananActivity.class);
                intent.putExtra("id_kamar", id_kamar2.getText());
                intent.putExtra("nomor", nomor2.getText());
                intent.putExtra("lokasi", lokasi2.getText());
                intent.putExtra("fasilitas", fasilitas2.getText());
                intent.putExtra("luas", luas2.getText());
                intent.putExtra("harga", harga2.getText());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void setData(String id_kamar, String gambar, String nokamar, String lokasi, String fas, String luas,String harga){

        TextView idKamar = findViewById(R.id.id_kamar);
        ImageView image = findViewById(R.id.gambar_kamar);
        TextView noKamar = findViewById(R.id.no_kamar);
        TextView lokasis = findViewById(R.id.lokasi);
        TextView fasilitas = findViewById(R.id.fasilitas);
        TextView luasKamar = findViewById(R.id.luas);
        TextView hargaKamar = findViewById(R.id.harga);
        setCurrency(hargaKamar);
        idKamar.setText(id_kamar);
        noKamar.setText(nokamar);
        lokasis.setText(lokasi);
        fasilitas.setText(fas);
        luasKamar.setText(luas);
        hargaKamar.setText(harga);

        Glide.with(this)
                .load(gambar)
                .apply(new RequestOptions().override(250, 250))
                .into(image);
    }

    private void setCurrency(final TextView harga){
        harga.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    harga.removeTextChangedListener(this);

                    Locale local = new Locale("id", "id");
                    String replaceable = String.format("[Rp,.\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    String cleanString = s.toString().replaceAll(replaceable,
                            "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    NumberFormat formatter = NumberFormat
                            .getCurrencyInstance(local);
                    formatter.setMaximumFractionDigits(0);
                    formatter.setParseIntegerOnly(true);
                    String formatted = formatter.format((parsed));

                    String replace = String.format("[Rp\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    String clean = formatted.replaceAll(replace, "");

                    current = formatted;
                    harga.setText(clean);
                    harga.addTextChangedListener(this);
                }
            }
        });
    }
}
