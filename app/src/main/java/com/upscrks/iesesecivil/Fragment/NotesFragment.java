package com.upscrks.iesesecivil.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.upscrks.iesesecivil.Adapter.NotesListAdapter;
import com.upscrks.iesesecivil.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends BaseFragment {

    @BindView(R.id.shimmer)
    ShimmerFrameLayout mShimmerFrameLayout;

    @BindView(R.id.notesRecycler)
    RecyclerView notesRecycler;

    public NotesFragment() {
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
        View view =inflater.inflate(R.layout.fragment_notes, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupNotesList();
    }

    private void setupNotesList(){
        View cView = View.inflate(getContext(), R.layout.list_item_notes, null);

        mShimmerFrameLayout.setVisibility(View.VISIBLE);
        mShimmerFrameLayout.addView(cView);
        mShimmerFrameLayout.startShimmer();

        notesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mDataAccess.getNotesList(list->{
            mShimmerFrameLayout.setVisibility(View.GONE);
            notesRecycler.setAdapter(new NotesListAdapter(list, getContext()));
        });
    }
}