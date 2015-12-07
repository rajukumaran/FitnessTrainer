package com.toetracker.fitnesstrainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.concurrent.ExecutionException;

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

    MobileServiceClient meia;
    MobileServiceTable<ExcerciseInput> mToDoTable;
    public RecentActivityAdapter(Context context, int layoutResourceId, MobileServiceClient mClient, MobileServiceTable<ExcerciseInput> mToDoTableIp) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        meia=mClient;
        mToDoTable= mToDoTableIp;
    }

    private float x1,x2;
    static final int MIN_DISTANCE = 50;
    //public ImageButton imgEdit,imgDelete;
    ExerciseActivity currentItem;
    String ExerciseName;
    /**
     * Returns the view for a specific item on the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        //ViewHolder viewHolder; //Use a viewholder for sufficent use of the listview

        final ExerciseActivity currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);


        final ImageButton imgEdit = (ImageButton) row.findViewById(R.id.imgEdit);
        final ImageButton imgDelete = (ImageButton) row.findViewById(R.id.imgDelete);
        final EditText txtEditUnit1 = (EditText)row.findViewById(R.id.txtEditUnit1);
        final EditText txtEditUnit2 = (EditText)row.findViewById(R.id.txtEditUnit2);
        final EditText txtEditUnit3 = (EditText)row.findViewById(R.id.txtEditUnit3);
        final ImageButton imgUpdate = (ImageButton) row.findViewById(R.id.imgUpdate);
        final ImageButton imgCancel = (ImageButton) row.findViewById(R.id.imgCancel);
        final TextView txtExercise = (TextView) row.findViewById(R.id.txtExercise);
        String CurrentValue1 = currentItem.ExerciseName + "\n" +
                currentItem.Unit1Name  + ": " + currentItem.Unit1Value +"    " +
                currentItem.Unit2Name  + ": " + currentItem.Unit2Value;
        if(!currentItem.Unit3Name.equals("")) {
            CurrentValue1 +=  "    " +
                    currentItem.Unit3Name + ": " + currentItem.Unit3Value;
        }
        final String CurrentValue= CurrentValue1;

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtEditUnit1.setVisibility(View.VISIBLE);
                txtEditUnit2.setVisibility(View.VISIBLE);
                txtEditUnit3.setVisibility(View.VISIBLE);
                imgUpdate.setVisibility(View.VISIBLE);
                imgCancel.setVisibility(View.VISIBLE);
                txtExercise.setVisibility(View.INVISIBLE);
                imgEdit.setVisibility(View.INVISIBLE);
                imgDelete.setVisibility(View.INVISIBLE);
                txtEditUnit1.setHint(currentItem.Unit1Name + ":" + currentItem.Unit1Value);
                txtEditUnit2.setHint(currentItem.Unit2Name + ":" + currentItem.Unit2Value);
                txtEditUnit3.setHint(currentItem.Unit3Name + ":" + currentItem.Unit3Value);

            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtEditUnit1.setVisibility(View.INVISIBLE);
                txtEditUnit2.setVisibility(View.INVISIBLE);
                txtEditUnit3.setVisibility(View.INVISIBLE);
                imgUpdate.setVisibility(View.INVISIBLE);
                imgCancel.setVisibility(View.INVISIBLE);
                txtExercise.setVisibility(View.VISIBLE);
                imgEdit.setVisibility(View.INVISIBLE);
                imgDelete.setVisibility(View.INVISIBLE);

                String TraineeName= TrainerGlobal.TraineeName;
                TraineeName = TraineeName.substring(TraineeName.indexOf('(')+1,TraineeName.indexOf(')'));
                final ExcerciseInput eI = new ExcerciseInput();
                eI.ExerciseDescID = currentItem.ExerciseDescID;
                eI.User= TraineeName;
                eI.Id=currentItem.Id;
                eI.Unit1=currentItem.Unit1Value;
                eI.Unit2=currentItem.Unit2Value;
                eI.Unit3=currentItem.Unit3Value;
                eI.ExerciseName= currentItem.ExerciseName;
                new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Exercise")
                        .setMessage("Are you sure to delete this Activity?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                remove(currentItem);
                                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        try {

                                            DeleteItem(eI);

                                        } catch (final Exception e) {
                                            int i=10;
                                            i=11;
                                            // createAndShowDialogFromTask(e, "Error");
                                        }

                                        return null;
                                    }
                                };

                                runAsyncTask(task);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();


            }
        });

        imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtEditUnit1.getText().toString().equals(""))
                    currentItem.Unit1Value=txtEditUnit1.getText().toString();
                if(!txtEditUnit2.getText().toString().equals(""))
                    currentItem.Unit2Value=txtEditUnit2.getText().toString();
                if(!txtEditUnit3.getText().toString().equals(""))
                    currentItem.Unit3Value=txtEditUnit3.getText().toString();
                String CurrentValue1 = currentItem.ExerciseName + "\n" +
                        currentItem.Unit1Name  + ": " + currentItem.Unit1Value +"    " +
                        currentItem.Unit2Name  + ": " + currentItem.Unit2Value;
                if(!currentItem.Unit3Name.equals("")) {
                    CurrentValue1 +=  "    " +
                            currentItem.Unit3Name + ": " + currentItem.Unit3Value;
                }
                txtExercise.setText(CurrentValue1);
                txtEditUnit1.setVisibility(View.INVISIBLE);
                txtEditUnit2.setVisibility(View.INVISIBLE);
                txtEditUnit3.setVisibility(View.INVISIBLE);
                imgUpdate.setVisibility(View.INVISIBLE);
                imgCancel.setVisibility(View.INVISIBLE);
                txtExercise.setVisibility(View.VISIBLE);
                imgEdit.setVisibility(View.INVISIBLE);
                imgDelete.setVisibility(View.INVISIBLE);
                String TraineeName= TrainerGlobal.TraineeName;
                TraineeName = TraineeName.substring(TraineeName.indexOf('(')+1,TraineeName.indexOf(')'));
                final ExcerciseInput eI = new ExcerciseInput();
                eI.ExerciseDescID = currentItem.ExerciseDescID;
                eI.User= TraineeName;
                eI.Id=currentItem.Id;
                eI.Unit1=currentItem.Unit1Value;
                eI.Unit2=currentItem.Unit2Value;
                eI.Unit3=currentItem.Unit3Value;
                eI.ExerciseName= currentItem.ExerciseName;
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {

                            checkItemInTable(eI);
                        } catch (final Exception e) {
                            int i=10;
                            i=11;
                           // createAndShowDialogFromTask(e, "Error");
                        }

                        return null;
                    }
                };

                runAsyncTask(task);

            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtEditUnit1.setVisibility(View.INVISIBLE);
                txtEditUnit2.setVisibility(View.INVISIBLE);
                txtEditUnit3.setVisibility(View.INVISIBLE);
                imgUpdate.setVisibility(View.INVISIBLE);
                imgCancel.setVisibility(View.INVISIBLE);
                txtExercise.setVisibility(View.VISIBLE);
                imgEdit.setVisibility(View.INVISIBLE);
                imgDelete.setVisibility(View.INVISIBLE);
            }
        });

        txtExercise.setText(CurrentValue );
        txtExercise.setEnabled(true);

        txtExercise.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE)
                        {
                            imgDelete.setVisibility(View.VISIBLE);
                            imgEdit.setVisibility(View.VISIBLE);
                           // Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show ();
                        }
                        else
                        {
                            // consider as something else - a screen tap for example
                        }
                        break;
                }
                return true;
            }
        });

//        final TextView txtUnits = (TextView) row.findViewById(R.id.txtUnits);
//        txtUnits.setText(currentItem.Unit1Name  + ": " + currentItem.Unit1Value +"    " +
//                currentItem.Unit2Name  + ": " + currentItem.Unit2Value +"    " +
//                currentItem.Unit3Name  + ": " + currentItem.Unit3Value );
//        txtUnits.setEnabled(true);



        return row;
    }
    public void checkItemInTable(ExcerciseInput item) throws ExecutionException, InterruptedException {
        mToDoTable = meia.getTable("ExcerciseInput",ExcerciseInput.class);

        mToDoTable.update(item).get();
    }

    public void DeleteItem(ExcerciseInput item) throws ExecutionException, InterruptedException {
        mToDoTable = meia.getTable("ExcerciseInput",ExcerciseInput.class);

        mToDoTable.delete(item).get();
    }
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
}