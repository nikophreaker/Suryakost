package com.nikxphreaker.suryakost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.nikxphreaker.suryakost.Adapter.KamarListAdapter;
import com.nikxphreaker.suryakost.Fragments.HomeFragment;
import com.nikxphreaker.suryakost.Fragments.MessageFragment;
import com.nikxphreaker.suryakost.Fragments.NotifFragment;
import com.nikxphreaker.suryakost.Fragments.ProfileFragment;
import com.nikxphreaker.suryakost.Fragments.TagihanFragment;
import com.nikxphreaker.suryakost.Model.Kamar;
import com.nikxphreaker.suryakost.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView botNav;
    FirebaseUser firebaseuser;
    DatabaseReference reference;
    private String title = "Suryakost";
    KamarListAdapter kamarListAdapter;
    private RecyclerView sewakamar;
    private List<Kamar> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setActionBarTitle(title);

        botNav = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        botNav.setOnNavigationItemSelectedListener(navListener);
        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());
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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_profil:
                            selectedFragment = new ProfileFragment();
                            break;
//                        case R.id.nav_message:
//                            selectedFragment = new MessageFragment();
//                            break;
                        case R.id.nav_payment:
                            selectedFragment = new TagihanFragment();
                            break;
//                        case R.id.nav_notif:
//                            selectedFragment = new NotifFragment();
//                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    return true;
                }
            };

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

        return true;
    }

    public void search(String harga, final String luas, final String fasilitas){
        FirebaseDatabase.getInstance().getReference().child("Kamar").equalTo("harga").startAt(harga).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                if(dataSnapshot.child("luas").exists() && dataSnapshot.child("fasilitas").exists()){
                    if(dataSnapshot.child("luas").getValue(String.class).equals(luas) && dataSnapshot.child("fasilitas").getValue(String.class).equals(fasilitas)){
                            //list.add(kamar);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
