package com.toetracker.fitnesstrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ExerciseInputActivity extends AzureBaseActivity {

    Button btnExerciseSubmit,btnAddExercise;
    EditText txtExerciseId;
    EditText txtUnit1,txtUnit2,txtUnit3;
    String Unit1,Unit2,Unit3;
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_input);

        btnExerciseSubmit = (Button)findViewById(R.id.btnExceriseSubmit);
        btnAddExercise= (Button)findViewById(R.id.btnAddExercise);
        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loggedInIntent = new Intent(getApplicationContext(), AddExercise.class);
                startActivity(loggedInIntent);
            }
        });
        //txtExerciseId = (EditText)findViewById(R.id.txtExerciseId);
        btnExerciseSubmit.setOnClickListener(loginClickListener);
        txtUnit1= (EditText)findViewById(R.id.txtUnit1);
        txtUnit2= (EditText)findViewById(R.id.txtUnit2);
        txtUnit3= (EditText)findViewById(R.id.txtUnit3);
        AssetManager am = getAssets();
        TrainerGlobal.setAssetManager(am);
        //AssetManager manager = getAssets();
        AutoCompleteTextView acTextView = (AutoCompleteTextView) findViewById(R.id.autoComplete);
        acTextView.setAdapter(new SuggestionAdapter(this,acTextView.getText().toString()));
        acTextView.setOnItemClickListener(AutocompleteItemClickListner);
        txtUnit1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (txtUnit1.getText().toString().equals(Unit1))
                        txtUnit1.setText("");
                } else {
                    if (txtUnit1.getText().toString().equals(""))
                        txtUnit1.setText(Unit1);
                }
            }
        });
        txtUnit2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    if (txtUnit2.getText().toString().equals(Unit2))
                        txtUnit2.setText("");
                }
                else{
                    if(txtUnit2.getText().toString().equals(""))
                        txtUnit2.setText(Unit2);
                }
            }
        });
        txtUnit3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    if (txtUnit3.getText().toString().equals(Unit3))
                        txtUnit3.setText("");
                }
                else{
                    if(txtUnit3.getText().toString().equals(""))
                        txtUnit3.setText(Unit3);
                }
            }
        });
        //ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.select_dialog_item,strValues);
        //acTextView.setThreshold(1);
        //acTextView.setAdapter(adapter);

    }

    AdapterView.OnItemClickListener AutocompleteItemClickListner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            adapterView.getAdapter().getItem(i);
            ID=TrainerGlobal.GetSelectedExercise.getId();
             Unit1 = TrainerGlobal.GetSelectedExercise.getUnit1();
             Unit2 = TrainerGlobal.GetSelectedExercise.getUnit2();
             Unit3 = TrainerGlobal.GetSelectedExercise.getUnit3();
            if(Unit1.equals("")) {
                txtUnit1.setEnabled(false);
                txtUnit1.setText("");
            }
                else {
                txtUnit1.setEnabled(true);
                txtUnit1.setText(Unit1);
            }
            if(Unit2.equals("")) {
                txtUnit2.setEnabled(false);
                txtUnit2.setText("");
            }else {
                txtUnit2.setText(Unit2);
                txtUnit2.setEnabled(true);
            }
                if(Unit3.equals("")) {
                    txtUnit3.setEnabled(false);
                    txtUnit3.setText("");
                }else {
                    txtUnit3.setText(Unit3);
                    txtUnit3.setEnabled(true);
                }
            //ID= ((ExerciseData)((ListView) adapterView).getAdapter().getItem(i)).ExerciseID;
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

}
