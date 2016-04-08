package edu.team2348.moviesaurus;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;

/**
 * Class that allows user to view their current profile information and update their information
 * @author Faizan Virani
 * @author Thomas Lilly
 * @version 1.0
 */

public class UserProfileActivity extends AppCompatActivity {
    /**
     * The TAG to identify the this class to the Logger
     */
    private static final String TAG = "UserProfileActivity";
    /**
     * The Parse account currently logged into the app
     */
    private final ParseUser user = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Profile");
        }
        ((TextView) findViewById(R.id.user_profile_name)).setText(user.getUsername());

        findViewById(R.id.new_email_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setEmail(((EditText) findViewById(R.id.new_email_edit_text)).getText().toString());
                ((EditText) findViewById(R.id.new_email_edit_text)).setText("");
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e("Parse Error", e.getMessage());
                        } else {
                            saveSuccess();
                        }
                    }
                });
            }
        });

        findViewById(R.id.new_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setPassword(((EditText) findViewById(R.id.new_password_edit_text)).getText().toString());
                ((EditText) findViewById(R.id.new_password_edit_text)).setText("");
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, e.getMessage());
                        } else {
                            saveSuccess();
                        }
                    }
                });
            }
        });

        final AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.major_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final String[] preList = getResources().getStringArray(R.array.major_list);
        Arrays.sort(preList);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, preList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setPrompt("Select Major");
        findViewById(R.id.new_major_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.put("major", spinner.getSelectedItem());
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, e.getMessage());
                        } else {
                            saveSuccess();
                        }
                    }
                });
            }
        });

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCurrentFocus() != null) {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return true;
        }
        return false;
    }

    /**
     * Notifies user that the profile was saved successfully
     */
    private void saveSuccess() {
        Snackbar.make(findViewById(R.id.user_profile_activity_layout), "Changed Successfully", Snackbar.LENGTH_LONG).show();
    }

}
