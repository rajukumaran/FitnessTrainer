package com.toetracker.fitnesstrainer;

/**
 * Created by rajmarappan on 9/27/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

/**
 * Created by rajmarappan on 9/27/15.
 */
public class TrainerAssociationAdapter extends ArrayAdapter<TrainerAssociation> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public TrainerAssociationAdapter(Context context, int layoutResourceId) {
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

        final TrainerAssociation currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);
        final TextView txtTrainee = (TextView) row.findViewById(R.id.textView);
        txtTrainee.setText(currentItem.TraineeID);
        txtTrainee.setEnabled(true);

            txtTrainee.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (mContext instanceof TrainerAssociationActivity) {
                        TrainerAssociationActivity activity = (TrainerAssociationActivity) mContext;
                        TrainerGlobal.TraineeName = currentItem.TraineeID.toString();
                        Intent loggedInIntent = new Intent(mContext, ExerciseInputActivity.class);
                        activity.startActivity(loggedInIntent);

                    }
                }
            });

        return row;
    }
}