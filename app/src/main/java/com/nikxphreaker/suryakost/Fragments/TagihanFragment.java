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
import com.nikxphreaker.suryakost.Adapter.PembayaranAdapter;
import com.nikxphreaker.suryakost.BayarActivity;
import com.nikxphreaker.suryakost.Model.Kamar;
import com.nikxphreaker.suryakost.Model.KamarIsi;
import com.nikxphreaker.suryakost.Model.Pembayaran;
import com.nikxphreaker.suryakost.Model.User;
import com.nikxphreaker.suryakost.R;
import com.nikxphreaker.suryakost.TambahKamarActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TagihanFragment extends Fragment {
    CircleImageView profile_image;
    FirebaseUser firebaseuser;
    DatabaseReference reference,isi_reference,pembayaran;
    ProgressDialog progressDialog;
    private PembayaranAdapter pembayaranAdapter;
    private RecyclerView tagihan;
    private List<Pembayaran> list;
    private List<KamarIsi> list2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tagihan, container,false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data from Firebase Database");
        progressDialog.show();

        profile_image = v.findViewById(R.id.profile_image);

        tagihan = v.findViewById(R.id.list_tagihan);
        tagihan.setHasFixedSize(true);
        tagihan.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    //change this
                    Glide.with(getContext()).load(user.getImageURL()).into(profile_image);
                }
                if(user.getLevel().equals("user")){
                    reference = FirebaseDatabase.getInstance().getReference("Pembayaran");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                Pembayaran pembayaran = dataSnapshot1.getValue(Pembayaran.class);
                                if (pembayaran.getId_user().equals(firebaseuser.getUid())){
                                    if (pembayaran.getStatus().equals("Sudah Bayar")){
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.fragment_container, new SudahBayarFragment());
                                        fragmentTransaction.commit();
                                        progressDialog.dismiss();
                                    } else if (pembayaran.getStatus().equals("Belum Bayar")) {
                                        startActivity(new Intent(getActivity(), BayarActivity.class));
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else if (user.getLevel().equals("admin")){
                showTagihan();}
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        progressDialog.dismiss();
        return v;
    }

    private void showTagihan() {
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        isi_reference = FirebaseDatabase.getInstance().getReference("Kamar_terisi");
        reference = FirebaseDatabase.getInstance().getReference("Pembayaran");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Pembayaran pembayaran = snapshot.getValue(Pembayaran.class);
                        pembayaran.setKey(snapshot.getKey());
                        list.add(pembayaran);
                    isi_reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                KamarIsi kamarIsi = snapshot.getValue(KamarIsi.class);
                                kamarIsi.setKey(snapshot.getKey());
                                list2.add(kamarIsi);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                pembayaranAdapter = new PembayaranAdapter(getContext(), list, list2);
                tagihan.setAdapter(pembayaranAdapter);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getDetails()+" "+databaseError.getMessage());
                progressDialog.dismiss();
            }
        });
    }
}
