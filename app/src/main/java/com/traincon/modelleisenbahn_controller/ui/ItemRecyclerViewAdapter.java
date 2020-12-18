package com.traincon.modelleisenbahn_controller.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.traincon.modelleisenbahn_controller.R;
import com.traincon.modelleisenbahn_controller.database.AppDatabase;
import com.traincon.modelleisenbahn_controller.database.Loco;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    private final List<Loco> locos;
    private final AppDatabase database;
    private final FragmentManager fragmentManager;
    private final ItemRecyclerViewAdapter itemRecyclerViewAdapter = this;

    public ItemRecyclerViewAdapter(List<Loco> locos, AppDatabase database, FragmentManager fragmentManager) {
        this.locos = locos;
        this.database = database;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.designation.setText(locos.get(position).designation);
        holder.address.setText(String.format("%s", locos.get(position).address));
        holder.editButton.setOnClickListener(v -> new LocoUpdateFragment(itemRecyclerViewAdapter, database, locos, position).show(fragmentManager, "j"));
        holder.deleteButton.setOnClickListener(v -> new ConfirmDeletionFragment(itemRecyclerViewAdapter, database, locos, position).show(fragmentManager, "Confirm deletion"));
    }

    @Override
    public int getItemCount() {
        return locos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView designation;
        public final TextView address;
        public final ImageButton editButton;
        public final ImageButton deleteButton;

        public ViewHolder(View view) {
            super(view);
            designation = view.findViewById(R.id.loco_designation);
            address = view.findViewById(R.id.loco_address);
            editButton = view.findViewById(R.id.edit);
            deleteButton = view.findViewById(R.id.delete);
        }
    }
}