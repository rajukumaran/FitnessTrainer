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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

//import com.toetracker.fitnesstrainer

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.gms.drive.query.SortOrder;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.*;

import java.net.MalformedURLException;

import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import org.apache.http.impl.conn.LoggingSessionOutputBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class TrainerAssociationActivity extends AzureBaseActivity {

    Button btnNewTrainee,btnAssociateUser;
    EditText txtTraineeID;
    MobileServiceTable<TrainerAssociation> mToDoTable;
    MobileServiceTable<TrainerAssociation> taObj;
    TrainerAssociationAdapter TA;
    LinearLayout linlaHeaderProgress;
    View.OnClickListener btnAssociateUser_Click = new View.OnClickListener(){
         @Override
         public void onClick(View view) {
             if(txtTraineeID.getText().toString().trim().equals(""))
             {
                 txtTraineeID.setError("Trainee ID Cannot be empty");
                 return;

             }
             // Create a new item
             mToDoTable = mClient.getTable(TrainerAssociation.class);
             final TrainerAssociation item = new TrainerAssociation();
            item.TraineeID = txtTraineeID.getText().toString();
             // Insert the new item
             AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                 @Override
                 protected Void doInBackground(Void... params) {
                     try {
                         final TrainerAssociation entity = mToDoTable.insert(item).get();
                         if(item.mId.equals("-1"))
                             runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {
                                     txtTraineeID.setError(entity.TraineeID);
                                 }
                             });
                         else
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                     TA.add(entity);
                             }
                         });
                     } catch (final Exception e) {
                         String strMessage = e.getMessage();

                     }
                     return null;
                 }
             };
             runAsyncTask(task);
         }
     };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_association);

        btnNewTrainee = (Button)findViewById(R.id.btnAddUser);
        btnAssociateUser = (Button)findViewById(R.id.btnAssociateUser);
        btnAssociateUser.setOnClickListener(btnAssociateUser_Click);
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        txtTraineeID = (EditText)findViewById(R.id.txtTraineeID);
        txtTraineeID.setHint("Enter Trainee ID");
        //TrainerGlobal.SetText(txtTraineeID,"Trainee ID");
        btnNewTrainee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loggedInIntent = new Intent(getApplicationContext(), AddTrainee.class);
                startActivity(loggedInIntent);
            }
        });

        linlaHeaderProgress.setVisibility(View.VISIBLE);
        try {
            taObj = mClient.getTable(TrainerAssociation.class);
            //MobileServiceList<TrainerAssociation> taObj1 = taObj.orderBy("TraineeID",QueryOrder.Ascending).execute().get();
            TA = new TrainerAssociationAdapter(this, R.layout.trainer_association_layout);
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

                mClient.invokeApi("GetTrainerAssociation", String.class, new ApiOperationCallback<String>() {


                    @Override
                    public void onCompleted(String result,
                                            Exception exception, ServiceFilterResponse response) {

                        if (exception == null) {
                            try {
                                JSONArray jarray = new JSONArray(result);
                                for(int i=0; i< jarray.length();i++)
                               {
                                   JSONObject js = jarray.getJSONObject(i);
                                   String FirstName = js.getString("FirstName");
                                   String LastName = js.getString("LastName");
                                   String UserName = js.getString("Username");
                                   String FullName = FirstName +" " + LastName + "(" + UserName +")";
                                   TrainerAssociation TAs = new TrainerAssociation();
                                   TAs.TraineeID=FullName;
                                   TA.add(TAs);

                               }

                                linlaHeaderProgress.setVisibility(View.GONE);

                                //.getJSONArray("results");
                                //String AuthString = result.getAsJsonPrimitive("mobileServiceAuthenticationToken").getAsString();
                            }catch(Exception err)
                            {
                                int i =0;
                                i=1;

                            }

                        }
                    }

                });



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
            TrainerGlobal.Logout(this);
        }
        return super.onOptionsItemSelected(item);
    }



}
