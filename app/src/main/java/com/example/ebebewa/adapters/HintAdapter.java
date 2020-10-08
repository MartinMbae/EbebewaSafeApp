package com.example.ebebewa.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Martin Mbae on 04,June,2020.
 */
public class HintAdapter extends ArrayAdapter<String> {


    public HintAdapter(Context context, int resource) {
        super(context, resource);
    }

    public HintAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public HintAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    public HintAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public HintAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    public HintAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}