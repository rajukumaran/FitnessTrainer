package com.toetracker.fitnesstrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rajmarappan on 11/3/15.
 */
public class AzureBaseActivity extends AppCompatActivity{
    public MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = TrainerGlobal.getMobileServiceClient();
        if(mClient==null)
        {
            try {
                mClient = new MobileServiceClient(
                        "https://toetrackertrainermob.azure-mobile.net/",
                        "mfCRUZnFolnIKhTUjOqAeXYyfPZftG32",this
                )
                        .withFilter(new ProgressFilterNew())
                        .withFilter(new RefreshTokenCacheFilterNew());
                TrainerGlobal.setMobileServiceClient(mClient);
            }catch (MalformedURLException err)
            {

            }
        }
    }
    /**
     * The RefreshTokenCacheFilterNew class filters responses for HTTP status code 401.
     * When 401 is encountered, the filter calls the authenticate method on the
     * UI thread. Out going requests and retries are blocked during authentication.
     * Once authentication is complete, the token cache is updated and
     * any blocked request will receive the X-ZUMO-AUTH header added or updated to
     * that request.
     */
    protected class RefreshTokenCacheFilterNew implements ServiceFilter {

        AtomicBoolean mAtomicAuthenticatingFlag = new AtomicBoolean();

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(
                final ServiceFilterRequest request,
                final NextServiceFilterCallback nextServiceFilterCallback
        )
        {
            // In this example, if authentication is already in progress we block the request
            // until authentication is complete to avoid unnecessary authentications as
            // a result of HTTP status code 401.
            // If authentication was detected, add the token to the request.
            //waitAndUpdateRequestToken(request);
            SharedPreferences prefs = getSharedPreferences(TrainerGlobal.SHAREDPREFFILE, Context.MODE_PRIVATE);
            String userId = prefs.getString(TrainerGlobal.USERIDPREF, "undefined");
            String token = prefs.getString(TrainerGlobal.TOKENPREF, "undefined");
            //if(token!="undefined") {
            MobileServiceUser user = new MobileServiceUser(userId);
            user.setAuthenticationToken(token);
            TrainerGlobal.getMobileServiceClient().setCurrentUser(user);

            request.removeHeader("X-ZUMO-AUTH");
            request.addHeader("X-ZUMO-AUTH", token);
            //}
            // Send the request down the filter chain
            // retrying up to 5 times on 401 response codes.
            ListenableFuture<ServiceFilterResponse> future = null;
            ServiceFilterResponse response = null;
            int responseCode = 401;

            future = nextServiceFilterCallback.onNext(request);
            try {
                response = future.get();
                responseCode = response.getStatus().getStatusCode();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                if (e.getCause().getClass() == MobileServiceException.class)
                {
                    MobileServiceException mEx = (MobileServiceException) e.getCause();
                    responseCode = mEx.getResponse().getStatus().getStatusCode();
                    if (responseCode == 401)
                    {
                        Intent loggedInIntent = new Intent(getApplicationContext(), Login.class);
                        startActivity(loggedInIntent);
                    }
                }
            }

            return future;
        }
    }

    /**
     * The ProgressFilterNew class renders a progress bar on the screen during the time the App is waiting for the response of a previous request.
     * the filter shows the progress bar on the beginning of the request, and hides it when the response arrived.
     */
    protected class ProgressFilterNew implements ServiceFilter {
        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //   if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //  if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }
    public class LoginRequest
    {
        public String username;
        public String password;
    }

}
