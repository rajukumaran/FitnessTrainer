package com.toetracker.fitnesstrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class ExerciseInputActivity extends AzureBaseActivity {

    Button btnExerciseSubmit,btnAddExercise;
    public EditText txtExerciseId;
    public EditText txtUnit1,txtUnit2,txtUnit3;
    String Unit1,Unit2,Unit3;
    String ID, Name;
    RecentActivityAdapter TA;
    MobileServiceTable<ExcerciseInput> mToDoTable;
    AutoCompleteTextView acTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_input);
        TextView txtView = (TextView)findViewById(R.id.txtVwTrainee);
        txtView.setText(TrainerGlobal.toCamelCase(TrainerGlobal.TraineeName));
        btnAddExercise = (Button)findViewById(R.id.btnAddExercise);
        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loggedInIntent = new Intent(getApplicationContext(), AddExercise.class);
                startActivity(loggedInIntent);
            }
        });
        btnExerciseSubmit = (Button)findViewById(R.id.btnExceriseSubmit);
        btnExerciseSubmit.setOnClickListener(btnExerciseSubmit_Click);

        //txtExerciseId = (EditText)findViewById(R.id.txtExerciseId);
       // btnExerciseSubmit.setOnClickListener(loginClickListener);
        txtUnit1= (EditText)findViewById(R.id.txtUnit1);
        txtUnit2= (EditText)findViewById(R.id.txtUnit2);
        txtUnit3= (EditText)findViewById(R.id.txtUnit3);
        AssetManager am = getAssets();
        TrainerGlobal.setAssetManager(am);
        //AssetManager manager = getAssets();
        acTextView = (AutoCompleteTextView) findViewById(R.id.autoComplete);

        acTextView.setAdapter(new SuggestionAdapter(this, acTextView.getText().toString()));
        acTextView.setOnItemClickListener(AutocompleteItemClickListner);
        acTextView.setHint("Type Exercise Name");
        acTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                btnExerciseSubmit.setEnabled(false);
                return false;
            }
        });
        RecentExerciseActivities();
        //ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.select_dialog_item,strValues);
        //acTextView.setThreshold(1);
        //acTextView.setAdapter(adapter);

    }

    View.OnClickListener btnExerciseSubmit_Click = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            List<ExcerciseData> lstEd = TrainerGlobal.getExercises();
            if(TrainerGlobal.GetSelectedExercise==null)
            {
                acTextView.setError("The exercise name is invalid");
                return;
            }
            for(ExcerciseData ed : lstEd)
            {
                if(ed.getId().equals(TrainerGlobal.GetSelectedExercise.getId()))
                {
                    if(!ed.getName().equals(acTextView.getText().toString()))
                    {
                        acTextView.setError("The exercise name is invalid");
                        return;
                    }
                    break;
                }
            }
            String TraineeName= TrainerGlobal.TraineeName;

            TraineeName = TraineeName.substring(TraineeName.indexOf('(')+1,TraineeName.indexOf(')'));
            final ExcerciseInput er = new ExcerciseInput();
            er.ExerciseName= Name;
            er.ExerciseDescID= ID;
            er.User=TraineeName;
            er.Unit1= txtUnit1.getText().toString();
            er.Unit2= txtUnit2.getText().toString();
            er.Unit3= txtUnit3.getText().toString();
            if(er.Unit3.equals(""))
                er.Unit3="0";

            int i=10;
            if(er.Unit1.trim().equals("") && !Unit1.equals(""))
            {
                txtUnit1.setError(Unit1 + " cannot be empty");
                return;
            }
            if(er.Unit2.trim().equals("") && !Unit2.equals(""))
            {
                txtUnit2.setError(Unit2 + " cannot be empty");
                return;
            }
            if(txtUnit3.getText().toString().trim().equals("") && !Unit3.equals(""))
            {
                txtUnit3.setError(Unit3 + " cannot be empty");
                return;
            }

            mToDoTable = mClient.getTable("ExcerciseInput",ExcerciseInput.class);
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        final ExcerciseInput entity = addItemInTable(er);
                        final ExerciseActivity eA = new ExerciseActivity();
                        eA.ExerciseName= er.ExerciseName;
                        eA.Id=er.Id;
                        eA.Unit1Name = Unit1;
                        eA.Unit1Value = er.Unit1;
                        eA.Unit2Name = Unit2;
                        eA.Unit2Value = er.Unit2;
                        eA.Unit3Name = Unit3;
                        eA.Unit3Value = er.Unit3;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                if(!entity.isComplete()){
//                                    mAdapter.add(entity);
//                                }
                                TA.insert(eA, 0);
                            }
                        });
                    } catch (final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                acTextView.setError(e.getMessage().toString());
                            }
                        });

                    }
                    return null;
                }
            };

            runAsyncTask(task);

