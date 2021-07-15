package com.upscrks.iesesecivil.Activity;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pawelkleczkowski.customgauge.CustomGauge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.upscrks.iesesecivil.Database.Model.MockTest;
import com.upscrks.iesesecivil.R;

public class MockTestResultActivity extends BaseActivity {

    @BindView(R.id.tvMarksObtained)
    TextView tvMarksObtained;

    @BindView(R.id.tvTotalMarks)
    TextView tvTotalMarks;

    @BindView(R.id.gaugeMarks)
    CustomGauge marksGauge;

    String mockTestId;
    int marksObtained=0, totalMarks=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_test_result);
        ButterKnife.bind(this);

        mockTestId = getIntent().getStringExtra("mockTestId");
        marksObtained = getIntent().getIntExtra("marksObtained",0);
        totalMarks = getIntent().getIntExtra("totalMarks",0);
        setupMarks();
    }

    private void setupMarks(){
        marksGauge.setEndValue(totalMarks);
        marksGauge.setValue(marksObtained);
        tvMarksObtained.setText(""+marksObtained);
        tvTotalMarks.setText("out of "+totalMarks);
    }

    @OnClick(R.id.ivClose)
    public void OnClickClose(){
        finish();
    }

    @OnClick(R.id.btnReviewAnswers)
    public void OnCLickReviewAnswers(){
        Intent intent = new Intent(this, MockTestReviewActivity.class);
        intent.putExtra("mockTestId", mockTestId);
        startActivity(intent);
    }
}