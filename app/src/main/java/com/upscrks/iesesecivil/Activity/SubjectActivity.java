package com.upscrks.iesesecivil.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.upscrks.iesesecivil.Adapter.SubjectListAdapter;
import com.upscrks.iesesecivil.Application.Helper;
import com.upscrks.iesesecivil.R;

import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubjectActivity extends BaseActivity {

    @BindView(R.id.subjectRecycler)
    RecyclerView subjectRecycler;

    SubjectListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        ButterKnife.bind(this);

        setupSubjects();
    }

    private void setupSubjects() {
        mDataAccess.getSubjects(getIntent().getBooleanExtra("previousYear",false),subjects->{
            subjects.add("Blank");
            adapter = new SubjectListAdapter(subjects, this);
            subjectRecycler.setLayoutManager(new LinearLayoutManager(this));
            subjectRecycler.setAdapter(adapter);
        });
    }

    @OnClick(R.id.btnPractice)
    public void OnClickPractice() {
        if(adapter != null) {
            String selectedSubject = adapter.getSelectedSubject();
            if (Helper.IsNullOrEmpty(selectedSubject)) {
                Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, MCQActivity.class);
                intent.putExtra("subject", selectedSubject);
                intent.putExtra("previousYear", getIntent().getBooleanExtra("previousYear", false));
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.back)
    public void OnClickBack() {
        finish();
    }
}