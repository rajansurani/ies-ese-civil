package com.upscrks.iesesecivil.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.upscrks.iesesecivil.Database.Model.MockData;
import com.upscrks.iesesecivil.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class QuestionNumberListAdapter extends RecyclerView.Adapter<QuestionNumberListAdapter.MyViewHolder>{

    int currentMcq=0;
    List<MockData> mMockData;
    Context mContext;
    boolean showAttemptedMark;
    OnQuestionClicked mOnQuestionClicked;

    public QuestionNumberListAdapter(List<MockData> mMockData, Context mContext, boolean showAttemptedMark, OnQuestionClicked mOnQuestionClicked) {
        this.mMockData = mMockData;
        this.mContext = mContext;
        this.showAttemptedMark = showAttemptedMark;
        this.mOnQuestionClicked = mOnQuestionClicked;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_question_number, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvQuestionNumber.setText(""+(position+1));
        if(currentMcq == position){
            holder.ivQuestionNumber.setBackground(mContext.getResources().getDrawable(R.drawable.custom_background_white_circle));
            holder.ivQuestionNumber.setImageTintList(mContext.getResources().getColorStateList(R.color.green));
            holder.tvQuestionNumber.setTextColor(mContext.getResources().getColor(R.color.green));
            holder.tvQuestionNumber.setBackground(mContext.getResources().getDrawable(R.drawable.custom_background_white_circle));
        }else{
            holder.ivQuestionNumber.setBackground(mContext.getResources().getDrawable(R.drawable.custom_background_white20_circle));
            holder.ivQuestionNumber.setImageTintList(mContext.getResources().getColorStateList(R.color.white50));
            holder.tvQuestionNumber.setTextColor(mContext.getResources().getColor(R.color.white50));
            holder.tvQuestionNumber.setBackground(mContext.getResources().getDrawable(R.drawable.custom_background_white20_circle));
        }
        if(position == (mMockData.size()-1))
            holder.viewLine.setVisibility(View.GONE);
        else
            holder.viewLine.setVisibility(View.VISIBLE);

        if(mMockData.get(position).getUserAnswer() !=0 && showAttemptedMark){
            holder.ivQuestionNumber.setVisibility(View.VISIBLE);
            holder.tvQuestionNumber.setVisibility(View.GONE);
        }else{
            holder.ivQuestionNumber.setVisibility(View.GONE);
            holder.tvQuestionNumber.setVisibility(View.VISIBLE);
        }
        holder.layout.setOnClickListener(v->{mOnQuestionClicked.OnClick(position);});
    }

    @Override
    public int getItemCount() {
        return mMockData.size();
    }

    public interface OnQuestionClicked{
        void OnClick(int questionNumber);
    }

    public int getCurrentMcq() {
        return currentMcq;
    }

    public void setCurrentMcq(int currentMcq) {
        this.currentMcq = currentMcq;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber;
        View viewLine;
        LinearLayout layout;
        ImageView ivQuestionNumber;

        public MyViewHolder(View v) {
            super(v);
            tvQuestionNumber = v.findViewById(R.id.tvQuestionNumber);
            viewLine = v.findViewById(R.id.viewLine);
            layout = v.findViewById(R.id.layout);
            ivQuestionNumber = v.findViewById(R.id.ivQuestionNumber);
        }

    }
}
