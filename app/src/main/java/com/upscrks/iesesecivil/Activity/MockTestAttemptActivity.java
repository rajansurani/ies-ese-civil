package com.upscrks.iesesecivil.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.upscrks.iesesecivil.Adapter.QuestionNumberListAdapter;
import com.upscrks.iesesecivil.Application.Helper;
import com.upscrks.iesesecivil.Database.Model.MCQ;
import com.upscrks.iesesecivil.Database.Model.MockData;
import com.upscrks.iesesecivil.Database.Model.MockTest;
import com.upscrks.iesesecivil.R;
import com.upscrks.iesesecivil.Utils.AdsUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MockTestAttemptActivity extends BaseActivity {

    @BindView(R.id.tvQuestion)
    TextView tvQuestion;

    @BindView(R.id.option1)
    LinearLayout option1;

    @BindView(R.id.tvOption1)
    TextView tvOption1;

    @BindView(R.id.tvOpText1)
    TextView tvOpText1;

    @BindView(R.id.option2)
    LinearLayout option2;

    @BindView(R.id.tvOption2)
    TextView tvOption2;

    @BindView(R.id.tvOpText2)
    TextView tvOpText2;

    @BindView(R.id.option3)
    LinearLayout option3;

    @BindView(R.id.tvOption3)
    TextView tvOption3;

    @BindView(R.id.tvOpText3)
    TextView tvOpText3;

    @BindView(R.id.option4)
    LinearLayout option4;

    @BindView(R.id.tvOption4)
    TextView tvOption4;

    @BindView(R.id.tvOpText4)
    TextView tvOpText4;

    @BindView(R.id.mcqSkeletonLayout)
    ShimmerFrameLayout mcqSkeletonLayout;

    @BindView(R.id.mcqLayout)
    LinearLayout mcqLayout;

    @BindView(R.id.tvTime)
    TextView tvTime;

    @BindView(R.id.timerProgress)
    ProgressBar timerProgress;

    @BindView(R.id.questionNumberRecycler)
    RecyclerView questionNumberRecycler;

    @BindView(R.id.btnSubmit)
    MaterialButton btnSubmit;

    int selectedOption = 0;
    List<MCQ> mMCQList = new ArrayList<>();
    int currentMcqCount = 0;
    boolean optionSelectionDisabled = false;
    MockTest mMockTest;
    MCQ currentMcq;
    int timeRemaining = 0;
    List<MockData> mMockData = new ArrayList<>();
    CountDownTimer timer;
    QuestionNumberListAdapter mQuestionNumberListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_test_attempt);
        ButterKnife.bind(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.transparent));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        showMCQLoading();
        if (Helper.IsNullOrEmpty(getIntent().getStringExtra("mockTestId"))) {
            createNewMockTest();
        }
        showAd();
    }

    private void createNewMockTest() {
        mDataAccess.createNewMockTest(30, mockTest -> {
            mMockTest = mockTest;
            mDataAccess.getMockQuestions(mMockTest, mcqs -> {
                mMCQList = mcqs;
                mMockData = new ArrayList<>();
                for (MCQ mcq : mMCQList) {
                    MockData data = new MockData();
                    data.setQuestionNumber(mcq.getQuestionNumber());
                    data.setMockId(mMockTest.getMockTestId());
                    data.setUserAnswer(0);
                    data.setCorrect(false);
                    mMockData.add(data);
                }
                dismissMCQLoading();
                setupQuestionNumberRecycler();
                currentMcqCount = 0;
                setupQuestion();
                startTimer();
            });
        });
    }

    @OnClick(R.id.btnPrevious)
    public void OnClickPrevious() {
        evaluateAnswer();
        if (currentMcqCount != 0)
            currentMcqCount--;
        mQuestionNumberListAdapter.setCurrentMcq(currentMcqCount);
        mQuestionNumberListAdapter.notifyDataSetChanged();
        setupQuestion();
    }

    @OnClick(R.id.btnSubmit)
    public void OnClickSubmit() {
        evaluateAnswer();

        if (currentMcqCount != mMockTest.getTotalQuestions() - 1) {
            currentMcqCount++;
            mQuestionNumberListAdapter.setCurrentMcq(currentMcqCount);
            mQuestionNumberListAdapter.notifyDataSetChanged();
            setupQuestion();
        } else {
            timer.cancel();
            int totalCorrect = 0, totalAnswered = 0;
            for (MockData data : mMockData) {
                if (data.isCorrect())
                    totalCorrect++;
                if (data.getUserAnswer() != 0)
                    totalAnswered++;
                mDataAccess.addMockTestActivity(data, onComplete -> {
                });
            }
            mMockTest.setFinishedOn(new Timestamp(new Date()));
            mMockTest.setTotalTimeTaken(mMockTest.getTotalTimeAllowed() - timeRemaining);
            mMockTest.setCorrectAnswers(totalCorrect);
            mMockTest.setTotalQuestionsAnswered(totalAnswered);

            mDataAccess.updateMockTest(mMockTest, onComplete -> {
            });
            Intent intent = new Intent(this, MockTestResultActivity.class);
            intent.putExtra("mockTestId", mMockTest.getMockTestId());
            intent.putExtra("marksObtained", mMockTest.getCorrectAnswers());
            intent.putExtra("totalMarks", mMockTest.getTotalQuestions());
            startActivity(intent);
            finish();
        }
    }

    private void evaluateAnswer() {
        if(!mMockData.isEmpty()) {
            mMockData.get(currentMcqCount).setUserAnswer(selectedOption);
            if (selectedOption == currentMcq.getCorrectAnswer())
                mMockData.get(currentMcqCount).setCorrect(true);
            else
                mMockData.get(currentMcqCount).setCorrect(false);
            mMockData.get(currentMcqCount).setAnsweredOn(new Timestamp(new Date()));
        }
    }

    private void setupQuestionNumberRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        questionNumberRecycler.setLayoutManager(layoutManager);
        mQuestionNumberListAdapter = new QuestionNumberListAdapter(mMockData, this, true, new QuestionNumberListAdapter.OnQuestionClicked() {
            @Override
            public void OnClick(int questionNumber) {
                evaluateAnswer();
                currentMcqCount = questionNumber;
                setupQuestion();
                mQuestionNumberListAdapter.setCurrentMcq(questionNumber);
                mQuestionNumberListAdapter.notifyDataSetChanged();
            }
        });
        questionNumberRecycler.setAdapter(mQuestionNumberListAdapter);
    }

    private void setupQuestion() {
        optionSelectionDisabled = false;
        clearSelection();
        currentMcq = mMCQList.get(currentMcqCount);
        tvQuestion.setText(currentMcq.getQuestion());
        tvOption1.setText(currentMcq.getOption1());
        tvOption2.setText(currentMcq.getOption2());
        tvOption3.setText(currentMcq.getOption3());
        tvOption4.setText(currentMcq.getOption4());
        selectOption(mMockData.get(currentMcqCount).getUserAnswer());
        selectedOption = mMockData.get(currentMcqCount).getUserAnswer();
        questionNumberRecycler.scrollToPosition(currentMcqCount);
        setupButtons();
    }

    private void showMCQLoading() {
        View view = LayoutInflater.from(this).inflate(R.layout.skeleton_layout_mcq, null);
        mcqSkeletonLayout.removeAllViews();
        mcqSkeletonLayout.addView(view);
        mcqSkeletonLayout.startShimmer();
        mcqSkeletonLayout.setVisibility(View.VISIBLE);
        mcqLayout.setVisibility(View.GONE);
    }

    private void dismissMCQLoading() {
        mcqSkeletonLayout.setVisibility(View.GONE);
        mcqSkeletonLayout.stopShimmer();
        mcqLayout.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        if (currentMcqCount == mMockTest.getTotalQuestions() - 1)
            btnSubmit.setText("Submit Answers");
        else
            btnSubmit.setText("Next Question");
    }

    private void startTimer() {
        timerProgress.setMax(100);
        timer = new CountDownTimer(mMockTest.getTotalTimeAllowed() * 60000, 1000) {
            @Override
            public void onTick(long l) {
                tvTime.setText(l / 60000 + " min " + (l % 60000) / 1000 + " sec left");
                timeRemaining = (int) (l / 60000);
                timerProgress.setProgress((int) (l * 100 / (mMockTest.getTotalTimeAllowed() * 60000)));
            }

            @Override
            public void onFinish() {
                if (!(isDestroyed() || isFinishing())) {
                    OnClickSubmit();
                }
            }
        };
        timer.start();
    }

    private void selectOption(int option) {
        clearSelection();
        switch (option) {
            case 1: {
                option1.setBackground(getResources().getDrawable(R.drawable.background_selected));
                tvOpText1.setTextColor(getResources().getColor(R.color.white));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tvOption1.setTextAppearance(R.style.FontThemeBold);
                }
                tvOption1.setTextColor(getResources().getColor(R.color.white));
            }
            break;
            case 2: {
                option2.setBackground(getResources().getDrawable(R.drawable.background_selected));
                tvOpText2.setTextColor(getResources().getColor(R.color.white));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tvOption2.setTextAppearance(R.style.FontThemeBold);
                }
                tvOption2.setTextColor(getResources().getColor(R.color.white));
            }
            break;
            case 3: {
                option3.setBackground(getResources().getDrawable(R.drawable.background_selected));
                tvOpText3.setTextColor(getResources().getColor(R.color.white));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tvOption3.setTextAppearance(R.style.FontThemeBold);
                }
                tvOption3.setTextColor(getResources().getColor(R.color.white));
            }
            break;
            case 4: {
                option4.setBackground(getResources().getDrawable(R.drawable.background_selected));
                tvOpText4.setTextColor(getResources().getColor(R.color.white));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tvOption4.setTextAppearance(R.style.FontThemeBold);
                }
                tvOption4.setTextColor(getResources().getColor(R.color.white));
            }
            break;
        }
    }

    @OnClick(R.id.option1)
    public void OnClickOption1() {
        if (!optionSelectionDisabled) {
            selectedOption = 1;
            selectOption(1);
        }
    }

    @OnClick(R.id.option2)
    public void OnClickOption2() {
        if (!optionSelectionDisabled) {
            selectedOption = 2;
            selectOption(2);
        }
    }

    @OnClick(R.id.option3)
    public void OnClickOption3() {
        if (!optionSelectionDisabled) {
            selectedOption = 3;
            selectOption(3);
        }
    }

    @OnClick(R.id.option4)
    public void OnClickOption4() {
        if (!optionSelectionDisabled) {
            selectedOption = 4;
            selectOption(4);
        }
    }

    private void clearSelection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvOption1.setTextAppearance(R.style.FontThemeRegular);
            tvOption2.setTextAppearance(R.style.FontThemeRegular);
            tvOption3.setTextAppearance(R.style.FontThemeRegular);
            tvOption4.setTextAppearance(R.style.FontThemeRegular);
        }
        option1.setBackground(getResources().getDrawable(R.drawable.background_unselected));
        tvOpText1.setTextColor(getResources().getColor(R.color.darkGrey));
        tvOption1.setTextColor(getResources().getColor(R.color.darkGrey));

        option2.setBackground(getResources().getDrawable(R.drawable.background_unselected));
        tvOpText2.setTextColor(getResources().getColor(R.color.darkGrey));
        tvOption2.setTextColor(getResources().getColor(R.color.darkGrey));

        option3.setBackground(getResources().getDrawable(R.drawable.background_unselected));
        tvOpText3.setTextColor(getResources().getColor(R.color.darkGrey));
        tvOption3.setTextColor(getResources().getColor(R.color.darkGrey));

        option4.setBackground(getResources().getDrawable(R.drawable.background_unselected));
        tvOpText4.setTextColor(getResources().getColor(R.color.darkGrey));
        tvOption4.setTextColor(getResources().getColor(R.color.darkGrey));
    }

    @OnClick(R.id.back)
    public void OnClickBack() {
        OnClickSubmit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OnClickSubmit();
    }

    private void showAd() {
        if (mFirebaseRemoteConfig.getBoolean("displayAds"))
            AdsUtils.loadBannerAd(MockTestAttemptActivity.this);
    }
}