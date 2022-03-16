package com.example.ebebewa_app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Martin Mbae on 09,June,2020.
 */
public class HighLightArrayAdapter extends ArrayAdapter<String> {

    private int mSelectedIndex = -1;

    public HighLightArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public HighLightArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public HighLightArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    public HighLightArrayAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public HighLightArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public HighLightArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }

    public void setSelection(int position) {
        mSelectedIndex = position;
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View itemView = super.getDropDownView(position, convertView, parent);

        if (position == mSelectedIndex) {
            itemView.setBackgroundColor(Color.rgb(56, 184, 226));
        } else {
            itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        return itemView;
    }
}