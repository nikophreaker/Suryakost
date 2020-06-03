package com.nikxphreaker.suryakost;

import android.annotation.SuppressLint;
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
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nikxphreaker.suryakost.Model.Pembayaran;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

public class BayarActivity extends AppCompatActivity {
    private String title = "Suryakost";
    TextView id_user,harga,ket;
    MaterialEditText lama;
    Button bayarkamar;
    ImageView strukbayar;

    Uri FilePathUri;
    int Image_Request_Code = 7;
    ProgressDialog progressDialog ;
    FirebaseUser firebaseuser;
    StorageReference storageReference;
    DatabaseReference pembayaran_ref;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_bayar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBarTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ket = findViewById(R.id.ket);
        harga = findViewById(R.id.harga);
        bayarkamar = findViewById(R.id.bayar);
        setCurrency(harga);
        strukbayar = findViewById(R.id.strukbayar);
        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("StrukBayar");
        pembayaran_ref = FirebaseDatabase.getInstance().getReference("Pembayaran");
        final Pembayaran pembayaran = (Pembayaran) getIntent().getSerializableExtra("data");
        progressDialog = new ProgressDialog(BayarActivity.this);

        strukbayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);
            }
        });
        pembayaran_ref.addValueEventListener(new ValueEventListener() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                    Pembayaran pembayaran = snapshot.getValue(Pembayaran.class);
                                                    if(pembayaran.getId_user().equals(firebaseuser.getUid())){
                                                        harga.setText(pembayaran.getHarga());
                                                        if (!pembayaran.getStruk().equals("")) {
                                                            ket.setText(R.string.sudah_bukti);
                                                            bayarkamar.setText(R.string.kirim_lagi);
                                                            new DownLoadImageTask(strukbayar).execute(pembayaran.getStruk());
                                                            bayarkamar.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    PesanKamar();
                                                                    startActivity(new Intent(BayarActivity.this, MainActivity.class));
                                                                }
                                                            });
                                                        } else if (pembayaran.getStatus().equals("Sudah Bayar")){
                                                            startActivity(new Intent(BayarActivity.this,MainActivity.class));
                                                        }
                                                        else {
                                                            bayarkamar.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    UploadImage();
                                                                    startActivity(new Intent(BayarActivity.this, MainActivity.class));
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
    }

    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(BayarActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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


    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                int x = bitmap.getWidth();
                int y = bitmap.getHeight();
                strukbayar.setImageBitmap(Bitmap.createScaledBitmap(bitmap, x, y, false));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Intent getActIntent(Activity activity) {
        // kode untuk pengambilan Intent
        return new Intent(activity, BayarActivity.class);
    }

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

    public void PesanKamar(){
        if (FilePathUri != null) {
            progressDialog.setTitle("Data sedang dikirim....");
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
                    pembayaran_ref = FirebaseDatabase.getInstance().getReference("Pembayaran");
                    final Uri downloadUri = task.getResult();
                    pembayaran_ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            for (DataSnapshot snapshot : dataSnapshot2.getChildren()){
                                Pembayaran pembayaran = snapshot.getValue(Pembayaran.class);
                                if(pembayaran.getId_user().equals(firebaseuser.getUid())){
                                    pembayaran.setKey(snapshot.getKey());
                                    pembayaran.setStruk(downloadUri.toString());
                                    pembayaran_ref.child(pembayaran.getKey()).child("struk").setValue(pembayaran.getStruk());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Data berhasil dikirim ", Toast.LENGTH_LONG).show();
                }
            });
        } else if (FilePathUri == null){
            pembayaran_ref = FirebaseDatabase.getInstance().getReference("Pembayaran");
            final Pembayaran pembayaran = (Pembayaran) getIntent().getSerializableExtra("data");
            final String downloadUri = pembayaran.getStruk();
            pembayaran_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                    for (DataSnapshot snapshot : dataSnapshot2.getChildren()){
                        Pembayaran pembayaran = snapshot.getValue(Pembayaran.class);
                        if(pembayaran.getId_user().equals(firebaseuser.getUid())){
                            pembayaran.setKey(snapshot.getKey());
                            pembayaran.setStruk(downloadUri);
                            pembayaran_ref.child(pembayaran.getKey()).child("struk").setValue(pembayaran.getStruk());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Data berhasil dikirim ", Toast.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(findViewById(R.id.dibayar), "Tolong periksa kembali gambar", Snackbar.LENGTH_LONG).show();
            //Toast.makeText(TambahKamarActivity.this, "(Update)Tolong periksa kembali field dan gambar", Toast.LENGTH_LONG).show();
        }
    }

    public void UploadImage() {
        if (FilePathUri != null) {
            progressDialog.setTitle("Data sedang dikirim...");
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
                    final Uri downloadUri = task.getResult();
                    pembayaran_ref = FirebaseDatabase.getInstance().getReference("Pembayaran");
                    pembayaran_ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            for (DataSnapshot snapshot : dataSnapshot2.getChildren()){
                                Pembayaran pembayaran = snapshot.getValue(Pembayaran.class);
                                if(pembayaran.getId_user().equals(firebaseuser.getUid())){
                                    pembayaran.setKey(snapshot.getKey());
                                    pembayaran.setStruk(downloadUri.toString());
                                    pembayaran_ref.child(pembayaran.getKey()).child("struk").setValue(pembayaran.getStruk());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Data berhasil dikirim ", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Snackbar.make(findViewById(R.id.dibayar), "Tolong periksa kembali data dan gambar", Snackbar.LENGTH_LONG).show();
            //Toast.makeText(TambahKamarActivity.this, "(Tambah)Tolong periksa kembali field dan gambar", Toast.LENGTH_LONG).show();
        }
    }
}
