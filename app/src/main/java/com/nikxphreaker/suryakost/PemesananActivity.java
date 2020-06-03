package com.nikxphreaker.suryakost;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikxphreaker.suryakost.Model.Kamar;
import com.nikxphreaker.suryakost.Model.Pembayaran;
import com.nikxphreaker.suryakost.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;

public class PemesananActivity extends AppCompatActivity {
    private String title = "Suryakost";
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;
    TextView dateResult,penyewa,id_user,id_kamare,hargaKamar;
    MaterialEditText lama;
    Button bt_datePicker,sewakamar;

    FirebaseUser firebaseuser;
    DatabaseReference reference,dreference,pembayaran_ref,isi_reference;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_pemesanan);

        penyewa = findViewById(R.id.penyewa);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBarTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String id_kamar = intent.getStringExtra("id_kamar");
        final String nama = intent.getStringExtra("nomor");
        final String lokasi = intent.getStringExtra("lokasi");
        final String fasilitas = intent.getStringExtra("fasilitas");
        final String luas = intent.getStringExtra("luas");
        final String harga = intent.getStringExtra("harga");
        setData(id_kamar,nama,lokasi,fasilitas,luas,harga);

        id_user = findViewById(R.id.id_user);
        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getApplicationContext() == null) {
                    return;
                }
                User user = dataSnapshot.getValue(User.class);
                penyewa.setText(user.getUsername());
                id_user.setText(user.getId());
                id_user.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        dateResult = findViewById(R.id.hasil_tgl);
        bt_datePicker = findViewById(R.id.tanggal);
        sewakamar = findViewById(R.id.sewakamar);
        lama = findViewById(R.id.lama);

        bt_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        sewakamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty(lama.getText().toString()) && !isEmpty(dateResult.getText().toString())) {
                    try {
                        PesanKamar();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else Snackbar.make(findViewById(R.id.tambahkamar), "Data tidak boleh kosong", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void setData(String id_kamar, String nokamar, String lokasi, String fas, String luas,String harga){
        id_kamare = findViewById(R.id.id_kamar);
        TextView noKamar = findViewById(R.id.nomor);
        TextView lokasis = findViewById(R.id.lokasi);
        TextView fasilitas = findViewById(R.id.fasilitas);
        TextView luasKamar = findViewById(R.id.luas);
        hargaKamar = findViewById(R.id.harga);
        id_kamare.setVisibility(View.INVISIBLE);
        setCurrency(hargaKamar);
        id_kamare.setText(id_kamar);
        noKamar.setText(nokamar);
        lokasis.setText(lokasi);
        fasilitas.setText(fas);
        luasKamar.setText(luas);
        hargaKamar.setText(harga);
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

    private void showDateDialog(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                dateResult.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

    public void PesanKamar() throws ParseException {
        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());
        dreference = FirebaseDatabase.getInstance().getReference("Kamar");
        pembayaran_ref = FirebaseDatabase.getInstance().getReference("Pembayaran");
        String TempKamar = id_kamare.getText().toString().trim();
        String TempUser = id_user.getText().toString().trim();
        String TempStatus = "Belum Bayar".trim();
        String tgl = dateResult.getText().toString();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = formatter.parse(tgl);
        String TempTglBayar = Long.toString(date.getTime()).trim();
        //inget myself ini tambah bulan dengan timestamp
        Calendar cal = Calendar.getInstance();
        cal.setTime(formatter.parse(tgl));
        cal.add(Calendar.MONTH, 1);
//        String tgl1 = dateFormatter.format(cal.getTime());
//        Date tgl2 = formatter.parse(tgl1);
        String TempBulanDepan = Long.toString(cal.getTimeInMillis()).trim();
        String TempStruk= "";
        String Harga = hargaKamar.getText().toString().trim();
        String TempHarga= Harga.replaceAll("\\.", "");
        String TempSisa = lama.getText().toString().trim();
        @SuppressWarnings("VisibleForTests")
        String PembUploadId = pembayaran_ref.push().getKey();
        Pembayaran kamarUploadInfo = new Pembayaran(TempUser, TempKamar, TempStatus, TempTglBayar, TempBulanDepan, TempHarga, TempStruk, TempSisa);
        pembayaran_ref.child(PembUploadId).setValue(kamarUploadInfo);
        startActivity(new Intent(PemesananActivity.this, BayarActivity.class));
    }
}
