package com.toetracker.fitnesstrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ExerciseInputActivity extends AppCompatActivity {

    Button btnExerciseSubmit;
    EditText txtExerciseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_input);
        btnExerciseSubmit = (Button)findViewById(R.id.btnExceriseSubmit);
        txtExerciseId = (EditText)findViewById(R.id.txtExerciseId);
        btnExerciseSubmit.setOnClickListener(loginClickListener);


    }

    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            authenticate();

        }
    };

    public void authenticate()
    {
        txtExerciseId.setText(TrainerGlobal.TraineeName);
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
