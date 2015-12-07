package com.toetracker.fitnesstrainer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Created by rajmarappan on 10/25/15.
 */
public class JsonParse {
    double current_latitude,current_longitude;
    public JsonParse(){}
    public JsonParse(double current_latitude,double current_longitude){
        this.current_latitude=current_latitude;
        this.current_longitude=current_longitude;
    }
    public List<ExcerciseData> getParseJsonWCF(String sName, Activity context)
    {
        List<ExcerciseData> ListData = new ArrayList<ExcerciseData>();
        try {
//            String temp=sName.replace(" ", "%20");
            //URL js = new URL("http://webheavens.com/suggestion.php?name="+temp);
            //URLConnection jc = js.openConnection();
            List<ExcerciseData> lstExercise= TrainerGlobal.getExercises();
            if(lstExercise==null)
            {
                lstExercise = new ArrayList<ExcerciseData>();
                SharedPreferences prefs = context.getSharedPreferences(TrainerGlobal.SHAREDPREFFILE, Context.MODE_PRIVATE);
                String line = prefs.getString(TrainerGlobal.EXCERCISELIST, "undefined");
                if(line.equals("")) {
                    AssetManager mgr = TrainerGlobal.getAssetManager();
                    InputStream file = mgr.open("Exercise.json");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(file));
                    line = reader.readLine();
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(TrainerGlobal.EXCERCISELIST, line);
                    editor.commit();
                }
                JSONObject jsonResponse = new JSONObject(line);
                JSONArray jsonArray = jsonResponse.getJSONArray("results");

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject r = jsonArray.getJSONObject(i);
                    lstExercise.add(new ExcerciseData(r.getString("ExerciseID"),r.getString("ExerciseName"),r.getString("Unit1"),r.getString("Unit2"),r.getString("Unit3")));
                }
                TrainerGlobal.setExercises(lstExercise);
            }
            for(ExcerciseData exData:lstExercise){
                if(exData.getName().toUpperCase().contains(sName.toUpperCase()))
                     ListData.add(exData);
            }
        } catch (Exception e1) {
            int i=10;
            i=11;

        }
        return ListData;

    }

}