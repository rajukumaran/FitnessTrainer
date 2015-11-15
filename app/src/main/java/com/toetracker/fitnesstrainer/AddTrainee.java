package com.toetracker.fitnesstrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiOperationCallback;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

public class AddTrainee extends AzureBaseActivity {

    Button btnAddTrainee;
    EditText txtUserName,txtFirstName, txtLastName,txtPhone,txtEmail;
    MobileServiceTable<TrainerAssociation> mToDoTable;
    Trainee tr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trainee);
        btnAddTrainee = (Button)findViewById(R.id.btnAddTrainee);
        btnAddTrainee.setOnClickListener(btnAddTrainee_Click);
        txtUserName= (EditText)findViewById(R.id.txtUserName);
        txtFirstName= (EditText)findViewById(R.id.txtFirstName);
        txtLastName = (EditText)findViewById(R.id.txtLastName);
        txtPhone= (EditText)findViewById(R.id.txtPhone);
        txtEmail= (EditText)findViewById(R.id.txtEmail);
        txtEmail.setHint("Email Address");
        txtFirstName.setHint("First Name");
        txtLastName.setHint("Last Name");
        txtUserName.setHint("UserName");
        txtPhone.setHint("Phone");

    }

    View.OnClickListener btnAddTrainee_Click = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            tr = new Trainee();
            tr.username=txtUserName.getText().toString();
            tr.Address="";
            tr.FirstName=txtFirstName.getText().toString();
            tr.LastName=txtLastName.getText().toString();
            tr.Phone=txtPhone.getText().toString();
            tr.Email=txtEmail.getText().toString();
            tr.Trainer=false;
            tr.password="Trainee123";
            mClient.invokeApi("CustomRegistration", tr, String.class, new ApiOperationCallback<String>() {

                @Override
                public void onCompleted(String result,
                                        Exception exception, ServiceFilterResponse response) {

                    if (exception == null) {

                        mToDoTable = mClient.getTable(TrainerAssociation.class);
                        final TrainerAssociation item = new TrainerAssociation();
                        item.TraineeID=tr.username;

                        // Insert the new item
                        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                            @Override
                            protected Void doInBackground(Void... params) {
                                try {

                                    final TrainerAssociation entity = mToDoTable.insert(item).get();

                                } catch (final Exception e) {

                                }
                                return null;
                            }
                        };

                        TrainerGlobal.runAsyncTask(task);

                           Intent loggedInIntent = new Intent(getApplicationContext(), TrainerAssociationActivity.class);
                            startActivity(loggedInIntent);

                    }
                }
            });

        }
    };


}
