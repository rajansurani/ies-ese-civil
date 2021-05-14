package com.upscrks.iesesecivil.Activity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.upscrks.iesesecivil.Database.Model.MCQ;
import com.upscrks.iesesecivil.Database.Model.MCQData;
import com.upscrks.iesesecivil.R;
import com.upscrks.iesesecivil.Utils.AdsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MCQActivity extends BaseActivity {

    @BindView(R.id.tvHeading)
    TextView tvHeading;
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

    @BindView(R.id.tvSubject)
    TextView tvSubject;

    @BindView(R.id.btnCheckAnswer)
    MaterialButton btnCheckAnswer;

    @BindView(R.id.btnNext)
    MaterialButton btnNext;

    @BindView(R.id.lottieAnimationView)
    LottieAnimationView animationView;

    @BindView(R.id.mcqSkeletonLayout)
    ShimmerFrameLayout mcqSkeletonLayout;

    @BindView(R.id.mcqLayout)
    LinearLayout mcqLayout;

    String subject, key;
    int selectedOption =0;
    List<MCQ> mMCQList = new ArrayList<>();
    ProgressDialog fetchMcqDialog;
    int currentMcqCount = 0;
    MCQ currentMcq;
    boolean previousYear = false, optionSelectionDisabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq);
        ButterKnife.bind(this);

        subject = getIntent().getStringExtra("subject");
        previousYear = getIntent().getBooleanExtra("previousYear", false);

        if (previousYear)
            tvHeading.setText("PREVIOUS YEAR QUESTION");
        key = subject;
        if (previousYear)
            key = key + "_previousYear";
        tvSubject.setText(subject);
        fetchMCQ();
    }

    private void fetchMCQ() {
        showMCQLoading();
        mDataAccess.getLastQuestion(key, lastCreatedOn -> {
            Map<String, Object> filters = new HashMap<>();
            filters.put("subject", subject);
            filters.put("prevYear", previousYear);
            mDataAccess.getMCQ(filters, "createdOn", 10,
                    lastCreatedOn,
                    Query.Direction.ASCENDING,
                    null, mcqs -> {
                        dismissMCQLoading();
                        if (mcqs.size() > 0) {
                            currentMcqCount = 0;
                            mMCQList = mcqs;
                            setupQuestion();
                        }
                        else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Practice Questions")
                            .setMessage("Looks like we are out of questions!");
                            builder.setPositiveButton("OK", (dialog,v)->{
                               dialog.dismiss();
                               finish();
                            });
                            builder.create().show();
                        }
                    });
        });
    }

    private void setupQuestion() {
        if (currentMcqCount == mMCQList.size() - 1) {
            fetchMCQ();
        } else {
            optionSelectionDisabled = false;
            clearSelection();
            currentMcq = mMCQList.get(currentMcqCount);
            tvQuestion.setText(currentMcq.getQuestion());
            tvOption1.setText(currentMcq.getOption1());
            tvOption2.setText(currentMcq.getOption2());
            tvOption3.setText(currentMcq.getOption3());
            tvOption4.setText(currentMcq.getOption4());
            showAd();
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
        if(!optionSelectionDisabled) {
            selectedOption = 1;
            selectOption(1);
        }
    }

    @OnClick(R.id.option2)
    public void OnClickOption2() {
        if(!optionSelectionDisabled) {
            selectedOption = 2;
            selectOption(2);
        }
    }

    @OnClick(R.id.option3)
    public void OnClickOption3() {
        if(!optionSelectionDisabled) {
            selectedOption = 3;
            selectOption(3);
        }
    }

    @OnClick(R.id.option4)
    public void OnClickOption4() {
        if(!optionSelectionDisabled) {
            selectedOption = 4;
            selectOption(4);
        }
    }

    @OnClick(R.id.btnNext)
    public void OnClickNext() {
        currentMcqCount++;
        setupQuestion();
        btnCheckAnswer.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);
        optionSelectionDisabled = false;
        selectedOption = 0;
    }

    @OnClick(R.id.btnCheckAnswer)
    public void OnClickCheckAnswer() {
        if(selectedOption == 0){
            Toast.makeText(this, "Please select any one Option", Toast.LENGTH_SHORT).show();
        }else {
            showCorrectAnswer();
            btnCheckAnswer.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            optionSelectionDisabled = true;
        }
    }

    private void showCorrectAnswer() {
        if(currentMcq !=null) {
            boolean isCorrect;
            clearSelection();
            int correctAnswer = currentMcq.getCorrectAnswer();
            if (selectedOption == correctAnswer)
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
                switch (selectedOption) {
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
            showAnswerAnimation(isCorrect);
            submitResponse(isCorrect);
        }
    }

    private void submitResponse(boolean isCorrect) {
        MCQData mcqData = new MCQData();
        mcqData.setQuestionId(currentMcq.getQuestionId());
        mcqData.setCorrect(isCorrect);
        mcqData.setAnsweredOn(new Timestamp(Calendar.getInstance().getTime()));
        mcqData.setSubject(subject);
        mcqData.setUserId(mAuth.getCurrentUser().getUid());

        mDataAccess.addMcqActivity(mcqData,key, currentMcq.getCreatedOn(), onComplete -> {
        });
    }

    private void showAnswerAnimation(boolean isCorrect) {
        if (isCorrect) {
            animationView.setAnimation(R.raw.correct_animation);
        } else {
            animationView.setAnimation(R.raw.wrong_animation);
        }
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void clearSelection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvOption1.setTextAppearance(R.style.FontThemeRegular);
            tvOption2.setTextAppearance(R.style.FontThemeRegular);
            tvOption3.setTextAppearance(R.style.FontThemeRegular);
            tvOption4.setTextAppearance(R.style.FontThemeRegular);
        }
        option1.setBackground(getResources().getDrawable(R.drawable.background_unselected));
        tvOpText1.setTextColor(getResources().getColor(R.color.grey));
        tvOption1.setTextColor(getResources().getColor(R.color.grey));

        option2.setBackground(getResources().getDrawable(R.drawable.background_unselected));
        tvOpText2.setTextColor(getResources().getColor(R.color.grey));
        tvOption2.setTextColor(getResources().getColor(R.color.grey));

        option3.setBackground(getResources().getDrawable(R.drawable.background_unselected));
        tvOpText3.setTextColor(getResources().getColor(R.color.grey));
        tvOption3.setTextColor(getResources().getColor(R.color.grey));

        option4.setBackground(getResources().getDrawable(R.drawable.background_unselected));
        tvOpText4.setTextColor(getResources().getColor(R.color.grey));
        tvOption4.setTextColor(getResources().getColor(R.color.grey));
    }

    @OnClick(R.id.back)
    public void OnClickBack(){
        finish();
    }

    private void showAd(){
        AdsUtils.loadBannerAd(MCQActivity.this);
    }
}