package com.ass2.i190417_i192048.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ass2.i190417_i192048.Models.Comments;
import com.ass2.i190417_i192048.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context mContext;
    List<Comments> commentsList;

    public CommentAdapter(Context mContext, List<Comments> commentsList) {
        this.mContext = mContext;
        this.commentsList = commentsList;
    }

    void updateList(List<Comments> list) {
        this.commentsList.clear();
        this.commentsList.addAll(list);
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        int pos = position;
        holder.commentValue.setText(commentsList.get(pos).getCommentBody());
        holder.commentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // string array
                List<String> ids = new ArrayList<>();
                db.collection("Comments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ids.add(document.getId());
                            }
                            commentsList.remove(pos);
                            notifyDataSetChanged();

                            // delete comments with specific ids
                            for (int i = 0; i < ids.size(); i++) {
                                db.collection("Comments").document(ids.get(i)).delete();
                            }

                            // add the comments again
                            for (Comments comment : commentsList) {
                                db.collection("Comments").add(comment);
                            }

                        }
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView commentValue;
        public LinearLayout commentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            commentValue = (TextView) itemView.findViewById(R.id.commentValue);
            commentLayout = (LinearLayout) itemView.findViewById(R.id.commentLayout);
        }
    }


}
