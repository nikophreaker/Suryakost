package com.nikxphreaker.suryakost.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nikxphreaker.suryakost.AboutActivity;
import com.nikxphreaker.suryakost.BayarActivity;
import com.nikxphreaker.suryakost.EditProfileActivity;
import com.nikxphreaker.suryakost.LoginActivity;
import com.nikxphreaker.suryakost.MainActivity;
import com.nikxphreaker.suryakost.Model.Pembayaran;
import com.nikxphreaker.suryakost.R;
import com.nikxphreaker.suryakost.Model.User;
import com.nikxphreaker.suryakost.SudahBayarActivity;
import com.nikxphreaker.suryakost.TambahAdminActivity;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    CircleImageView image_profile;
    TextView username,adm;
    CardView logout,tagihan,tambahadm,editprofile,kontak_adm,about;

    DatabaseReference reference,pembayaran_ref;
    FirebaseUser fuser;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        image_profile = view.findViewById(R.id.profile_image);
        logout = view.findViewById(R.id.keluar);
        tagihan = view.findViewById(R.id.tagihan);
        about = view.findViewById(R.id.about);
        tambahadm = view.findViewById(R.id.tambahadmin);
        kontak_adm = view.findViewById(R.id.kontak_adm);

        editprofile = view.findViewById(R.id.edit_profile);
        adm = view.findViewById(R.id.adm);
        username = view.findViewById(R.id.username);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                adm.setText("Profile "+user.getLevel());
                if (getActivity() == null) {
                    return;
                }
                if (user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getActivity()).load(user.getImageURL()).into(image_profile);
                }
                if (user.getLevel().equals("admin")){
                    tagihan.setVisibility(View.GONE);
                    kontak_adm.setVisibility(View.GONE);
                } else if (user.getLevel().equals("user")){
                    tambahadm.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        kontak_adm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri uri = Uri.parse("smsto:123456789");
//                Intent i = new Intent(Intent.ACTION_SENDTO,uri);
//                i.setPackage("com.whatsapp");
//                startActivity(i);
                boolean installed = appInstaledOrNot("com.whatsapp");

                if (installed){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+"+6285891225823"+"&text=Permisi admin"));
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "WhatsApp belum ter-install di perangkat anda",Toast.LENGTH_SHORT).show();
                }
            }
        });

        tambahadm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TambahAdminActivity.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AboutActivity.class));
            }
        });

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        tagihan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pembayaran_ref = FirebaseDatabase.getInstance().getReference("Pembayaran");
                pembayaran_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Pembayaran pembayaran = postSnapshot.getValue(Pembayaran.class);
                            if (pembayaran.getId_user().equals(fuser.getUid())) {
                                if (pembayaran.getStatus().equals("Sudah Bayar")) {
                                    if (pembayaran == null){return;}
                                    startActivity(new Intent(getActivity(), SudahBayarActivity.class));
                                } else if (pembayaran.getStatus().equals("Belum Bayar")){
                                startActivity(new Intent(getActivity(), BayarActivity.class));
                                }
                            } else if(!pembayaran.getId_user().equals(fuser.getUid())){
                                Toast.makeText(getActivity(), "Tidak ada tagihan",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        return view;
    }

    private boolean appInstaledOrNot(String url){
        PackageManager packageManager = getActivity().getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(url,PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }catch (PackageManager.NameNotFoundException e){
            app_installed = false;
        }
        return app_installed;
    }

    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtention(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress..", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}
