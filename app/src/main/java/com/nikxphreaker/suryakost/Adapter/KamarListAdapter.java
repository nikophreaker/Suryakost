package com.nikxphreaker.suryakost.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikxphreaker.suryakost.DetailActivity;
import com.nikxphreaker.suryakost.Model.Kamar;
import com.nikxphreaker.suryakost.Model.KamarIsi;
import com.nikxphreaker.suryakost.Model.User;
import com.nikxphreaker.suryakost.R;
import com.nikxphreaker.suryakost.TambahKamarActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class KamarListAdapter extends RecyclerView.Adapter<KamarListAdapter.ViewHolder> {
    private Context mContext;
    private List<Kamar> listKamar;
    private List<KamarIsi> listKamarIsi;
    FirebaseUser firebaseuser;
    DatabaseReference reference;
    FirebaseDataListener listener;

    public KamarListAdapter(Context mContext, List<Kamar> list, List<KamarIsi> list2) {
        this.mContext = mContext;
        this.listKamar = list;
        this.listKamarIsi = list2;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_kamar, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Kamar kamar = listKamar.get(position);
        holder.nomor.setText(kamar.getNomor());
        holder.lokasi.setText(kamar.getLokasi());
        holder.luas.setText(kamar.getLuas());
        holder.harga.setText(kamar.getHarga());
        if (kamar.getGambar() == null){
            return;
        }
        if (kamar.getGambar().equals("default")) {
            holder.gambar.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(kamar.getGambar()).into(holder.gambar);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("id_kamar", kamar.getKey());
                intent.putExtra("gambar", kamar.getGambar());
                intent.putExtra("nomor", kamar.getNomor());
                intent.putExtra("lokasi", kamar.getLokasi());
                intent.putExtra("fasilitas", kamar.getFasilitas());
                intent.putExtra("luas", kamar.getLuas());
                intent.putExtra("harga", kamar.getHarga());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mContext == null) {
                    return;
                }
                User user = dataSnapshot.getValue(User.class);
                if(user.getLevel().equals("admin")){
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final Dialog dialog = new Dialog(mContext);
                            dialog.setContentView(R.layout.edit_delete_view);
                            dialog.setTitle("Pilih Aksi");
                            dialog.show();
                            Button editButton = dialog.findViewById(R.id.bt_edit_data);
                            Button delButton = dialog.findViewById(R.id.bt_delete_data);
                            editButton.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                            mContext.startActivity(TambahKamarActivity.getActIntent((Activity) mContext).putExtra("data", listKamar.get(position)));
                                        }
                                    }
                            );

                            delButton.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
//                                            dialog.dismiss();
//                                            listener.onDeleteData(listKamar.get(position), position);
                                            dialog.dismiss();
                                            deleteData(position);
                                        }
                                    }
                            );
                            return true;
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    @Override
    public int getItemCount() {
        return listKamar.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nomor,lokasi,luas,harga;
        public ImageView gambar;
        public ViewHolder(View itemView) {
            super(itemView);
            nomor = itemView.findViewById(R.id.no_kamar);
            lokasi = itemView.findViewById(R.id.lokasi);
            luas = itemView.findViewById(R.id.luas);
            harga = itemView.findViewById(R.id.harga);
            setCurrency(harga);
            gambar = itemView.findViewById(R.id.gambar_kamar);
        }
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

    private void deleteData(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setMessage("Do you want to delete this room?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final Kamar kamar = listKamar.get(position);
                        DatabaseReference myRef = database.getReference().child("Kamar").child(kamar.getKey());
                        myRef.removeValue();
                        DatabaseReference cekKamarIsi = database.getInstance().getReference();
                        cekKamarIsi.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("Kamar_terisi")){
                                    final KamarIsi kamarIsi = listKamarIsi.get(position);
                                    DatabaseReference myRef2 = database.getReference().child("Kamar_terisi").child(kamarIsi.getKey());
                                    myRef2.removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public interface FirebaseDataListener{
        void onDeleteData(Kamar kamar, int position);
    }
}
