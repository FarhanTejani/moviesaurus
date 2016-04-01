package edu.team2348.moviesaurus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by faizanv on 3/18/16.
 */
public class UserRecyclerViewAdapter extends RecyclerView.Adapter {

    List<String> users;

    public UserRecyclerViewAdapter(List<String> users) {
        this.users = users;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView email;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            email = (TextView) itemView.findViewById(R.id.admin_page_user_email);
        }
    }
}
