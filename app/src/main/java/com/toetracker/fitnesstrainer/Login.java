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

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.net.MalformedURLException;

public class Login extends AzureBaseActivity {

    EditText txtEmail;
    EditText txtPassword;
    Button btnSubmit;
    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    MobileServiceClient mClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //tableLayout.SetBackgroundResource(Resource.Drawable.cool_gradient);
        txtEmail = (EditText)findViewById(R.id.txtUserName);
        txtPassword=(EditText)findViewById(R.id.txtPassword);
        txtEmail.setHint("User Name");
        txtPassword.setHint("Password");
        TrainerGlobal.SetText(txtEmail, "User Name");
        TrainerGlobal.SetText(txtPassword, "Password");
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(loginClickListener);
        try {
            mClient = new MobileServiceClient(
                    "https://toetrackertrainermob.azure-mobile.net/",
                    "mfCRUZnFolnIKhTUjOqAeXYyfPZftG32",
                    this
            );
        }catch (MalformedURLException err)
        {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            authenticate();

        }
    };
    private void authenticate() {


        // New login using the provider and update the token cache.
        LoginRequest req = new LoginRequest();
        req.username= txtEmail.getText().toString();
        req.password= txtPassword.getText().toString();
        if(req.username.equals(""))
            txtEmail.setError("User Name cannot be empty");
        if(req.password.equals(""))
            txtPassword.setError("Password cannot be empty");

        // New login using the provider and update the token cache.
        mClient.invokeApi("CustomLogin", req, JsonObject.class, new ApiOperationCallback<JsonObject>() {

            @Override
            public void onCompleted(JsonObject result,
                                    Exception exception, ServiceFilterResponse response) {

                if (exception == null) {

                    String AuthString = result.getAsJsonPrimitive("mobileServiceAuthenticationToken").getAsString();
                    String UserId = result.getAsJsonPrimitive("userId").getAsString();//result.get("userId").toString();
                    String UserType = result.getAsJsonPrimitive("userType").getAsString();;
                    MobileServiceUser user = new MobileServiceUser(UserId);
                    user.setAuthenticationToken(AuthString);
                    mClient.setCurrentUser(user);
                    SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(USERIDPREF, UserId);
                    editor.putString(TOKENPREF, AuthString);
                    editor.commit();
                    mClient .withFilter(new ProgressFilterNew())
                            .withFilter(new RefreshTokenCacheFilterNew());
                    TrainerGlobal.setMobileServiceClient(mClient);
                    if(UserType.toUpperCase().equals("TRAINER")) {
                        Intent loggedInIntent = new Intent(getApplicationContext(), TrainerAssociationActivity.class);
                        startActivity(loggedInIntent);
                    }else
                     {
                         Intent loggedInIntent = new Intent(getApplicationContext(), ExerciseInputActivity.class);
                         startActivity(loggedInIntent);
                     }
                }else
                {
                    if(result == null)
                    {
                        txtPassword.setError(exception.getMessage().substring(1,exception.getMessage().length()-1));
                        return;
                    }
                }
            }
        });
    }



    class LoginRequest
    {
        public String username;
        public String password;
    }
}
