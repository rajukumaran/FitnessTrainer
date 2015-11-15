package com.toetracker.fitnesstrainer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiOperationCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddExercise extends AzureBaseActivity {

    EditText txtNewExercise;
    Spinner spnBodyArea,spnMuscle,spnUnit1,spnUnit2,spnUnit3;
    List<String> items;
    List<String> MuscleLists;
    String MuscleResults;
    ArrayAdapter<String> MuscleAdapter;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapterUnits;
    LinearLayout linlaHeaderProgress;
    Button btnSubmitNewExercise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        btnSubmitNewExercise = (Button)findViewById(R.id.btnSubmitNewExercise);
        btnSubmitNewExercise.setOnClickListener(btnSubmitNewExercise_click);
        txtNewExercise = (EditText)findViewById(R.id.txtNewExercise);
        spnBodyArea= (Spinner)findViewById(R.id.spnBodyArea);
        spnMuscle = (Spinner)findViewById(R.id.spnMuscle);
        spnUnit1= (Spinner)findViewById(R.id.spnUnit1);
        spnUnit2= (Spinner)findViewById(R.id.spnUnit2);
        spnUnit3= (Spinner)findViewById(R.id.spnUnit3);
        String [] strUnits = {"Mins","LBs","KGs","Reps","Miles","KMs"};
        adapterUnits = new ArrayAdapter<String>(this,R.layout.spinner_layout,strUnits);
        spnUnit1.setAdapter(adapterUnits);
        spnUnit2.setAdapter(adapterUnits);
        spnUnit3.setAdapter(adapterUnits);
        TrainerGlobal.SetText(txtNewExercise,"Exercise Name");
        items = new ArrayList<String>();
        MuscleLists = new ArrayList<String>();
        items.add("Select Body Type");
        MuscleLists.add("Select Muscle Type");
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, items);
        spnBodyArea.setAdapter(adapter);

         MuscleAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, MuscleLists);
        spnMuscle.setAdapter(MuscleAdapter);

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
                    JSONObject jsonResponse = new JSONObject(result);
                    Iterator<?> keys = jsonResponse.keys();
                    items.clear();
                    items.add("Select Body Type");
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        items.add(key);
                   }
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
                MuscleLists.add("Select Muscle Type");
                try {
                    JSONObject jsonResponse = new JSONObject(MuscleResults);
                    String strBodyArea = items.get(index);
                    String [] strMuscle = jsonResponse.get(strBodyArea).toString().split(",");
                    for (String strVal: strMuscle
                         ) {
                        MuscleLists.add(strVal);
                    }
                    spnMuscle.setSelection(0);

                }catch (Exception e)
                {}
          }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }


    View.OnClickListener btnSubmitNewExercise_click = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

        }
    };
}
