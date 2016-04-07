package edu.team2348.moviesaurus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.LinkedList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private List<String> emailList;
    private List<ParseUser> userList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ban Users");

//        recyclerView = (RecyclerView) findViewById(R.id.admin_user_list);
        userList = new LinkedList<>();
        emailList = new LinkedList<>();
        listView = (ListView) findViewById(R.id.admin_user_listview);
        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("email", ParseUser.getCurrentUser().getEmail());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    userList = objects;
                    for (final ParseUser user : objects) {
                        emailList.add(user.getEmail());
                    }
                    setAdapter();
                }
            }
        });
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ParseUser temp = userList.get(position);
                Log.d("ParseUser", temp.getEmail());
                Log.d("ParseUser", String.valueOf(temp.getBoolean("banned")));
                final boolean banned = temp.getBoolean("banned");
                temp.put("banned", !banned);
                temp.saveInBackground();
            }
        });
    }

    /**
     * Method to set the ListView's data adapter
     */
    private void setAdapter() {
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emailList));
    }
}
