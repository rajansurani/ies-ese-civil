package com.upscrks.iesesecivil.Activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.upscrks.iesesecivil.Adapter.QuestionNumberListAdapter;
import com.upscrks.iesesecivil.Application.Helper;
import com.upscrks.iesesecivil.Database.Model.MCQ;
import com.upscrks.iesesecivil.Database.Model.MockData;
import com.upscrks.iesesecivil.Database.Model.MockTest;
import com.upscrks.iesesecivil.R;
import com.upscrks.iesesecivil.Utils.AdsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MockTestReviewActivity extends BaseActivity {

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

    @BindView(R.id.tvMarks)
    TextView tvMarks;

    @BindView(R.id.questionNumberRecycler)
    RecyclerView questionNumberRecycler;

    @BindView(R.id.ivNext)
    ImageView ivNext;

    @BindView(R.id.ivPrev)
    ImageView ivPrev;

    @BindView(R.id.btnNext)
    LinearLayout btnNext;

    @BindView(R.id.btnPrevious)
    LinearLayout btnPrevious;

    @BindView(R.id.tvNotAnswered)
    TextView tvNotAnswered;

    List<MCQ> mMCQList = new ArrayList<>();
    int currentMcqCount = 0;
    MockTest mMockTest;
    MCQ currentMcq;
    List<MockData> mMockData = new ArrayList<>();
    QuestionNumberListAdapter mQuestionNumberListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_test_review);
        ButterKnife.bind(this);

        ButterKnife.bind(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.transparent));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        showMCQLoading();
        if (!Helper.IsNullOrEmpty(getIntent().getStringExtra("mockTestId"))) {
            fetchMockTest();
            showAd();
        } else {
            Toast.makeText(this, "Mock Test Answers Not Found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchMockTest() {
        Log.d("Mock Test", "fetchMockTest: Fetching MockTest");
        mDataAccess.fetchMockTestById(getIntent().getStringExtra("mockTestId"), mockTest -> {
            mMockTest = mockTest;
            Log.d("Mock Test", "fetchMockTest: Fetching MockMcq");
            mDataAccess.getMockQuestions(mMockTest, mcqs -> {
                mMCQList = mcqs;
                Collections.sort(mMCQList, new Comparator<MCQ>() {
                    @Override
                    public int compare(MCQ o1, MCQ o2) {
                        return Integer.compare(o1.getQuestionNumber(), o2.getQuestionNumber());
                    }
                });
                Log.d("Mock Test", "fetchMockTest: Fetching MockDat");
                mDataAccess.getMockData(mMockTest, mockData -> {
                    Collections.sort(mMockData, new Comparator<MockData>() {
                        @Override
                        public int compare(MockData o1, MockData o2) {
                            return Integer.compare(o1.getQuestionNumber(), o2.getQuestionNumber());
                        }
                    });
                    mMockData = mockData;
                    tvMarks.setText(mMockTest.getCorrectAnswers() + "/" + mMockTest.getTotalQuestions());
                    dismissMCQLoading();
                    setupQuestionNumberRecycler();
                    currentMcqCount = 0;
                    setupQuestion();
                });
            });
        });
    }

    @OnClick(R.id.btnNext)
    public void OnClickNext() {
        if (currentMcqCount != mMockTest.getTotalQuestions() - 1) {
            currentMcqCount++;
            mQuestionNumberListAdapter.setCurrentMcq(currentMcqCount);
            mQuestionNumberListAdapter.notifyDataSetChanged();
            setupQuestion();
        }
    }

    @OnClick(R.id.btnPrevious)
    public void OnClickPrevious() {
        if (currentMcqCount != 0) {
            currentMcqCount--;
            mQuestionNumberListAdapter.setCurrentMcq(currentMcqCount);
            mQuestionNumberListAdapter.notifyDataSetChanged();
            setupQuestion();
        }
    }

    private void setupQuestionNumberRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        questionNumberRecycler.setLayoutManager(layoutManager);
        mQuestionNumberListAdapter = new QuestionNumberListAdapter(mMockData, this, false, new QuestionNumberListAdapter.OnQuestionClicked() {
            @Override
            public void OnClick(int questionNumber) {
                currentMcqCount = questionNumber;
                setupQuestion();
                mQuestionNumberListAdapter.setCurrentMcq(questionNumber);
                mQuestionNumberListAdapter.notifyDataSetChanged();
            }
        });
        questionNumberRecycler.setAdapter(mQuestionNumberListAdapter);
    }

    private void setupQuestion() {
        clearSelection();
        currentMcq = mMCQList.get(currentMcqCount);
        tvQuestion.setText(currentMcq.getQuestion());
        tvOption1.setText(currentMcq.getOption1());
        tvOption2.setText(currentMcq.getOption2());
        tvOption3.setText(currentMcq.getOption3());
        tvOption4.setText(currentMcq.getOption4());
        showCorrectAnswer();
        questionNumberRecycler.scrollToPosition(currentMcqCount);
        setupNextPrevButtons();
    }

    private void setupNextPrevButtons() {
        if (currentMcqCount == 0) {
            ivPrev.setImageTintList(getResources().getColorStateList(R.color.blue20));
            btnPrevious.setBackground(getDrawable(R.drawable.custom_background_white_circle));
        } else {
            ivPrev.setImageTintList(getResources().getColorStateList(R.color.blue));
            btnPrevious.setBackground(getDrawable(R.drawable.custom_background_white_75r_light_grey_border));
        }

        if (currentMcqCount == mMockTest.getTotalQuestions() - 1) {
            ivNext.setImageTintList(getResources().getColorStateList(R.color.blue20));
            btnNext.setBackground(getDrawable(R.drawable.custom_background_white_circle));
        } else {
            ivNext.setImageTintList(getResources().getColorStateList(R.color.blue));
            btnNext.setBackground(getDrawable(R.drawable.custom_background_white_75r_light_grey_border));
        }

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

    private void showCorrectAnswer() {
        if (currentMcq != null) {
            MockData mockData = Helper.filter(mMockData, i -> i.getQuestionNumber() == currentMcq.getQuestionNumber()).get(0);
            boolean isCorrect;
            clearSelection();
            int correctAnswer = currentMcq.getCorrectAnswer();
            if (mockData.getUserAnswer() == correctAnswer)
                isCorrect = true;
            else
                isCorrect = false;
            switch (correctAnswer) {
                case 1: {
                    option1.setBackground(getResources().getDrawable(R.drawable.background_correct));
                    tvOpText1.setTextColor(getResources().getColor(R.color.darkGreen));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tvOption1.setTextAppearance(R.style.FontThemeBold);
                    }
                    tvOption1.setTextColor(getResources().getColor(R.color.darkGreen));
                }
                break;
                case 2: {
                    option2.setBackground(getResources().getDrawable(R.drawable.background_correct));
                    tvOpText2.setTextColor(getResources().getColor(R.color.darkGreen));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tvOption2.setTextAppearance(R.style.FontThemeBold);
                    }
                    tvOption2.setTextColor(getResources().getColor(R.color.darkGreen));
                }
                break;
                case 3: {
                    option3.setBackground(getResources().getDrawable(R.drawable.background_correct));
                    tvOpText3.setTextColor(getResources().getColor(R.color.darkGreen));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tvOption3.setTextAppearance(R.style.FontThemeBold);
                    }
                    tvOption3.setTextColor(getResources().getColor(R.color.darkGreen));
                }
                break;
                case 4: {
                    option4.setBackground(getResources().getDrawable(R.drawable.background_correct));
                    tvOpText4.setTextColor(getResources().getColor(R.color.darkGreen));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tvOption4.setTextAppearance(R.style.FontThemeBold);
                    }
                    tvOption4.setTextColor(getResources().getColor(R.color.darkGreen));
                }
                break;
            }
            if (!isCorrect) {
                switch (mockData.getUserAnswer()) {
                    case 0:
                        tvNotAnswered.setVisibility(View.VISIBLE);
                        break;
                    case 1: {
                        option1.setBackground(getResources().getDrawable(R.drawable.background_wrong));
                        tvOpText1.setTextColor(getResources().getColor(R.color.red));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            tvOption1.setTextAppearance(R.style.FontThemeBold);
                        }
                        tvOption1.setTextColor(getResources().getColor(R.color.red));
                    }
                    break;
                    case 2: {
                        option2.setBackground(getResources().getDrawable(R.drawable.background_wrong));
                        tvOpText2.setTextColor(getResources().getColor(R.color.red));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            tvOption2.setTextAppearance(R.style.FontThemeBold);
                        }
                        tvOption2.setTextColor(getResources().getColor(R.color.red));
                    }
                    break;
                    case 3: {
                        option3.setBackground(getResources().getDrawable(R.drawable.background_wrong));
                        tvOpText3.setTextColor(getResources().getColor(R.color.red));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            tvOption3.setTextAppearance(R.style.FontThemeBold);
                        }
                        tvOption3.setTextColor(getResources().getColor(R.color.red));
                    }
                    break;
                    case 4: {
                        option4.setBackground(getResources().getDrawable(R.drawable.background_wrong));
                        tvOpText4.setTextColor(getResources().getColor(R.color.red));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            tvOption4.setTextAppearance(R.style.FontThemeBold);
                        }
                        tvOption4.setTextColor(getResources().getColor(R.color.red));
                    }
                    break;
                }
            }
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

        tvNotAnswered.setVisibility(View.GONE);
    }

    @OnClick(R.id.back)
    public void OnClickBack() {
        finish();
    }

    private void showAd() {
        AdsUtils.loadBannerAd(MockTestReviewActivity.this);
    }
}