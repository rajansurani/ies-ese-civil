package com.upscrks.iesesecivil.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.upscrks.iesesecivil.Adapter.BooksListAdapter;
import com.upscrks.iesesecivil.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BooksFragment extends BaseFragment {

    @BindView(R.id.booksLayout)
    LinearLayout layoutBooks;

    public BooksFragment() {
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
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDataAccess.getBooksSubjectList(list->{
            for(String subject : list){
                createHorizontalLayout(subject);
            }
        });
    }

    private void createHorizontalLayout(String subject) {
        if (!isAdded()) return;

        View view = getLayoutInflater().inflate(R.layout.layout_horizontal_books_tile, null, false);
        TextView tvSubject = view.findViewById(R.id.tvSubject);
        tvSubject.setText(subject);

        View cView = View.inflate(getContext(), R.layout.list_item_books, null);

        ShimmerFrameLayout shimmerBooks = view.findViewById(R.id.shimmer);
        shimmerBooks.setVisibility(View.VISIBLE);
        shimmerBooks.addView(cView);
        shimmerBooks.startShimmer();

        RecyclerView booksRecycler = view.findViewById(R.id.booksRecycler);

        booksRecycler.setHasFixedSize(true);
        booksRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        mDataAccess.getBooksBySubject(subject, list -> {
            shimmerBooks.setVisibility(View.GONE);
            booksRecycler.setAdapter(new BooksListAdapter(list, getContext()));
        });

        layoutBooks.addView(view);
    }
}