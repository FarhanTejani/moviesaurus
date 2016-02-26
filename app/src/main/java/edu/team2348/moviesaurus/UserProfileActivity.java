package edu.team2348.moviesaurus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Class that allows user to view their current profile information and update their information
 * @author Faizan Virani
 * @version 1.0
 */

public class UserProfileActivity extends AppCompatActivity {

    ParseUser user = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                user.setPassword(((EditText)findViewById(R.id.new_password_edit_text)).getText().toString());
                ((EditText) findViewById(R.id.new_password_edit_text)).setText("");
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

    }


    private void saveSuccess() {
        Snackbar.make(findViewById(R.id.user_profile_activity_layout), "Changed Successfully", Snackbar.LENGTH_LONG).show();
    }

}
