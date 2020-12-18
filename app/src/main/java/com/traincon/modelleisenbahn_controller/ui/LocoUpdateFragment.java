package com.traincon.modelleisenbahn_controller.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.traincon.modelleisenbahn_controller.R;
import com.traincon.modelleisenbahn_controller.database.AppDatabase;
import com.traincon.modelleisenbahn_controller.database.Loco;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class LocoUpdateFragment extends DialogFragment {
    private final ItemRecyclerViewAdapter parentViewAdapter;
    private final AppDatabase database;
    private final List<Loco> locos;
    private final int position;

    public LocoUpdateFragment(ItemRecyclerViewAdapter parentViewAdapter, AppDatabase database, List<Loco> locos, int position) {
        this.parentViewAdapter = parentViewAdapter;
        this.database = database;
        this.locos = locos;
        this.position = position;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = requireActivity().getLayoutInflater().inflate(R.layout.content_loco_update, null);
        final TextInputEditText designationInput = view.findViewById(R.id.input_designation);
        final TextInputEditText addressInput = view.findViewById(R.id.input_address);
        final Loco loco = locos.get(position);
        designationInput.setText(loco.designation);
        addressInput.setText(String.format("%s", loco.address));
        builder.setView(view)
                .setTitle(R.string.contentDescription_edit)
                .setPositiveButton(R.string.positive_button_edit, (dialog, id) -> {
                    Thread thread = new Thread(() -> {
                        if (!Objects.requireNonNull(designationInput.getText()).toString().equals("")) {
                            loco.setDesignation(designationInput.getText().toString());
                            locos.get(position).setDesignation(designationInput.getText().toString());
                        }
                        if (!Objects.requireNonNull(addressInput.getText()).toString().equals("")) {
                            try {

                                loco.setAddress(Integer.parseInt(addressInput.getText().toString()));
                                locos.get(position).setAddress(Integer.parseInt(addressInput.getText().toString()));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        database.locoDao().updateLoco(loco);
                    });
                    thread.start();
                    try {
                        thread.join();
                        parentViewAdapter.notifyDataSetChanged();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(R.string.negative_button_edit, (dialog, id) -> {
                });
        return builder.create();
    }
}
