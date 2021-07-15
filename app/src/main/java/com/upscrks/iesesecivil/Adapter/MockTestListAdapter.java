package com.upscrks.iesesecivil.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.rpc.Help;
import com.upscrks.iesesecivil.Activity.MockTestResultActivity;
import com.upscrks.iesesecivil.Activity.MockTestReviewActivity;
import com.upscrks.iesesecivil.Application.Helper;
import com.upscrks.iesesecivil.Database.Model.MockTest;
import com.upscrks.iesesecivil.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MockTestListAdapter extends RecyclerView.Adapter<MockTestListAdapter.MyViewHolder>{

    List<MockTest> mMockTests;
    Context mContext;

    public MockTestListAdapter(List<MockTest> mockTests, Context context) {
        mMockTests = mockTests;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_mock_test, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MockTest test = mMockTests.get(position);
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        holder.tvDate.setText(dateFormat.format(test.getCreatedOn().toDate()));
        holder.tvTimeTaken.setText(test.getTotalTimeTaken()+" min");
        holder.tvMarks.setText(test.getCorrectAnswers()+" out of "+test.getTotalQuestions());
        holder.layout.setOnClickListener(v->{
            Intent intent = new Intent(mContext, MockTestReviewActivity.class);
            intent.putExtra("mockTestId",test.getMockTestId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mMockTests.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTimeTaken, tvMarks;
        LinearLayout layout;

        public MyViewHolder(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tvDate);
            tvTimeTaken = v.findViewById(R.id.tvTimeTaken);
            tvMarks = v.findViewById(R.id.tvMarks);
            layout = v.findViewById(R.id.layout);
        }

    }
}
