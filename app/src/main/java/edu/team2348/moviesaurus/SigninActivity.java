package edu.team2348.moviesaurus;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class SigninActivity extends AppCompatActivity {

    Button signUpBtn;
    Button signInBtn;
    EditText email;
    EditText pass;
    Intent loggedInIntent;
    Intent signUpIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        signUpBtn = (Button) findViewById(R.id.register_button);
        signInBtn = (Button) findViewById(R.id.login_button);
        email = (EditText) findViewById(R.id.email_edit_text);
        pass = (EditText) findViewById(R.id.password_edit_text);
        loggedInIntent = new Intent(this, MainActivity.class);
        signUpIntent = new Intent(this, RegisterActivity.class);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground(
                        email.getText().toString(),
                        pass.getText().toString(),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    startActivity(loggedInIntent);
                                } else {
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
            }
        });

    }

}
