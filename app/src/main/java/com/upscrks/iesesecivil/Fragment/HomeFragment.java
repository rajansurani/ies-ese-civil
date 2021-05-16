package com.upscrks.iesesecivil.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upscrks.iesesecivil.Activity.SubjectActivity;
import com.upscrks.iesesecivil.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.subjectCard)
    public void OnClickSubjectCard(){
        startActivity(new Intent(getContext(), SubjectActivity.class));
        mDataAccess.logEvent("NAVIGATION","Home to Subjects");
    }

    @OnClick(R.id.previousYearCard)
    public void OnClickPreviousYearCard(){
        Intent intent = new Intent(getContext(), SubjectActivity.class);
        intent.putExtra("previousYear",true);
        startActivity(intent);
        mDataAccess.logEvent("NAVIGATION","Home to Previous Year");
    }
}