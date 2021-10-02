package com.prakash.locationreminder.adapters;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prakash.locationreminder.BLL.DeleteTaskBLL;
import com.prakash.locationreminder.BLL.UpdateTaskBLL;
import com.prakash.locationreminder.R;
import com.prakash.locationreminder.StrictMod.StrictMod;
import com.prakash.locationreminder.interfaces.Url;
import com.prakash.locationreminder.models.LongLat;


import java.util.List;

import static com.prakash.locationreminder.interfaces.Url.cookie;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.Holder> {
    private Context context;
    private List<LongLat> longLatList;

    public LocationAdapter(Context context, List<LongLat> longLatList) {
        this.context = context;
        this.longLatList = longLatList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_lonlat, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final LongLat longLat = longLatList.get(i);
        holder.tvName.setText(longLat.getName());
        holder.tvTask.setText(longLat.getTask());
        holder.iEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.layout_add_alarm);
                final EditText etName = dialog.findViewById(R.id.etName);
                final EditText etTask = dialog.findViewById(R.id.etTask);
                TextView tvLatitude = dialog.findViewById(R.id.tvLatitude);
                TextView tvLongitude = dialog.findViewById(R.id.tvLongitude);
                Button btnAdd = dialog.findViewById(R.id.btnAdd);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);

                tvLatitude.setText("Latitude : " + longLat.getLat());
                tvLongitude.setText("Longitude : " + longLat.getLon());
                etName.setText(longLat.getName());
                etTask.setText(longLat.getTask());
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                            etName.setError("Please enter name.");
                            etName.requestFocus();
                            return;
                        }
                        LongLat longLat1 = new LongLat(etName.getText().toString().trim(), etTask.getText().toString().trim(), longLat.getLon(), longLat.getLat());
                        StrictMod.StrictMode();
                        UpdateTaskBLL bll = new UpdateTaskBLL(longLat1, longLat.get_id(), cookie);
                        if(bll.updateTask()){
                            Toast.makeText(context, "Alarm Update Successfully!!!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.show();
            }
        });

        holder.iDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Url.getEndPoints().deletelonglat(cookie, longLat.get_id());
                StrictMod.StrictMode();
                DeleteTaskBLL bll = null;
                bll = new DeleteTaskBLL(longLat, longLat.get_id(), cookie);
                if (bll.deleteTask()) {
                    Toast.makeText(context, "Alarm Delete Successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete alarm.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return longLatList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvName, tvTask;
        private ImageView iEdit, iDelete;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvTask = itemView.findViewById(R.id.tvTask);
            iEdit = itemView.findViewById(R.id.iEdit);
            iDelete = itemView.findViewById(R.id.iDelete);
        }
    }
}