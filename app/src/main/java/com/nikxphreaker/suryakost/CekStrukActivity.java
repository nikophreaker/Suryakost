package com.nikxphreaker.suryakost;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikxphreaker.suryakost.Fragments.HomeFragment;
import com.nikxphreaker.suryakost.Model.KamarIsi;
import com.nikxphreaker.suryakost.Model.Pembayaran;
import com.nikxphreaker.suryakost.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zolad.zoominimageview.ZoomInImageView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;
import static java.lang.Integer.parseInt;

public class CekStrukActivity extends AppCompatActivity {
    private String title = "Suryakost";
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;
    TextView dateResult,penyewa,id_user,id_kamare,hargaKamar,lama,tgls;
    Button tagih,konfirmasi;
    Date date;
    String key_pemb,lamas,tgl_depan,id_kamar,id_users,key_kamaris,tgl;
    FirebaseUser firebaseuser;
    DatabaseReference reference,dreference,dreference2,pembayaran_ref,isi_reference;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_cek_struk);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBarTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        id_kamar = intent.getStringExtra("id_kamar");
        id_users = intent.getStringExtra("id_user");
        final String nomor = intent.getStringExtra("nomor");
        final String nama = intent.getStringExtra("username");
        final String lokasi = intent.getStringExtra("lokasi");
        final String fasilitas = intent.getStringExtra("fasilitas");
        final String luas = intent.getStringExtra("luas");
        final String harga = intent.getStringExtra("harga");
        final String struk = intent.getStringExtra("struk");
        lamas = intent.getStringExtra("lama");
        tgl_depan = intent.getStringExtra("tgl_depan");
        tgl = intent.getStringExtra("tgl");
        key_pemb = intent.getStringExtra("key");
        //key_kamaris = intent.getStringExtra("kamarisi_key");
        setData(nama,nomor,lokasi,fasilitas,luas,harga,struk,lamas,tgl_depan);

        id_user = findViewById(R.id.id_user);
        tagih = findViewById(R.id.tagih);
        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());
        konfirmasi = findViewById(R.id.konfirmasikamar);
        pembayaran_ref = FirebaseDatabase.getInstance().getReference("Pembayaran").child(key_pemb);
        pembayaran_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getApplicationContext() == null) {
                    return;
                }
                Pembayaran pembayaran = dataSnapshot.getValue(Pembayaran.class);
                if(pembayaran.getStatus().equals("Sudah Bayar")){
                    konfirmasi.setVisibility(View.INVISIBLE);
                    tagih.setVisibility(View.VISIBLE);
                }else
                {konfirmasi.setVisibility(View.VISIBLE);
                    tagih.setVisibility(View.INVISIBLE);}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        konfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                masukin_kekamar();
                try {
                    konfirmasi_bayar();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(CekStrukActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        tagih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pembayaran_ref = FirebaseDatabase.getInstance().getReference("Pembayaran");
                pembayaran_ref.child(key_pemb).child("status").setValue("Belum Bayar");
                startActivity(new Intent(CekStrukActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

    }

    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void setData(String nama, String nokamar, String lokasi, String fas, String luas,String harga, String gambar, String lamas, String tgl){
        penyewa = findViewById(R.id.penyewa);
        TextView noKamar = findViewById(R.id.nomor);
        TextView lokasis = findViewById(R.id.lokasi);
        TextView fasilitas = findViewById(R.id.fasilitas);
        TextView luasKamar = findViewById(R.id.luas);
        ZoomInImageView image = findViewById(R.id.strukbayar);
        hargaKamar = findViewById(R.id.harga);
        lama = findViewById(R.id.lama);
        tgls = findViewById(R.id.hasil_tgl);
        setCurrency(hargaKamar);
        penyewa.setText(nama);
        noKamar.setText(nokamar);
        lokasis.setText(lokasi);
        fasilitas.setText(fas);
        luasKamar.setText(luas);
        hargaKamar.setText(harga);
        lama.setText(lamas);
        Long fucek = Long.parseLong(tgl);
        tgls.setText(getDate(fucek,"dd-MM-yyyy"));
        Glide.with(this)
                .load(gambar)
                .apply(new RequestOptions().override(1000, 1000))
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

    public static String getDate(long milliSeconds, String dateFormat){
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void konfirmasi_bayar() throws ParseException {
        pembayaran_ref = FirebaseDatabase.getInstance().getReference("Pembayaran");
        dreference = FirebaseDatabase.getInstance().getReference("Kamar_terisi");
        dreference2 = FirebaseDatabase.getInstance().getReference("Kamar");
        pembayaran_ref.child(key_pemb).child("status").setValue("Sudah Bayar");
        int subtract = parseInt(lamas);
        int result = subtract - 1;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        cal.setTime(formatter.parse(tgls.getText().toString()));
        cal.add(Calendar.MONTH, 1);
        String TempBulan = Long.toString(cal.getTimeInMillis());
        pembayaran_ref.child(key_pemb).child("bulan_depan").setValue(TempBulan.trim());
        dreference2.child(id_kamar).child("tersedia").setValue("tidak");
        isi_reference = FirebaseDatabase.getInstance().getReference("Kamar_terisi");
//        pembayaran_ref.child(key_pemb).child("tgl_pembayaran").setValue(tgl_depan.trim());
        if(result == 0){
            final DatabaseReference myRef1 = pembayaran_ref.child(key_pemb);
            final DatabaseReference myRef2 = dreference.child(id_kamar);
            dreference2.child(id_kamar).child("tersedia").setValue("ya");
            isi_reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        KamarIsi kamarIsi = dataSnapshot1.getValue(KamarIsi.class);
                        kamarIsi.setKey(dataSnapshot1.getKey());
                        if(kamarIsi.getId_kamar().equals(id_kamar)){
                            isi_reference.child(kamarIsi.getKey()).removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            myRef1.removeValue();
            myRef2.removeValue();
        }
        else{
        pembayaran_ref.child(key_pemb).child("sisa_bayar").setValue(Integer.toString(result));
        pembayaran_ref.child(key_pemb).child("struk").setValue("");
        }
//        DatabaseReference cekKamarIsi = FirebaseDatabase.getInstance().getReference("Kamar_terisi");
//        cekKamarIsi.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
//                    for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
//                        if (!dataSnapshot2.hasChild(id_kamar)) {
//
//                        } else {
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    public void masukin_kekamar(){
        isi_reference = FirebaseDatabase.getInstance().getReference("Kamar_terisi");
        String TempKamar = id_kamar.trim();
        String TempUser = id_users.trim();
        String TempPemb = key_pemb.trim();
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//        cal.setTime(formatter.parse(tgls.getText().toString()));
//        cal.add(Calendar.MONTH, 1);
//        String TempAwal = Long.toString(cal.getTimeInMillis()).trim();
        String TempAwal = tgl.trim();
        String TempSisa = lama.getText().toString().trim();
        @SuppressWarnings("VisibleForTests")
        String MasukUploadId = isi_reference.push().getKey();
        KamarIsi kamarIsi = new KamarIsi(TempKamar, TempUser, TempPemb, TempAwal, TempSisa);
        isi_reference.child(MasukUploadId).setValue(kamarIsi);
    }
}
