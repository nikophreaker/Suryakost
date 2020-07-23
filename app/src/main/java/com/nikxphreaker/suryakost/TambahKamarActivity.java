package com.nikxphreaker.suryakost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nikxphreaker.suryakost.Model.Kamar;
import com.nikxphreaker.suryakost.Model.KamarIsi;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TambahKamarActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    CheckBox ac,kipas,lemari,toilet;
    MaterialEditText nomor,lebar,panjang,harga;
    Button tambah;
    ImageView gambarkamar;
    String fasilitas = "";
    private String title = "Data Kamar";
    Spinner spin;
    String KamarUploadId;

    Uri FilePathUri;
    StorageReference storageReference;
    DatabaseReference databaseReference,isi_reference;
    int Image_Request_Code = 7;
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_tambah_kamar);

        ac = findViewById(R.id.ac);
        kipas = findViewById(R.id.kipas);
        lemari = findViewById(R.id.lemari);
        toilet = findViewById(R.id.toilet);
        nomor = findViewById(R.id.nomor);
        spin = findViewById(R.id.wilayah);
        lebar = findViewById(R.id.lebar);
        panjang = findViewById(R.id.panjang);
        harga = findViewById(R.id.harga);
        gambarkamar = findViewById(R.id.gambarkamar);
        tambah = findViewById(R.id.tambahkamar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.wilayah_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);


        setCurrency(harga);
        storageReference = FirebaseStorage.getInstance().getReference("Kamar");
        databaseReference = FirebaseDatabase.getInstance().getReference("Kamar");
        KamarUploadId = databaseReference.push().getKey();
        progressDialog = new ProgressDialog(TambahKamarActivity.this);

        final Kamar kamar = (Kamar) getIntent().getSerializableExtra("data");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBarTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gambarkamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);
            }
        });

        if (kamar != null) {
            nomor.setText(kamar.getNomor());
            harga.setText(kamar.getHarga());
            String compareValue = kamar.getLokasi();
            int spinnerPosition = adapter.getPosition(compareValue);
            spin.setSelection(spinnerPosition);
            if (kamar.getLuas() == null){return;}
            String luasas = kamar.getLuas();
            String luas = luasas.replace("Meter", "");
            String luases = luas.replace("x", "");
            lebar.setText(luases.substring(0,1));
            panjang.setText(luases.substring(1,2));
            new DownLoadImageTask(gambarkamar).execute(kamar.getGambar());
            tambah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UpdateData();
                    startActivity(new Intent(TambahKamarActivity.this, MainActivity.class));
                }
            });
        } else {
        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty(nomor.getText().toString()) && !isEmpty(harga.getText().toString()) && !isEmpty(lebar.getText().toString())
                        && !isEmpty(panjang.getText().toString())){
                    UploadImage();
                    Kamar_isi();
                } else {
                    Snackbar.make(findViewById(R.id.tambahkamar), "Data tidak boleh kosong", Snackbar.LENGTH_LONG).show();
                }}
        });
        }
    }

    private boolean isEmpty(String s) {
        // Cek apakah ada fields yang kosong, sebelum disubmit
        return TextUtils.isEmpty(s);
    }

    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(TambahKamarActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Select Image method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                int x = bitmap.getWidth();
                int y = bitmap.getHeight();
                gambarkamar.setImageBitmap(Bitmap.createScaledBitmap(bitmap, x, y, false));
            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }


    public void UploadImage() {

        if (FilePathUri != null) {

            progressDialog.setTitle("Data sedang ditambahkan...");
            progressDialog.show();
            final StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            storageReference2.putFile(FilePathUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference2.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (ac.isChecked()){
                                fasilitas += ac.getText().toString()+", ";
                            }
                            if (kipas.isChecked()){
                                fasilitas += kipas.getText().toString()+", ";
                            }
                            if (lemari.isChecked()){
                                fasilitas += lemari.getText().toString()+", ";
                            }
                            if (toilet.isChecked()){
                                fasilitas += toilet.getText().toString();
                            }
                            Uri downloadUri = task.getResult();
                            fasilitas = fasilitas.replaceAll(", $", "");
                            String TempNomor = nomor.getText().toString().trim();
                            String TempWilayah = spin.getSelectedItem().toString().trim();
                            String TempLuas = lebar.getText().toString()+"x"+panjang.getText().toString()+"Meter".trim();
                            String Harga = harga.getText().toString().trim();
                            String TempHarga= Harga.replaceAll("\\.", "");
                            String TempFasilitas = fasilitas.trim();
                            String Ketersediaan = "ya";
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Data berhasil ditambahkan ", Toast.LENGTH_LONG).show();
                            Kamar kamarUploadInfo = new Kamar(TempNomor, TempWilayah, TempFasilitas, TempLuas, TempHarga, downloadUri.toString(), Ketersediaan);
                            databaseReference.child(KamarUploadId).setValue(kamarUploadInfo);
                            startActivity(new Intent(TambahKamarActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    });
        }
        else {
            Snackbar.make(findViewById(R.id.tambahkamar), "(Tambah)Tolong periksa kembali data dan gambar", Snackbar.LENGTH_LONG).show();
            //Toast.makeText(TambahKamarActivity.this, "(Tambah)Tolong periksa kembali field dan gambar", Toast.LENGTH_LONG).show();
        }
    }

    private void setCurrency(final EditText harga){
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
                    harga.setSelection(clean.length());
                    harga.addTextChangedListener(this);
                }
            }
        });
    }

    public static Intent getActIntent(Activity activity) {
        // kode untuk pengambilan Intent
        return new Intent(activity, TambahKamarActivity.class);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    private void updateBarang(Kamar kamar) {
//        databaseReference.child("kamar") //akses parent index, ibaratnya seperti nama tabel
//                .child(kamar.getKey()) //select barang berdasarkan key
//                .setValue(kamar) //set value barang yang baru
//                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Snackbar.make(findViewById(R.id.tambah), "Data berhasil diupdatekan", Snackbar.LENGTH_LONG).setAction("Oke", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                finish();
//                            }
//                        }).show();
//                    }
//                });
//    }


    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    public void UpdateData() {

        if (FilePathUri != null) {
            progressDialog.setTitle("Data sedang diupdate...");
            progressDialog.show();
            final Kamar kamar = (Kamar) getIntent().getSerializableExtra("data");
            final StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            storageReference2.putFile(FilePathUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference2.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (ac.isChecked()){
                        fasilitas += ac.getText().toString()+", ";
                    }
                    if (kipas.isChecked()){
                        fasilitas += kipas.getText().toString()+", ";
                    }
                    if (lemari.isChecked()){
                        fasilitas += lemari.getText().toString()+", ";
                    }
                    if (toilet.isChecked()){
                        fasilitas += toilet.getText().toString();
                    }
                    Uri downloadUri = task.getResult();
                    fasilitas = fasilitas.replaceAll(", $", "");
                    String TempNomor = nomor.getText().toString().trim();
                    String TempWilayah = spin.getSelectedItem().toString().trim();
                    String TempLuas = lebar.getText().toString()+"x"+panjang.getText().toString()+"Meter".trim();
                    String Harga = harga.getText().toString().trim();
                    String TempHarga= Harga.replaceAll("\\.", "");
                    String TempFasilitas = fasilitas.trim();
                    String Ketersediaan = "ya";
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Data berhasil diupdate ", Toast.LENGTH_LONG).show();
                    @SuppressWarnings("VisibleForTests")
                    String KamarUploadId = kamar.getKey();
                    Kamar kamarUploadInfo = new Kamar(TempNomor, TempWilayah, TempFasilitas, TempLuas, TempHarga, downloadUri.toString(), Ketersediaan);
                    databaseReference.child(KamarUploadId).setValue(kamarUploadInfo);
                    startActivity(new Intent(TambahKamarActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        } else if (FilePathUri == null){
            final Kamar kamar = (Kamar) getIntent().getSerializableExtra("data");
            if (ac.isChecked()){
                fasilitas += ac.getText().toString()+", ";
            }
            if (kipas.isChecked()){
                fasilitas += kipas.getText().toString()+", ";
            }
            if (lemari.isChecked()){
                fasilitas += lemari.getText().toString()+", ";
            }
            if (toilet.isChecked()){
                fasilitas += toilet.getText().toString();
            }
            String downloadUri = kamar.getGambar();
            fasilitas = fasilitas.replaceAll(", $", "");
            String TempNomor = nomor.getText().toString().trim();
            String TempWilayah = spin.getSelectedItem().toString().trim();
            String TempLuas = lebar.getText().toString()+"x"+panjang.getText().toString()+"Meter".trim();
            String Harga = harga.getText().toString().trim();
            String TempHarga= Harga.replaceAll("\\.", "");
            String TempFasilitas = fasilitas.trim();
            String Ketersediaan = "ya";
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Data berhasil diupdate ", Toast.LENGTH_LONG).show();
            @SuppressWarnings("VisibleForTests")
            String KamarUploadId = kamar.getKey();
            Kamar kamarUploadInfo = new Kamar(TempNomor, TempWilayah, TempFasilitas, TempLuas, TempHarga, downloadUri, Ketersediaan);
            databaseReference.child(KamarUploadId).setValue(kamarUploadInfo);
            startActivity(new Intent(TambahKamarActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        else {
            Snackbar.make(findViewById(R.id.tambahkamar), "(Update)Tolong periksa kembali data dan gambar", Snackbar.LENGTH_LONG).show();
            //Toast.makeText(TambahKamarActivity.this, "(Update)Tolong periksa kembali field dan gambar", Toast.LENGTH_LONG).show();
        }
    }

    public void Kamar_isi() {
        isi_reference = FirebaseDatabase.getInstance().getReference("Kamar_terisi");
        String TempKamar = KamarUploadId;
        String TempUser = "";
        String TempPemb = "";
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//        cal.setTime(formatter.parse(tgls.getText().toString()));
//        cal.add(Calendar.MONTH, 1);
//        String TempAwal = Long.toString(cal.getTimeInMillis()).trim();
        String TempAwal = "";
        String TempSisa = "";
        @SuppressWarnings("VisibleForTests")
        String MasukUploadId = isi_reference.push().getKey();
        KamarIsi kamarIsi = new KamarIsi(TempKamar, TempUser, TempPemb, TempAwal, TempSisa);
        isi_reference.child(MasukUploadId).setValue(kamarIsi);
    }

}
