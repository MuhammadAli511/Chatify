package com.ass2.i190417_i192048.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ass2.i190417_i192048.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewContactFragment extends Fragment {


    public NewContactFragment() {
        // Required empty public constructor
    }


    EditText phoneNum;
    Button addContact;
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        phoneNum = view.findViewById(R.id.phoneNumOne);
        addContact = view.findViewById(R.id.addContact);
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid(); // person adding id
        db = FirebaseFirestore.getInstance();

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumStr = phoneNum.getText().toString(); // person to be added
                db.collection("Users").whereEqualTo("phoneNum", phoneNumStr).get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                String newContact = task.getResult().getDocuments().get(0).getId(); // person to be added ID
                                Map<String, String> map = new HashMap<>();
                                map.put("ContactID", newContact);
                                db.collection("Users").document(currentUser).collection("Contacts").document(newContact).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, String> map2 = new HashMap<>();
                                            map2.put("ContactID", currentUser);
                                            db.collection("Users").document(newContact).collection("Contacts").document(currentUser).set(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        phoneNum.setText("");
                                                        Toast.makeText(getContext(), "Contact Added", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(getContext(), "No user found with this phone number", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });




            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_contact, container, false);
    }
}