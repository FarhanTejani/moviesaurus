package edu.team2348.moviesaurus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Activity that allows a user to sign into the app
 * @author Fazian Virani
 * @version 1.0
 */
public class SigninActivity extends AppCompatActivity {
    private static final String TAG = "SigninActivity";

    private Button signUpBtn;
    private Button signInBtn;
    private TextInputEditText email;
    private TextInputEditText pass;
    private Intent loggedInIntent;
    private Intent signUpIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        signUpBtn = (Button) findViewById(R.id.register_button);
        signInBtn = (Button) findViewById(R.id.login_button);
        email = (TextInputEditText) findViewById(R.id.email_edit_text);
        pass = (TextInputEditText) findViewById(R.id.password_edit_text);
        loggedInIntent = new Intent(this, MainActivity.class);
        signUpIntent = new Intent(this, RegisterActivity.class);
        ParseUser userPersist = ParseUser.getCurrentUser();
        if (userPersist != null) {
            startActivity(loggedInIntent);
            finish();
        }

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "No persistent user");
                ParseUser.logInInBackground(
                        email.getText().toString(),
                        pass.getText().toString(),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    if (user.getBoolean("banned")) {
                                        if (getCurrentFocus() != null) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                        }
                                        Snackbar.make(findViewById(R.id.signin_layout), "Sorry you're banned", Snackbar.LENGTH_LONG).show();
                                        user.logOutInBackground();
                                    } else {
                                        startActivity(loggedInIntent);
                                        finish();
                                    }
                                } else {
                                    if (getCurrentFocus() != null) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                    }
                                    Snackbar.make(findViewById(R.id.signin_layout), "Login Failed!", Snackbar.LENGTH_LONG).show();
                                    Log.e("ParseError", e.getMessage());
                                }
                            }
                        }
                );

            }
        });


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signUpIntent);
                finish();
            }
        });

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return true;
        }
        return false;
    }

}
