package com.globallogic.lrds;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;
import java.util.Date;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        TextView mSignInButton = (TextView) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        TextView mRegisterButton = (TextView) findViewById(R.id.register_button);
        mRegisterButton.setText(Html.fromHtml(String.format(getString(R.string.register))));

        TextView mRecoveryButton = (TextView) findViewById(R.id.recovery_password_button);
        mRecoveryButton.setText(Html.fromHtml(String.format(getString(R.string.recovery_password))));
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        return true;
    }

    private boolean isPasswordValid(String password) {
        return password.trim().length() > 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Response> {
        private final int ERROR_CODE_SHOULD_WAIT = 1;
        private final Response RESPONSE_SUCCESSFUL_LOGIN = new Response(200, new Gson().fromJson("{'time':'" + new Date() + "', 'token': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9'}", JsonObject.class));
        private final Response RESPONSE_ERROR_SHOULD_WAIT = new Response(400, new Gson().fromJson("{'time':'" + new Date() + "', 'errorCode': " + ERROR_CODE_SHOULD_WAIT + "}", JsonObject.class));
        private final Response RESPONSE_ERROR_INVALID_CREDENTIALS = new Response(403);
        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        private final String[] DUMMY_CREDENTIALS = new String[]{
                "facu:globallogic",
                "jorge:globallogic",
                "ema:globallogic",
                "juan:globallogic",
                "ale:globallogic",
                "gaston:globallogic"
        };

        private final String userName;
        private final String password;

        UserLoginTask(String username, String password) {
            this.userName = username;
            this.password = password;
        }

        @Override
        protected Response doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // Nothing to do
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(userName)) {
                    // Account exists, return true if the password matches.
                    if (pieces[1].equals(password)) {
                        return RESPONSE_SUCCESSFUL_LOGIN;
                    } else {
                        return RESPONSE_ERROR_INVALID_CREDENTIALS;
                    }
                }
            }

            return RESPONSE_ERROR_SHOULD_WAIT;
        }

        @Override
        protected void onPostExecute(final Response response) {
            mAuthTask = null;
            showProgress(false);

            switch (response.getStatusCode()) {
                case HttpURLConnection.HTTP_OK:
                    Toast.makeText(LoginActivity.this, getString(R.string.welcome_user, response.getBody().get("token")), Toast.LENGTH_SHORT).show();
                    break;
                case HttpURLConnection.HTTP_FORBIDDEN:
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    break;
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    switch (response.getBody().get("errorCode").getAsInt()) {
                        case ERROR_CODE_SHOULD_WAIT:
                            Toast.makeText(LoginActivity.this, getString(R.string.error_you_should_wait), Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private class Response {
        Integer statusCode;
        JsonObject body;

        public Response(int statusCode) {
            this.statusCode = statusCode;
            this.body = new JsonObject();
        }

        public Response(int statusCode, JsonObject body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public JsonObject getBody() {
            return body;
        }

        public void setBody(JsonObject body) {
            this.body = body;
        }
    }
}