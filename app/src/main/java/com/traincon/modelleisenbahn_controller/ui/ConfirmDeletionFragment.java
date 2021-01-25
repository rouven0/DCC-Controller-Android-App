package com.traincon.modelleisenbahn_controller.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import com.traincon.modelleisenbahn_controller.R;
import com.traincon.modelleisenbahn_controller.database.AppDatabase;
import com.traincon.modelleisenbahn_controller.database.Loco;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * This is the confirmation dialog box for the deletion of a loco
 * @see androidx.fragment.app.DialogFragment
 */
public class ConfirmDeletionFragment extends DialogFragment {
    private final ItemRecyclerViewAdapter parentViewAdapter;
    private final AppDatabase database;
    private final List<Loco> locos;
    private final int position;

    public ConfirmDeletionFragment(ItemRecyclerViewAdapter parentViewAdapter, AppDatabase database, List<Loco> locos, int position) {
        this.parentViewAdapter = parentViewAdapter;
        this.database = database;
        this.locos = locos;
        this.position = position;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.message_confirm_deletion_1) + locos.get(position).designation + getResources().getString(R.string.message_confirm_deletion_2))
                .setTitle(R.string.title_confirm_deletion)
                .setPositiveButton(R.string.positive_button_del, (dialog, id) -> {
                    Thread thread = new Thread(() -> {
                        database.locoDao().delete(locos.get(position));
                        locos.remove(position);
                    });
                    thread.start();
                    try {
                        thread.join();
                        parentViewAdapter.notifyDataSetChanged();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(R.string.negative_button_del, (dialog, id) -> {
                });
        return builder.create();
    }
}
