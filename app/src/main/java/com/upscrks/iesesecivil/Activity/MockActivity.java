package com.upscrks.iesesecivil.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.upscrks.iesesecivil.Adapter.MockTestListAdapter;
import com.upscrks.iesesecivil.Database.Model.MockTest;
import com.upscrks.iesesecivil.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MockActivity extends BaseActivity {

    @BindView(R.id.tvNoMockFound)
    TextView tvNoMockFound;

    @BindView(R.id.mockTestRecycler)
    RecyclerView mockTestRecycler;

    @BindView(R.id.tvMarksObtained)
    TextView tvMarksObtained;

    @BindView(R.id.tvTotalMarks)
    TextView tvTotalMarks;

    @BindView(R.id.tvTestTaken)
    TextView tvTestTaken;
    List<MockTest> mMockTests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupMockTests();
    }

    private void setupMockTests() {
        mDataAccess.fetchMockTests(mockTests -> {
            mMockTests = mockTests;
            mockTestRecycler.setLayoutManager(new LinearLayoutManager(this));
            mockTestRecycler.setAdapter(new MockTestListAdapter(mockTests, this));
            mockTestRecycler.setNestedScrollingEnabled(false);

            if (mockTests.isEmpty())
                tvNoMockFound.setVisibility(View.VISIBLE);
            else
                tvNoMockFound.setVisibility(View.GONE);

            setupHeaders();
        });
    }

    private void setupHeaders() {
        tvTestTaken.setText(""+mMockTests.size());
        Collections.sort(mMockTests, new Comparator<MockTest>() {
            @Override
            public int compare(MockTest mockTest, MockTest t1) {
                return Integer.compare(t1.getCorrectAnswers(), mockTest.getCorrectAnswers());
            }
        });
        if(!mMockTests.isEmpty()){
            tvMarksObtained.setText(mMockTests.get(0).getCorrectAnswers()+"");
            tvTotalMarks.setText(" out of "+mMockTests.get(0).getTotalQuestions());
        }
        else{
            tvMarksObtained.setText("0");
            tvTotalMarks.setText(" out of 30");
        }
    }

    @OnClick(R.id.btnStartNewMockTest)
    public void OnClickStartNewMockTest() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_mock_test_rules);
        dialog.findViewById(R.id.btnStart).setOnClickListener(view -> {
            Intent intent = new Intent(this, MockTestAttemptActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.ivClose).setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    @OnClick(R.id.back)
    public void OnClickBack(){
        finish();
    }
}