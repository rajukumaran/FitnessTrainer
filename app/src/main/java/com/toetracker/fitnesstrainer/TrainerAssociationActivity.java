package com.toetracker.fitnesstrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

//import com.toetracker.fitnesstrainer

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.*;

import java.net.MalformedURLException;

import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class TrainerAssociationActivity extends AzureBaseActivity {
    TextView txtView;
    EditText edtText;
    Button btnSubmit;
    String AuthString;
    String UserId;
    //MobileServiceClient mClient;
    public boolean bAuthenticating = false;
    public final Object mAuthenticationLock = new Object();
    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    MobileServiceTable<TrainerAssociation> taObj;
    TrainerAssociationAdapter TA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_association);
        //taObj = new ArrayList<TrainerAssociation>();
        try {
            mClient = new MobileServiceClient(
                    "https://toetrackertrainermob.azure-mobile.net/",
                    "mfCRUZnFolnIKhTUjOqAeXYyfPZftG32",this
            )
                    .withFilter(new ProgressFilterNew())
                    .withFilter(new RefreshTokenCacheFilterNew());
        }catch (MalformedURLException err)
        {

        }

        try {
            taObj = mClient.getTable(TrainerAssociation.class);
            TA = new TrainerAssociationAdapter(this, R.layout.trainer_association_layout, taObj);
            ListView lstView = (ListView) findViewById(R.id.listView);
            lstView.setAdapter(TA);

            refreshItemsFromTable();
        }catch (Exception err)
        {
            Integer i =10;
            i=11;

        }
    }

    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<TrainerAssociation> results = taObj.execute().get();
                    Integer i=10;
                    i=11;
                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TA.clear();

                            for (TrainerAssociation item : results) {
                                TA.add(item);
                            }
                        }
                    });
                } catch (final Exception e){

                    Integer i=10;
                    i=11;
                    //createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           // authenticate();

        }
    };
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trainer_association, menu);

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
            SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(USERIDPREF, "");
            editor.putString(TOKENPREF, "");
            editor.commit();
            Intent loggedInIntent = new Intent(getApplicationContext(), Login.class);
            startActivity(loggedInIntent);
        }
        return super.onOptionsItemSelected(item);
    }
//
//
//    private boolean loadUserTokenCache(MobileServiceClient client)
//    {
//        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
//        String userId = prefs.getString(USERIDPREF, "undefined");
//        if (userId == "undefined")
//            return false;
//        String token = prefs.getString(TOKENPREF, "undefined");
//        if (token == "undefined")
//            return false;
//
//        MobileServiceUser user = new MobileServiceUser(userId);
//        user.setAuthenticationToken(token);
//        client.setCurrentUser(user);
//
//        return true;
//    }
//
//    /**
//     * The RefreshTokenCacheFilterNew class filters responses for HTTP status code 401.
//     * When 401 is encountered, the filter calls the authenticate method on the
//     * UI thread. Out going requests and retries are blocked during authentication.
//     * Once authentication is complete, the token cache is updated and
//     * any blocked request will receive the X-ZUMO-AUTH header added or updated to
//     * that request.
//     */
//    private class RefreshTokenCacheFilterNew implements ServiceFilter {
//
//        AtomicBoolean mAtomicAuthenticatingFlag = new AtomicBoolean();
//
//        @Override
//        public ListenableFuture<ServiceFilterResponse> handleRequest(
//                final ServiceFilterRequest request,
//                final NextServiceFilterCallback nextServiceFilterCallback
//        )
//        {
//            // In this example, if authentication is already in progress we block the request
//            // until authentication is complete to avoid unnecessary authentications as
//            // a result of HTTP status code 401.
//            // If authentication was detected, add the token to the request.
//            //waitAndUpdateRequestToken(request);
//            SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
//            String userId = prefs.getString(USERIDPREF, "undefined");
//            String token = prefs.getString(TOKENPREF, "undefined");
//            //if(token!="undefined") {
//                MobileServiceUser user = new MobileServiceUser(userId);
//                user.setAuthenticationToken(token);
//                mClient.setCurrentUser(user);
//
//                request.removeHeader("X-ZUMO-AUTH");
//                request.addHeader("X-ZUMO-AUTH", token);
//            //}
//            // Send the request down the filter chain
//            // retrying up to 5 times on 401 response codes.
//            ListenableFuture<ServiceFilterResponse> future = null;
//            ServiceFilterResponse response = null;
//            int responseCode = 401;
//
//            future = nextServiceFilterCallback.onNext(request);
//            try {
//                response = future.get();
//                responseCode = response.getStatus().getStatusCode();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                if (e.getCause().getClass() == MobileServiceException.class)
//                {
//                    MobileServiceException mEx = (MobileServiceException) e.getCause();
//                    responseCode = mEx.getResponse().getStatus().getStatusCode();
//                    if (responseCode == 401)
//                    {
//                        Intent loggedInIntent = new Intent(getApplicationContext(), Login.class);
//                        startActivity(loggedInIntent);
//                    }
//                }
//            }
//
//            return future;
//        }
//    }
//
//    /**
//     * The ProgressFilterNew class renders a progress bar on the screen during the time the App is waiting for the response of a previous request.
//     * the filter shows the progress bar on the beginning of the request, and hides it when the response arrived.
//     */
//    private class ProgressFilterNew implements ServiceFilter {
//        @Override
//        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {
//
//            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();
//
//            runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    //   if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
//                }
//            });
//
//            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);
//
//            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
//                @Override
//                public void onFailure(Throwable e) {
//                    resultFuture.setException(e);
//                }
//
//                @Override
//                public void onSuccess(ServiceFilterResponse response) {
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            //  if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
//                        }
//                    });
//
//                    resultFuture.set(response);
//                }
//            });
//
//            return resultFuture;
//        }
//    }
//    public class LoginRequest
//    {
//        public String username;
//        public String password;
//    }


}
