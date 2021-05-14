package com.upscrks.iesesecivil.Adapter;

import android.content.Context;
import android.graphics.fonts.FontStyle;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.upscrks.iesesecivil.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.MyViewHolder>{

    List<String> subjectList;
    String selectedSubject="";
    Context mContext;

    public SubjectListAdapter(List<String> subjectList, Context context) {
        this.subjectList = subjectList;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_subject, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String subject = subjectList.get(position);
        holder.tvSubject.setText(subject);

        if(subject.equals(selectedSubject)){
            holder.layout.setBackground(mContext.getDrawable(R.drawable.custom_background_green_10r));
            holder.ivTick.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvSubject.setTextAppearance(R.style.FontThemeBold);
            }
            holder.tvSubject.setTextColor(mContext.getResources().getColor(R.color.white));
        }else{
            holder.layout.setBackground(mContext.getDrawable(R.drawable.custom_background_white_10r_green_border));
            holder.ivTick.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvSubject.setTextAppearance(R.style.FontThemeSemiBold);
            }
            holder.tvSubject.setTextColor(mContext.getResources().getColor(R.color.green));
        }

        holder.layout.setOnClickListener(v->{
            setSelectedSubject(subject);
            notifyDataSetChanged();
        });
        if(subject.equals("Blank")) {
            holder.layout.setVisibility(View.GONE);
        }
        else{
            holder.layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public String getSelectedSubject() {
        return selectedSubject;
    }

    public void setSelectedSubject(String selectedSubject) {
        this.selectedSubject = selectedSubject;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView tvSubject;
        ImageView ivTick;

        public MyViewHolder(View v) {
            super(v);
            tvSubject = v.findViewById(R.id.tvSubject);
            ivTick = v.findViewById(R.id.ivTick);
            layout = v.findViewById(R.id.layout);
        }

    }
}
