package com.ass2.i190417_i192048.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ass2.i190417_i192048.Adapters.UsersAdapter;
import com.ass2.i190417_i192048.Models.Users;
import com.ass2.i190417_i192048.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {


    public ChatsFragment() {

    }

    List<Users> list = new ArrayList<>();
    FirebaseFirestore db;
    UsersAdapter adapter;
    RecyclerView recyclerView;

    public void getData3(){
        db = FirebaseFirestore.getInstance();
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> contactsList = new ArrayList<>();
        db.collection("Users").document(currentUserID).collection("Contacts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (int i = 0; i < task.getResult().size(); i++) {
                    String contactID = task.getResult().getDocuments().get(i).getId();
                    contactsList.add(contactID);
                }
                if (contactsList.size()!=0)
                {
                    db.collection("Users").whereIn("userId", contactsList).get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            for (int i = 0; i < task2.getResult().size(); i++) {
                                list.add(task2.getResult().getDocuments().get(i).toObject(Users.class));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.chatsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UsersAdapter(list, getContext());
        recyclerView.setAdapter(adapter);

        // clear list
        list.clear();
        getData3();
        return view;
    }

}