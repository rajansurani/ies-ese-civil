package com.upscrks.iesesecivil.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.upscrks.iesesecivil.Activity.PDFViewerActivity;
import com.upscrks.iesesecivil.Database.Model.Notes;
import com.upscrks.iesesecivil.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.MyViewHolder>{

    List<Notes> mNotesList;
    Context mContext;

    public NotesListAdapter(List<Notes> notesList, Context context) {
        mNotesList = notesList;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_notes, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notes notes = mNotesList.get(position);
        holder.tvTitle.setText(notes.getTitle());
        holder.layout.setOnClickListener(v->{
            Intent intent = new Intent(mContext, PDFViewerActivity.class);
            intent.putExtra("pdfUrl",notes.getFileUrl());
            intent.putExtra("title",notes.getTitle());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mNotesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView tvTitle;

        public MyViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            layout = v.findViewById(R.id.layout);
        }

    }
}
