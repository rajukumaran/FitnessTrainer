package com.toetracker.fitnesstrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by rajmarappan on 10/23/15.
 */
public class TrainerGlobal {

    public static String TraineeName = "";
    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static AssetManager manager;
    private static MobileServiceClient mClient;

    public static void setAssetManager(AssetManager mgr) {
        manager = mgr;
    }

    public static AssetManager getAssetManager() {
        return manager;
    }

    public static List<ExerciseData> lstExercises = null;

    public static List<ExerciseData> getExercises() {
        return lstExercises;
    }

    public static void setExercises(List<ExerciseData> Exercises) {
        lstExercises = Exercises;
    }

    public static ExerciseData GetSelectedExercise;

    public static MobileServiceClient getMobileServiceClient() {
        return mClient;
    }

    public static void setMobileServiceClient(MobileServiceClient mClientNew) {
        mClient = mClientNew;
    }

    public static MobileServiceClient getMobileServiceClient(Context ctx, ServiceFilter pr, ServiceFilter rc) {
        try {
            mClient = new MobileServiceClient(
                    "https://toetrackertrainermob.azure-mobile.net/",
                    "mfCRUZnFolnIKhTUjOqAeXYyfPZftG32",
                    ctx
            );
            mClient.withFilter(pr).withFilter(rc);
        } catch (MalformedURLException err) {

        }
        return mClient;

    }

    public static AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    public static void SetText(final EditText txtEmail, final String DefaultText) {

        txtEmail.setText(DefaultText);
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b==true)
                {
                    if(txtEmail.getText().toString().equals(DefaultText))
                    {
                        txtEmail.setText("");
                    }
                }else
                {
                    if(txtEmail.getText().toString().equals(""))
                        txtEmail.setText(DefaultText);
                }
            }
        });

    }

    public static void Logout(AzureBaseActivity activity){

        SharedPreferences prefs = activity.getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USERIDPREF, "");
        editor.putString(TOKENPREF, "");
        editor.commit();
        Intent loggedInIntent = new Intent(activity.getApplicationContext(), Login.class);
        activity.startActivity(loggedInIntent);
    }
}