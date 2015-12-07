package com.toetracker.fitnesstrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiOperationCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddExercise extends AzureBaseActivity {

    EditText txtNewExercise;
    Spinner spnBodyArea,spnMuscle,spnUnit1,spnUnit2,spnUnit3,spnSecondaryMuscle;
    List<String> BodyAreaLists;
    List<String> MuscleLists;
    List<String> MuscleListIds;
    List<String> SecondaryMuscleLists;
    String MuscleResults;
    ArrayAdapter<String> SecondaryMuscleAdapter;
    ArrayAdapter<String> MuscleAdapter;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapterUnits;
    LinearLayout linlaHeaderProgress;
    Button btnSubmitNewExercise,btnClearPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        btnClearPreference = (Button)findViewById(R.id.btnClearPreference);
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        btnSubmitNewExercise = (Button)findViewById(R.id.btnSubmitNewExercise);
        btnSubmitNewExercise.setOnClickListener(btnSubmitNewExercise_click);
        btnClearPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences(TrainerGlobal.SHAREDPREFFILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(TrainerGlobal.EXCERCISELIST, "");
                editor.commit();
            }
        });
        txtNewExercise = (EditText)findViewById(R.id.txtNewExercise);

        txtNewExercise.setHint("Exercise Name");
        spnBodyArea= (Spinner)findViewById(R.id.spnBodyArea);
        spnMuscle = (Spinner)findViewById(R.id.spnMuscle);
        spnSecondaryMuscle= (Spinner)findViewById(R.id.spnSecondaryMuscle);
        spnUnit1= (Spinner)findViewById(R.id.spnUnit1);
        spnUnit2= (Spinner)findViewById(R.id.spnUnit2);
        spnUnit3= (Spinner)findViewById(R.id.spnUnit3);
        String [] strUnits = {"Select Unit","Mins","LBs","KGs","Reps","Miles","KMs","Inclination"};
        adapterUnits = new ArrayAdapter<String>(this,R.layout.spinner_layout,strUnits);
        spnUnit1.setAdapter(adapterUnits);
        spnUnit2.setAdapter(adapterUnits);
        spnUnit3.setAdapter(adapterUnits);
        //TrainerGlobal.SetText(txtNewExercise,"Exercise Name");
        BodyAreaLists = new ArrayList<String>();
        MuscleLists = new ArrayList<String>();
        MuscleListIds = new ArrayList<String>();
        SecondaryMuscleLists = new ArrayList<String>();
        BodyAreaLists.add("Select Body Type");
        MuscleLists.add("Select Muscle Type");
        SecondaryMuscleLists.add("Select Secondary Muscle");
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, BodyAreaLists);
        spnBodyArea.setAdapter(adapter);

        MuscleAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, MuscleLists);
        spnMuscle.setAdapter(MuscleAdapter);
        SecondaryMuscleAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, SecondaryMuscleLists);
        spnSecondaryMuscle.setAdapter(SecondaryMuscleAdapter);

        linlaHeaderProgress.setVisibility(View.VISIBLE);


        ListenableFuture<String> result = mClient.invokeApi("GetMuscles","GET", null, String.class );
        Futures.addCallback(result, new FutureCallback<String>() {
            @Override
            public void onFailure(Throwable exc) {
                Throwable exec1 = exc;
                // createAndShowDialog((Exception) exc, "Error");
            }

            @Override
            public void onSuccess(String result) {
                MuscleResults = result;
                try {
                      JSONArray jarr = new JSONArray(MuscleResults);
                      for(int i=0; i<jarr.length();i++)
                      {
                          JSONObject jobj =  jarr.getJSONObject(i);
                          String strCurBody = jobj.getString("BodyAreaName");
                          if(!BodyAreaLists.contains(strCurBody))
                              BodyAreaLists.add(strCurBody);

                      }


//                    JSONObject jsonResponse = new JSONObject(result);
//                    Iterator<?> keys = jsonResponse.keys();
//                    BodyAreaLists.clear();
//                    BodyAreaLists.add("Select Body Type");
//                    while (keys.hasNext()) {
//                        String key = (String) keys.next();
//                        BodyAreaLists.add(key);
//                    }
                    linlaHeaderProgress.setVisibility(View.GONE);
                } catch (Exception er) {

                }

            }
        });

        spnBodyArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();
                MuscleLists.clear();
                MuscleListIds.clear();
                MuscleLists.add("Select Muscle Type");
                SecondaryMuscleLists.clear();
                SecondaryMuscleLists.add("Select Secondary Muscle");
                try {
                    //JSONObject jsonResponse = new JSONObject(MuscleResults);
                    String strBodyArea = spnBodyArea.getSelectedItem().toString();
                    JSONArray jarr = new JSONArray(MuscleResults);
                    for (int i = 0; i < jarr.length(); i++) {
                        JSONObject jobj = jarr.getJSONObject(i);
                        String strCurBody = jobj.getString("BodyAreaName");
                        if (strCurBody.equals(strBodyArea)) {
                            MuscleLists.add(jobj.getString("MuscleDescName"));
                            MuscleListIds.add(jobj.getString("MuscleDescID"));
                            SecondaryMuscleLists.add(jobj.getString("MuscleDescName"));
                        }

                    }
//                    String [] strMuscle = jsonResponse.get(strBodyArea).toString().split(",");
//                    for (String strVal: strMuscle
//                         ) {
//                        MuscleLists.add(strVal);
//                    }
                    spnMuscle.setSelection(0);

                } catch (Exception e) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });



    }


    View.OnClickListener btnSubmitNewExercise_click = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            if(txtNewExercise.getText().toString().trim().equals(""))
            {
                txtNewExercise.setError("Exercise Name cannot be empty");
                return;
            }
            if(spnBodyArea.getSelectedItemPosition()==0) {
                ((TextView) spnBodyArea.getSelectedView()).setError("None Selected");

                return ;
            }
            if(spnMuscle.getSelectedItemPosition()==0)
            {
                ((TextView) spnMuscle.getSelectedView()).setError("None Selected");
                return ;
            }
            if(spnUnit1.getSelectedItemPosition()==0)
            {
                ((TextView) spnUnit1.getSelectedView()).setError("None Selected");
                return ;
            }
            final String strExerciseName = txtNewExercise.getText().toString();
            final ExerciseDescRequest edr = new ExerciseDescRequest();
            edr.ExerciseName=  strExerciseName;
            edr.MuscleId= MuscleListIds.get(spnMuscle.getSelectedItemPosition()-1);
            edr.MuscleId= MuscleListIds.get(spnSecondaryMuscle.getSelectedItemPosition()-1);
            edr.Unit1=spnUnit1.getSelectedItem().toString();
            edr.Unit2=spnUnit2.getSelectedItem().toString();
            if(spnUnit3.getSelectedItemPosition()==0)
                edr.Unit3="";
            else
                edr.Unit3=spnUnit3.getSelectedItem().toString();

            mClient.invokeApi("AddExercise", edr, String.class, new ApiOperationCallback<String>() {

                @Override
                public void onCompleted(String result,
                                        Exception exception, ServiceFilterResponse response) {
                    if(exception==null) {
                        JSONObject jsnExercise = new JSONObject();
                        try {
                            jsnExercise.put("ExerciseName", edr.ExerciseName);
                            jsnExercise.put("ExerciseID", result);
                            jsnExercise.put("Unit1", edr.Unit1);
                            jsnExercise.put("Unit2", edr.Unit2);
                            jsnExercise.put("Unit3", edr.Unit3);
                            SharedPreferences prefs = getSharedPreferences(TrainerGlobal.SHAREDPREFFILE, Context.MODE_PRIVATE);
                            String line = prefs.getString(TrainerGlobal.EXCERCISELIST, "undefined");
                            if (line.equals("") || line.equals("undefined")) {
                                AssetManager mgr = TrainerGlobal.getAssetManager();
                                InputStream file = mgr.open("Exercise.json");
                                BufferedReader reader = new BufferedReader(new InputStreamReader(file));
                                line = reader.readLine();

                            }
                            JSONObject jsonResponse = new JSONObject(line);
                            JSONArray jsonArray = jsonResponse.getJSONArray("results");
                            jsonArray.put(jsnExercise);
                            JSONObject jsobj = new JSONObject();
                            jsonResponse.remove("results");
                            jsobj.put("results", jsonArray);
                            String strValue = jsobj.toString();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(TrainerGlobal.EXCERCISELIST, strValue);
                            editor.commit();
                            ExcerciseData ed = new ExcerciseData(result, edr.ExerciseName, edr.Unit1, edr.Unit2, edr.Unit3);
                            List<ExcerciseData> edLst = TrainerGlobal.getExercises();
                            if (edLst == null)
                                edLst = new ArrayList<ExcerciseData>();
                            edLst.add(ed);
                            TrainerGlobal.setExercises(edLst);
                            Intent loggedInIntent = new Intent(getApplicationContext(), ExerciseInputActivity.class);
                            startActivity(loggedInIntent);
                        } catch (JSONException err) {

                        } catch (IOException err) {
                        } catch (Exception err) {
                            int i = 10;
                            i = 11;
                        }
                    }else { txtNewExercise.setError(exception.getMessage().substring(1,exception.getMessage().length()-1));}

                }
            });



            int i =10;
            i=11;




        }
    };
    public class ExerciseDescRequest
    {
        public String ExerciseName;
        public String MuscleId ;
        public String SecondaryMuslceId ;
        public String Unit1 ;
        public String Unit2 ;
        public String Unit3 ;
    }
}


