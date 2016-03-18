package edu.team2348.moviesaurus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.LinkedList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

//    RecyclerView recyclerView;
    List<String> emailList;
    List<ParseUser> userList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ban Users");

//        recyclerView = (RecyclerView) findViewById(R.id.admin_user_list);
        userList = new LinkedList<>();
        emailList = new LinkedList<>();
        listView = (ListView) findViewById(R.id.admin_user_listview);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("email", ParseUser.getCurrentUser().getEmail());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    userList = objects;
                    for (ParseUser user : objects) {
                        emailList.add(user.getEmail().toString());
                    }
                    setAdapter();
                }
            }
        });
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseUser temp = userList.get(position);
                Log.d("ParseUser", temp.getEmail());
                Log.d("ParseUser", String.valueOf(temp.getBoolean("banned")));
                boolean banned = temp.getBoolean("banned");
                temp.put("banned", !banned);
                temp.saveInBackground();
            }
        });
    }

    private void setAdapter() {
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emailList));
    }
}
