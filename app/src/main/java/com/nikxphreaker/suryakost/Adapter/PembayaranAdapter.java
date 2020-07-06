package com.nikxphreaker.suryakost.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikxphreaker.suryakost.CekStrukActivity;
import com.nikxphreaker.suryakost.Model.Kamar;
import com.nikxphreaker.suryakost.Model.KamarIsi;
import com.nikxphreaker.suryakost.Model.Pembayaran;
import com.nikxphreaker.suryakost.Model.User;
import com.nikxphreaker.suryakost.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PembayaranAdapter extends RecyclerView.Adapter<PembayaranAdapter.ViewHolder> {
    private Context mContext;
    private List<Pembayaran> listPembayaran;
    private List<KamarIsi> listKamarIsi;
    DatabaseReference reference,dreference,dreference2;
    String username;

    public PembayaranAdapter(Context mContext, List<Pembayaran> list, List<KamarIsi> list2) {
        this.mContext = mContext;
        this.listPembayaran = list;
        this.listKamarIsi = list2;
    }
    @NonNull
    @Override
    public PembayaranAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tagihan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PembayaranAdapter.ViewHolder holder, final int position) {
        final Pembayaran pembayaran = listPembayaran.get(position);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (mContext == null) {
                                                    return;
                                                }
                                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                                                User user = postSnapshot.getValue(User.class);
                                                if (user.getId().equals(pembayaran.getId_user())) {
                                                    holder.nama_penyewa.setText(user.getUsername());
                                                    username = user.getUsername();
                                                }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

        holder.lama_sewa.setText(pembayaran.getSisa_bayar());
        Long fucek = Long.parseLong(pembayaran.getTgl_pembayaran());
        holder.tgl.setText(getDate(fucek,"dd/MM/yyyy"));
        holder.tagih.setText(pembayaran.getHarga());
        if(pembayaran.getStatus().equals("Sudah Bayar")){
            holder.status.setTextColor(Color.GREEN);
        }
        holder.status.setText(pembayaran.getStatus());
        dreference = FirebaseDatabase.getInstance().getReference("Kamar");
        dreference2 = FirebaseDatabase.getInstance().getReference("KamarIsi");
        dreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mContext == null) {
                    return;
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Kamar kamar = snapshot.getValue(Kamar.class);
                    kamar.setKey(snapshot.getKey());
                    if (kamar.getKey().equals(pembayaran.getId_kamar())) {
                        holder.no_kamar.setText(kamar.getNomor());
                        holder.lokasi.setText(kamar.getLokasi());

                        dreference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    final KamarIsi kamarIsi = snapshot.getValue(KamarIsi.class);
                                    kamarIsi.setKey(snapshot.getKey());
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            //final KamarIsi kamarIsi = listKamarIsi.get(position);
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, CekStrukActivity.class);
                                intent.putExtra("id_kamar", kamar.getKey());
                                intent.putExtra("username", username);
                                intent.putExtra("nomor", kamar.getNomor());
                                intent.putExtra("lokasi", kamar.getLokasi());
                                intent.putExtra("fasilitas", kamar.getFasilitas());
                                intent.putExtra("luas", kamar.getLuas());
                                intent.putExtra("harga", kamar.getHarga());
                                intent.putExtra("struk", pembayaran.getStruk());
                                intent.putExtra("lama", pembayaran.getSisa_bayar());
                                intent.putExtra("tgl", pembayaran.getTgl_pembayaran());
                                intent.putExtra("tgl_depan", pembayaran.getBulan_depan());
                                intent.putExtra("id_user", pembayaran.getId_user());
                                intent.putExtra("key", pembayaran.getKey());
                               // intent.putExtra("kamarisi_key", kamarIsi.getKey());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }
                        });

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                final Dialog dialog = new Dialog(mContext);
                                dialog.setContentView(R.layout.edit_delete_view);
                                dialog.setTitle("Pilih Aksi");
                                dialog.show();
                                Button editButton = dialog.findViewById(R.id.bt_edit_data);
                                editButton.setVisibility(View.GONE);
                                Button delButton = dialog.findViewById(R.id.bt_delete_data);
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listPembayaran.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nama_penyewa,no_kamar,lokasi,lama_sewa,tgl,tagih,status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama_penyewa = itemView.findViewById(R.id.nama_penyewa);
            no_kamar = itemView.findViewById(R.id.no_kamar);
            lokasi = itemView.findViewById(R.id.lokasi);
            lama_sewa = itemView.findViewById(R.id.lama_sewa);
            tgl = itemView.findViewById(R.id.tgl);
            tagih = itemView.findViewById(R.id.tagih);
            status = itemView.findViewById(R.id.status);
            setCurrency(tagih);
        }
    }

    public static String getDate(long milliSeconds, String dateFormat){
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
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
                        final Pembayaran pembayaran = listPembayaran.get(position);
                        DatabaseReference myRef = database.getReference().child("Pembayaran").child(pembayaran.getKey());
                        myRef.removeValue();
                        DatabaseReference cekKamarIsi = database.getInstance().getReference();
                        cekKamarIsi.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("Kamar_terisi")){
                                    final KamarIsi kamarIsi = listKamarIsi.get(position);
                                    final DatabaseReference myRef2 = database.getReference().child("Kamar_terisi").child(kamarIsi.getKey());
                                    myRef2.removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
//                        myRef2.child("id_pembayaran").setValue("");
//                        myRef2.child("id_user").setValue("");
//                        myRef2.child("sisa_waktu").setValue("");
//                        myRef2.child("tgl_masuk").setValue("");
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
}
