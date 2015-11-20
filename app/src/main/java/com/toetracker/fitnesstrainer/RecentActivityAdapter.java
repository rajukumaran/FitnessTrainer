package com.toetracker.fitnesstrainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by rajmarappan on 11/19/15.
 */
public class RecentActivityAdapter extends ArrayAdapter<ExerciseActivity> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public RecentActivityAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    /**
     * Returns the view for a specific item on the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final ExerciseActivity currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);
        final TextView txtExercise = (TextView) row.findViewById(R.id.txtExercise);
        txtExercise.setText(currentItem.ExerciseName);
        txtExercise.setEnabled(true);


        final TextView txtUnits = (TextView) row.findViewById(R.id.txtUnits);
        txtUnits.setText(currentItem.Unit1Name  + ":" + currentItem.Unit1Value +", " +
                currentItem.Unit2Name  + ":" + currentItem.Unit2Value +", " +
                currentItem.Unit3Name  + ":" + currentItem.Unit3Value );
        txtUnits.setEnabled(true);


        return row;
    }
}