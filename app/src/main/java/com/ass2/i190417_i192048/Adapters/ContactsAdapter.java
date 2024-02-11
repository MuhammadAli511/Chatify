package com.ass2.i190417_i192048.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ass2.i190417_i192048.Models.Users;
import com.ass2.i190417_i192048.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder12> {
    List<Users> contactsList;
    Context context;

    public ContactsAdapter(List<Users> contactsList, Context context) {
        this.contactsList = contactsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder12 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contacts_user_row, parent, false);
        return new ViewHolder12(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder12 holder12, int position) {
        int pos = position;
        Users users = contactsList.get(position);
        holder12.userName1.setText(users.getName());
        Glide.with(context).load(contactsList.get(pos).getProfileURL()).into(holder12.userImage1);
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public class ViewHolder12 extends RecyclerView.ViewHolder {
        ImageView userImage1;
        TextView userName1;
        public ViewHolder12(@NonNull View itemView) {
            super(itemView);
            userImage1 = itemView.findViewById(R.id.userImage1);
            userName1 = itemView.findViewById(R.id.userName1);

        }
    }
}
