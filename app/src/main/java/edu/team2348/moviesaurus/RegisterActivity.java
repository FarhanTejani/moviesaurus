package edu.team2348.moviesaurus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;

/**
 * Activity that allows a user to register an account on Moviesaurus
 * @author Faizan Virani
 * @author Thomas Lilly
 * @version 1.0
 */
public class RegisterActivity extends AppCompatActivity {
//    private static String TAG = "RegisterActivity";

    private EditText email;
    private EditText password;
    private Intent intent;
    private AppCompatSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = (AppCompatSpinner) findViewById(R.id.major_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        String[] preList = getResources().getStringArray(R.array.major_list);
        Arrays.sort(preList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, preList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setPrompt("Select Major");

        email = (EditText) findViewById(R.id.registration_email);
        password = (EditText) findViewById(R.id.registration_password);
        Button signUp = (Button) findViewById(R.id.signup_button);
        intent = new Intent(this, MainActivity.class);

        signUp.setOnClickListener(new SignUpClickHandler());
    }

    private class SignUpClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ParseUser user = new ParseUser();
            user.setEmail(email.getText().toString());
            user.setUsername(email.getText().toString());
            user.setPassword(password.getText().toString());
            user.put("admin", false);
            user.put("banned", false);
            user.put("major", spinner.getSelectedItem());
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        startActivity(intent);
                    } else {
                        Log.e("ParseError", e.getMessage());
                        if (getCurrentFocus() != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                        Snackbar
                                .make(findViewById(R.id.register_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
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
