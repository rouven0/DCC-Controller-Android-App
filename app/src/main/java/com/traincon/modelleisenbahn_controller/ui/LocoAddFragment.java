package com.traincon.modelleisenbahn_controller.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.google.android.material.textfield.TextInputEditText;
import com.traincon.modelleisenbahn_controller.R;
import com.traincon.modelleisenbahn_controller.database.AppDatabase;
import com.traincon.modelleisenbahn_controller.database.Loco;

import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

public class LocoAddFragment extends Fragment {

    private AppDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loco_add, container, false);

        DatabaseViewModel viewModel = new ViewModelProvider(requireActivity()).get(DatabaseViewModel.class);
        database = viewModel.appDatabase;

        final TextInputEditText designationInput = view.findViewById(R.id.input_designation);
        final TextInputEditText addressInput = view.findViewById(R.id.input_address);

        view.findViewById(R.id.button_save_loco).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.requireNonNull(designationInput.getText()).toString().equals("") && !Objects.requireNonNull(addressInput.getText()).toString().equals("")) {
                    try {
                        final Handler handler = new Handler(Looper.getMainLooper());
                        final Loco loco = new Loco();
                        loco.setDesignation(designationInput.getText().toString());
                        loco.setAddress(Integer.parseInt(addressInput.getText().toString()));

                        final Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                database.locoDao().insertLoco(loco);
                            }
                        });

                        Runnable getThreadStateRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (!thread.isAlive()) {
                                    NavHostFragment.findNavController(LocoAddFragment.this)
                                            .navigate(R.id.action_AddLocoFragment_to_LocoListFragment);
                                    handler.removeCallbacks(this);
                                } else {
                                    handler.post(this);
                                }
                            }
                        };
                        thread.start();
                        handler.post(getThreadStateRunnable);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        addressInput.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.animation_shake));
                    }

                }

            }
        });

        return view;
    }
}
