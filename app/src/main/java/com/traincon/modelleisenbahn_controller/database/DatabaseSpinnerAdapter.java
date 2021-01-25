package com.traincon.modelleisenbahn_controller.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.traincon.modelleisenbahn_controller.R;

import java.util.List;

/**
 * @see android.widget.Adapter
 * @see android.widget.BaseAdapter
 * @see android.widget.ListAdapter
 * @see android.widget.SpinnerAdapter
 */
public class DatabaseSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final LayoutInflater inflater;
    private final List<Loco> locos;

    public DatabaseSpinnerAdapter(Context context, List<Loco> locos) {
        this.inflater = LayoutInflater.from(context);
        this.locos = locos;
    }

    @Override
    public int getCount() {
        return locos.size();
    }

    @Override
    public Object getItem(int position) {
        return locos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return locos.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_spinner, parent, false);
        }
        Loco loco = (Loco) getItem(position);
        TextView textView = view.findViewById(R.id.text);
              textView.setText(loco.designation);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_spinner, parent, false);
        }
        Loco loco = (Loco) getItem(position);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(loco.designation);
        return view;
    }
}
