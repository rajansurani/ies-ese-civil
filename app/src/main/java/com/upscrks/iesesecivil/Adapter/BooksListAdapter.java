package com.upscrks.iesesecivil.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.upscrks.iesesecivil.Database.Model.Books;
import com.upscrks.iesesecivil.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BooksListAdapter extends RecyclerView.Adapter<BooksListAdapter.MyViewHolder>{

    List<Books> mBooks;
    Context mContext;

    public BooksListAdapter(List<Books> books, Context context) {
        mBooks = books;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_books, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Books book = mBooks.get(position);
        holder.tvAuthor.setText(book.getBookAuthor());
        holder.tvBookTitle.setText(book.getBookTitle());
        holder.layout.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(book.getLink()));
            mContext.startActivity(intent);
        });
        Glide.with(mContext).load(book.getImageUrl()).into(holder.ivBook);
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView tvBookTitle, tvAuthor;
        ImageView ivBook;

        public MyViewHolder(View v) {
            super(v);
            tvBookTitle = v.findViewById(R.id.tvBookName);
            tvAuthor = v.findViewById(R.id.tvAuthor);
            layout = v.findViewById(R.id.layout);
            ivBook = v.findViewById(R.id.ivBook);
        }

    }
}