//            try {
//                //List<ExcerciseInput> lr = mToDoTable.execute().get();
//                mToDoTable.insert(er).get();
//            }catch(ExecutionException err)
//            {
//                i=11;
//            }catch(InterruptedException err)
//            {
//                i=12;
////            }catch(MobileServiceException err)
////            {
////                i=13;
//            }finally {
//
//                i =14;
//            }

//            mClient.invokeApi("EnterExerciseActivity", er , String.class, new ApiOperationCallback<String>() {
//
//                @Override
//                public void onCompleted(String result,
//                                        Exception exception, ServiceFilterResponse response) {
//
//                    if (exception == null) {
//                        txtUnit1.setHint(Unit1);
//                        txtUnit2.setHint(Unit2);
//                        txtUnit3.setHint(Unit3);
//                    }
//                }
//
//            });
        }
    };

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }public ExcerciseInput addItemInTable(ExcerciseInput item) throws ExecutionException, InterruptedException {
        ExcerciseInput entity = mToDoTable.insert(item).get();
        return entity;
    }

    AdapterView.OnItemClickListener AutocompleteItemClickListner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            adapterView.getAdapter().getItem(i);
            btnExerciseSubmit.setEnabled(true);

            ID=TrainerGlobal.GetSelectedExercise.getId();
            Name= TrainerGlobal.GetSelectedExercise.getName();
             Unit1 = TrainerGlobal.GetSelectedExercise.getUnit1();
             Unit2 = TrainerGlobal.GetSelectedExercise.getUnit2();
             Unit3 = TrainerGlobal.GetSelectedExercise.getUnit3();
            if(Unit1.equals("")) {
                txtUnit1.setEnabled(false);
                txtUnit1.setHint("");
            }
                else {
                txtUnit1.setEnabled(true);
                txtUnit1.setHint(Unit1);
            }
            if(Unit2.equals("")) {
                txtUnit2.setEnabled(false);
                txtUnit2.setHint("");
            }else {
                txtUnit2.setEnabled(true);
                txtUnit2.setHint(Unit2);
            }
                if(Unit3.equals("")) {
                    txtUnit3.setEnabled(false);
                    txtUnit3.setText("");
                }else {
                    txtUnit3.setEnabled(true);
                    txtUnit3.setHint( Unit3);
                }
            //ID= ((ExcerciseData)((ListView) adapterView).getAdapter().getItem(i)).ExerciseID;
        }
    };

    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            authenticate();

        }
    };

    public void authenticate()
    {
        //txtExerciseId.setText(TrainerGlobal.TraineeName);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exercise_input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.logout)
        {

            SharedPreferences prefs = getSharedPreferences(TrainerGlobal.SHAREDPREFFILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(TrainerGlobal.USERIDPREF, "");
            editor.putString(TrainerGlobal.TOKENPREF, "");
            editor.commit();
            Intent loggedInIntent = new Intent(getApplicationContext(), Login.class);
            startActivity(loggedInIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void RecentExerciseActivities(){

        TA = new RecentActivityAdapter(this, R.layout.exercise_activity_layout, this.mClient, this.mToDoTable);
        ListView lstView = (ListView) findViewById(R.id.lstViewActivity);
        lstView.setAdapter(TA);
        String TraineeName= TrainerGlobal.TraineeName;
        TraineeName = TraineeName.substring(TraineeName.indexOf('(')+1,TraineeName.indexOf(')'));
        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        Pair<String, String> param = Pair.create("TraineeID",TraineeName);
        params.add(param);
        TA.clear();
        ListenableFuture<String> result = mClient.invokeApi("GetRecentActivity","GET", params , String.class );
        Futures.addCallback(result, new FutureCallback<String>() {
            @Override
            public void onFailure(Throwable exc) {
                Throwable exec1 = exc;
                // createAndShowDialog((Exception) exc, "Error");
            }

            @Override
            public void onSuccess(String result) {

                try {

                    JSONArray jarray = new JSONArray(result);

                    for(int i=0; i< jarray.length();i++)
                    {
                        JSONObject js = jarray.getJSONObject(i);
                        ExerciseActivity TAs = new ExerciseActivity();
                        TAs.ExerciseName = js.getString("ExerciseName");
                        TAs.Unit1Name = js.getString("Unit1Name");
                        TAs.Unit1Value= js.getString("Unit1Value");
                        TAs.Unit2Name = js.getString("Unit2Name");
                        TAs.Unit2Value= js.getString("Unit2Value");
                        TAs.Unit3Name = js.getString("Unit3Name");
                        TAs.Unit3Value= js.getString("Unit3Value");
                        TAs.ExerciseDescID= js.getString("ExerciseDescID");
                        TAs.Id=js.getString("Id");
                        TA.add(TAs);

                    }
                    //JSONObject jsonResponse = new JSONObject(result);

                } catch (Exception er) {

                }

            }
        });
    }

}
