package com.ass2.i190417_i192048.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ass2.i190417_i192048.Adapters.ContactsAdapter;
import com.ass2.i190417_i192048.Adapters.UsersAdapter;
import com.ass2.i190417_i192048.Models.Users;
import com.ass2.i190417_i192048.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    public ContactsFragment() {

    }

    List<Users> list = new ArrayList<>();
    FirebaseFirestore db;
    ContactsAdapter adapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView = view.findViewById(R.id.contactsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContactsAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
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
        return view;

    }

    @Override
    public void onResume() {
        list.clear();
        adapter.notifyDataSetChanged();
        super.onResume();
    }
}