package com.toetracker.fitnesstrainer;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by rajmarappan on 10/23/15.
 */
public class TrainerGlobal {

    public static String TraineeName ="";
    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static  AssetManager manager ;
    private static MobileServiceClient mClient;
    public static void setAssetManager(AssetManager mgr){manager= mgr;}
    public static AssetManager getAssetManager(){return manager;}
    public static List<ExerciseData> lstExercises = null;
    public static List<ExerciseData> getExercises(){return  lstExercises;}
    public static void setExercises(List<ExerciseData> Exercises){lstExercises=Exercises; }
    public static ExerciseData GetSelectedExercise;
    public static  MobileServiceClient getMobileServiceClient(){return mClient;}
    public static void setMobileServiceClient(MobileServiceClient mClientNew){mClient=mClientNew;}
    public static MobileServiceClient getMobileServiceClient(Context ctx, ServiceFilter pr, ServiceFilter rc){
        try {
            mClient = new MobileServiceClient(
                    "https://toetrackertrainermob.azure-mobile.net/",
                    "mfCRUZnFolnIKhTUjOqAeXYyfPZftG32",
                    ctx
            );
                    mClient.withFilter(pr).withFilter(rc);
        }catch (MalformedURLException err)
        {

        }
        return  mClient;

    }

    public static AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

}
