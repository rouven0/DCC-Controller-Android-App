package com.traincon.modelleisenbahn_controller.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.traincon.modelleisenbahn_controller.R;
import com.traincon.modelleisenbahn_controller.database.AppDatabase;
import com.traincon.modelleisenbahn_controller.database.DatabaseViewModel;
import com.traincon.modelleisenbahn_controller.database.Loco;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

public class LocoListFragment extends Fragment {

    private AppDatabase database;
    private List<Loco> locos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_loco_list, container, false);
        DatabaseViewModel viewModel = new ViewModelProvider(requireActivity()).get(DatabaseViewModel.class);
        database = viewModel.appDatabase;

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                locos = database.locoDao().getAll();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView = view.findViewById(R.id.list);
        final ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(locos, database, getParentFragmentManager());
        recyclerView.setAdapter(adapter);


        view.findViewById(R.id.button_add_loco).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LocoListFragment.this)
                        .navigate(R.id.action_LocoListFragment_to_AddLocoFragment);
            }
        });
        return view;
    }

}