package com.ass2.i190417_i192048.ChatApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ass2.i190417_i192048.Adapters.MessageAdapter;
import com.ass2.i190417_i192048.Models.Messages;
import com.ass2.i190417_i192048.Models.Users;
import com.ass2.i190417_i192048.MusicApp.Signin;
import com.ass2.i190417_i192048.MusicApp.Signup;
import com.ass2.i190417_i192048.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.List;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatDetailActivity extends AppCompatActivity {

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    ImageView backButton, userImage, sendMsg;
    TextView userName, status;
    EditText messageToSend;
    FirebaseAuth mAuth;
    RecyclerView chatDetailRecyclerView;
    List<Messages> messagesList;
    MessageAdapter messageAdapter;
    String senderId1;
    String receiverId1;
    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
    ImageView selectImageButton;
    Uri imageURISelecting;
    String receiverId;
    String receiverName;
    String receiverImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        backButton = findViewById(R.id.backButton);
        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        sendMsg = findViewById(R.id.sendMsg);
        messageToSend = findViewById(R.id.messageToSend);
        status = findViewById(R.id.status);
        selectImageButton = findViewById(R.id.selectImageButton);





        mAuth = FirebaseAuth.getInstance();
        chatDetailRecyclerView = findViewById(R.id.chatDetailRecyclerView);
        messagesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messagesList, this);
        chatDetailRecyclerView.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatDetailRecyclerView.setLayoutManager(layoutManager);


        final String senderId = mAuth.getCurrentUser().getUid();
        receiverId = getIntent().getStringExtra("userID");
        receiverName = getIntent().getStringExtra("userName");
        receiverImage = getIntent().getStringExtra("profileURL");

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 55);
            }
        });

        userName.setText(receiverName);
        Glide.with(this).load(receiverImage).into(userImage);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String senderRoom = senderId + receiverId;
        String receiverRoom = receiverId + senderId;
        senderId1 = senderId;
        receiverId1 = receiverId;
        final String[] senderName = {""};
        final String[] senderImage = {""};

        db1.collection("Users").document(senderId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                senderName[0] = documentSnapshot.getString("name");
                senderImage[0] = documentSnapshot.getString("profileURL");

            }
        });

        // check for realtime updates on user status
        db.getReference().child("UsersStatus").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String status1 = snapshot.getValue(String.class);
                    status.setText(status1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db.getReference().child("UsersStatus").child(receiverId1).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    String status1 = task.getResult().getValue(String.class);
                    status.setText(status1);
                }
            }
        });



        db.getReference().child("Chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Messages messages = snapshot1.getValue(Messages.class);
                    messagesList.add(messages);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageToSend.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatDetailActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                    return;
                }
                Messages messages = new Messages(senderId, message);
                Date date = new Date();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm");
                String dateText = formatter.format(cal.getTime());
                messages.setTimestamp(dateText);
                messageToSend.setText("");


                messages.setSenderRoom(senderRoom);
                messages.setReceiverRoom(receiverRoom);
                String chatSendMsgIDStr = db.getReference().child("Chats").child(senderRoom).push().getKey();
                String chatReceiveMsgIDStr = db.getReference().child("Chats").child(receiverRoom).push().getKey();
                messages.setChatSendMsgID(chatSendMsgIDStr);
                messages.setChatReceiveMsgID(chatReceiveMsgIDStr);
                messages.setMsgType("Text");


                db.getReference().child("Chats").child(senderRoom).child(chatSendMsgIDStr).setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.getReference("Chats").child(receiverRoom).child(chatReceiveMsgIDStr).setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FirebaseFirestore.getInstance().collection("Users").document(receiverId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String token = documentSnapshot.getString("deviceID");
                                        try {
                                            OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+message+"'}, 'include_player_ids': ['" + token + "'], 'data': {'senderId': '"+senderId+"', 'senderName': '"+senderName[0]+"' , 'senderImage': '"+senderImage[0]+"' }}"),null);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.getReference().child("UsersStatus").child(receiverId1).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    String status1 = task.getResult().getValue(String.class);
                    status.setText(status1);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 55 && resultCode == RESULT_OK) {
            imageURISelecting = data.getData();
            ClipData mClipData = data.getClipData();
            if (imageURISelecting!=null){
                sendImage(imageURISelecting);
            } else {
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    sendImage(uri);
                }
            }
        }
    }

    public void sendImage(Uri val){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        // get timestamp in milliseconds
        long timestamp = System.currentTimeMillis();
        StorageReference imageRef = storageRef.child("images/" + timestamp);
        UploadTask uploadTask = imageRef.putFile(val);
        uploadTask.addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ChatDetailActivity.this, "Image upload failed", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadURL) {
                        final String senderId = mAuth.getCurrentUser().getUid();
                        String senderRoom = senderId + receiverId;
                        String receiverRoom = receiverId + senderId;
                        senderId1 = senderId;
                        receiverId1 = receiverId;
                        final String[] senderName = {""};
                        final String[] senderImage = {""};
                        String imageURLStr = downloadURL.toString();
                        Messages messages = new Messages(senderId, imageURLStr);
                        Date date = new Date();
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm");
                        String dateText = formatter.format(cal.getTime());
                        messages.setTimestamp(dateText);
                        messageToSend.setText("");
                        messages.setSenderRoom(senderRoom);
                        messages.setReceiverRoom(receiverRoom);
                        String chatSendMsgIDStr = db.getReference().child("Chats").child(senderRoom).push().getKey();
                        String chatReceiveMsgIDStr = db.getReference().child("Chats").child(receiverRoom).push().getKey();
                        messages.setChatSendMsgID(chatSendMsgIDStr);
                        messages.setChatReceiveMsgID(chatReceiveMsgIDStr);
                        messages.setMsgType("Image");
                        String message12 = "Image Received";
                        db.getReference().child("Chats").child(senderRoom).child(chatSendMsgIDStr).setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                db.getReference("Chats").child(receiverRoom).child(chatReceiveMsgIDStr).setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        FirebaseFirestore.getInstance().collection("Users").document(receiverId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                String token = documentSnapshot.getString("deviceID");
                                                try {
                                                    OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+message12+"'}, 'include_player_ids': ['" + token + "'], 'data': {'senderId': '"+senderId+"', 'senderName': '"+senderName[0]+"' , 'senderImage': '"+senderImage[0]+"' }}"),null);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });
            }
        });
    }
}
