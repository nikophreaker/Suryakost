package com.nikxphreaker.suryakost.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikxphreaker.suryakost.Adapter.KamarListAdapter;
import com.nikxphreaker.suryakost.Model.Kamar;
import com.nikxphreaker.suryakost.Model.KamarIsi;
import com.nikxphreaker.suryakost.Model.Pembayaran;
import com.nikxphreaker.suryakost.Model.User;
import com.nikxphreaker.suryakost.R;
import com.nikxphreaker.suryakost.SudahBayarActivity;
import com.nikxphreaker.suryakost.TambahKamarActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    FloatingActionButton fab;
    TextView username,bujank;
    CircleImageView profile_image;
    FirebaseUser firebaseuser;
    DatabaseReference reference,pembayaran_ref,isi_reference;
    ProgressDialog progressDialog;
    private KamarListAdapter kamarListAdapter;
    private RecyclerView sewakamar;
    private List<Kamar> list;
    private List<KamarIsi> list2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container,false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data from Firebase Database");
        progressDialog.show();

        username = v.findViewById(R.id.username);
        bujank = v.findViewById(R.id.bujank);
        profile_image = v.findViewById(R.id.profile_image);
        fab = v.findViewById(R.id.tambah);
        fab.hide();

        sewakamar = v.findViewById(R.id.list_kamar);
        sewakamar.setHasFixedSize(true);
        sewakamar.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        pembayaran_ref = FirebaseDatabase.getInstance().getReference("Pembayaran");
        isi_reference = FirebaseDatabase.getInstance().getReference("Kamar_terisi");
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    return;
                }
                final User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                bujank.setText("Dashboard "+user.getLevel());
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    //change this
                    Glide.with(getContext()).load(user.getImageURL()).into(profile_image);
                }
                if(user.getLevel().equals("admin")){
                    fab.show();
                }
                if(user.getLevel().equals("admin")){
                    fab.show();
                }
//                pembayaran_ref.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                            Pembayaran pembayaran = postSnapshot.getValue(Pembayaran.class);
//                            if (pembayaran.getId_user().equals(firebaseuser.getUid())) {
//                                if (pembayaran.getStatus().equals("Sudah Bayar")) {
//                                    if (pembayaran == null){return;}
//                                    FragmentManager fragmentManager = getFragmentManager();
//                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                    fragmentTransaction.replace(R.id.fragment_container, new SudahBayarFragment());
//                                    fragmentTransaction.commit();
//                                    progressDialog.dismiss();
//                                }
//                            }
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
                isi_reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            KamarIsi kamarIsi = snapshot.getValue(KamarIsi.class);
                            if (kamarIsi == null){return;}
                            if (kamarIsi.getId_user().equals(firebaseuser.getUid()) && user.getLevel().equals("user")){
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, new MemberFragment());
                                fragmentTransaction.commit();
                                progressDialog.dismiss();
                            } else if (!kamarIsi.getId_user().equals(firebaseuser.getUid())){
//                                FragmentManager fragmentManager = getFragmentManager();
//                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                fragmentTransaction.replace(R.id.fragment_container, new ProfileFragment());
//                                fragmentTransaction.commit();
//                                progressDialog.dismiss();
                            } else if (!kamarIsi.getId_user().equals(firebaseuser.getUid()) && user.getLevel().equals("user")){
                        }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                showKamar();
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        startActivity(new Intent(getActivity(), TambahKamarActivity.class));
            }
        });
        progressDialog.dismiss();
        return v;
    }

    private void showKamar() {
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Kamar");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final Kamar kamar = snapshot.getValue(Kamar.class);
                        kamar.setKey(snapshot.getKey());
                        list.add(kamar);
                        isi_reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    KamarIsi kamarIsi = snapshot.getValue(KamarIsi.class);
                                    kamarIsi.setKey(snapshot.getKey());
                                    if (kamarIsi.getId_user().equals("")) {
                                        list2.add(kamarIsi);
                                    } else if (!kamarIsi.getId_user().equals("")) {}
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                }
                kamarListAdapter = new KamarListAdapter(getContext(), list, list2);
                sewakamar.setAdapter(kamarListAdapter);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getDetails()+" "+databaseError.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void showKamar2() {
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Kamar");
        isi_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    KamarIsi kamarIsi = snapshot.getValue(KamarIsi.class);
                    kamarIsi.setKey(snapshot.getKey());
                    if (kamarIsi.getId_user().equals("")) {
                        list2.add(kamarIsi);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                list.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    final Kamar kamar = snapshot.getValue(Kamar.class);
                                    kamar.setKey(snapshot.getKey());
                                    list.add(kamar);
                                }
                                kamarListAdapter = new KamarListAdapter(getContext(), list, list2);
                                sewakamar.setAdapter(kamarListAdapter);
                                progressDialog.dismiss();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                System.out.println(databaseError.getDetails()+" "+databaseError.getMessage());
                                progressDialog.dismiss();
                            }
                        });
                    } else if (!kamarIsi.getId_user().equals("")) {}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
