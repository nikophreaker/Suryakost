package com.nikxphreaker.suryakost.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.nikxphreaker.suryakost.TambahKamarActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberFragment extends Fragment {
    FloatingActionButton fab;
    TextView username,bujank;
    CircleImageView profile_image;
    FirebaseUser firebaseuser;
    DatabaseReference reference,pembayaran_ref;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_member, container,false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data from Firebase Database");
        progressDialog.show();

        username = v.findViewById(R.id.username);
        bujank = v.findViewById(R.id.bujank);
        profile_image = v.findViewById(R.id.profile_image);

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
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
                progressDialog.dismiss();
//                berhenti.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        pembayaran_ref = FirebaseDatabase.getInstance().getReference("Pembayaran");
//                        pembayaran_ref.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if (getContext() == null) {
//                                    return;
//                                }
//                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                    final Pembayaran pembayaran = snapshot.getValue(Pembayaran.class);
//                                    pembayaran.setKey(snapshot.getKey());
//                                        if(dataSnapshot.hasChild(user.getId())){
//                                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
//                                            final DatabaseReference myRef2 = database.getReference().child("Kamar_terisi").child(user.getId());
//                                            myRef2.removeValue();
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        progressDialog.dismiss();


        return v;
    }
}
