package com.traincon.modelleisenbahn_controller.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.traincon.modelleisenbahn_controller.R;
import com.traincon.modelleisenbahn_controller.database.Loco;

import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    private final List<Loco> locos;

    public ItemRecyclerViewAdapter(List<Loco> locos) {
        this.locos = locos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.designation.setText(locos.get(position).designation);
        holder.address.setText(String.format("%s", locos.get(position).address));
    }

    @Override
    public int getItemCount() {
        return locos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView designation;
        public final TextView address;

        public ViewHolder(View view) {
            super(view);
            designation = (TextView) view.findViewById(R.id.item_number);
            address = (TextView) view.findViewById(R.id.content);
        }
    }
}