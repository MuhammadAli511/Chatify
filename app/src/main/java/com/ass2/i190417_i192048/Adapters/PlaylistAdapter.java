package com.ass2.i190417_i192048.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ass2.i190417_i192048.Models.Playlists;
import com.ass2.i190417_i192048.R;
import com.ass2.i190417_i192048.MusicApp.SpecificPlaylist;
import com.bumptech.glide.Glide;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context mContext;
    List<Playlists> playlistsList;

    public PlaylistAdapter(Context mContext, List<Playlists> playlistsList) {
        this.mContext = mContext;
        this.playlistsList = playlistsList;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        int pos = position;
        holder.playlistName.setText(playlistsList.get(pos).getPlaylistName());
        Glide.with(mContext).load(playlistsList.get(pos).getImageURL()).into(holder.playlistImage);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SpecificPlaylist.class);
                intent.putExtra("playlistName", playlistsList.get(pos).getPlaylistName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView playlistName;
        public ImageView playlistImage;
        public LinearLayout parentLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            playlistName = (TextView) itemView.findViewById(R.id.playListName);
            playlistImage = (ImageView) itemView.findViewById(R.id.playListImage);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
        }
    }
}