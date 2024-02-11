package com.ass2.i190417_i192048.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ass2.i190417_i192048.Models.Music;
import com.ass2.i190417_i192048.MusicApp.MusicMediaPlayer;
import com.ass2.i190417_i192048.MusicApp.PlayMusic;
import com.ass2.i190417_i192048.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    Context mContext;
    List<Music> musicList;

    public MusicAdapter(Context mContext, List<Music> musicList) {
        this.mContext = mContext;
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String musicName = musicList.get(position).getTitle();
        holder.musicTitle.setText(musicList.get(position).getTitle());
        Glide.with(mContext).load(musicList.get(position).getImageURL()).into(holder.musicImage);

        if (MusicMediaPlayer.currentIndex == position) {
            holder.musicTitle.setTextColor(mContext.getResources().getColor(R.color.yellow));
        } else {
            holder.musicTitle.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        holder.musicParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicMediaPlayer.getInstance().reset();
                MusicMediaPlayer.currentIndex = holder.getAdapterPosition();
                Intent intent = new Intent(mContext, PlayMusic.class);
                intent.putExtra("musicList", (Serializable)musicList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        holder.musicParentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = holder.getAdapterPosition();
                String title = musicList.get(pos).getTitle();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Music").document(title).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = task.getResult().getString("userID");
                        if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            musicList.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, musicList.size());
                            db.collection("Music").document(title).delete();
                        } else{
                            Toast.makeText(mContext, "You can not delete this Music", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView musicTitle;
        public ImageView musicImage;
        public LinearLayout musicParentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            musicTitle = itemView.findViewById(R.id.musicTitle);
            musicImage = itemView.findViewById(R.id.musicImage);
            musicParentLayout = itemView.findViewById(R.id.musicParentLayout);
        }
    }

}
