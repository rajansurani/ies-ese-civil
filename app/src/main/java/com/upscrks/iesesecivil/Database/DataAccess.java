package com.upscrks.iesesecivil.Database;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.upscrks.iesesecivil.Application.Constants;
import com.upscrks.iesesecivil.Application.Helper;
import com.upscrks.iesesecivil.Database.Model.Books;
import com.upscrks.iesesecivil.Database.Model.MCQ;
import com.upscrks.iesesecivil.Database.Model.MCQData;
import com.upscrks.iesesecivil.Database.Model.MockData;
import com.upscrks.iesesecivil.Database.Model.MockTest;
import com.upscrks.iesesecivil.Database.Model.Notes;
import com.upscrks.iesesecivil.Database.Model.User;
import com.upscrks.iesesecivil.Utils.OnCompleteListener;
import com.upscrks.iesesecivil.Utils.OnCompleteSingleListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;

public class DataAccess {

    // static variable single_instance of type Singleton
    private static DataAccess mDataAccess = null;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private Context mContext;

    private DataAccess(Context context) {
        this.mFirestore = Helper.getFirestore();
        this.mStorage = FirebaseStorage.getInstance();
        this.mDatabase = Helper.getDatabase().getReference(Constants.FIREBASE_DB);
        this.mAuth = FirebaseAuth.getInstance();
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mContext = context;
    }

    // static method to create instance of Singleton class
    public static DataAccess getInstance(Context context) {
        if (mDataAccess == null)
            mDataAccess = new DataAccess(context);

        return mDataAccess;
    }

    public void getCurrentUser(boolean applyLister, OnCompleteSingleListener<User> onCompleteListener) {

        if (mAuth.getCurrentUser() != null) {
            DatabaseReference reference = mDatabase.child(User.TABLE).child(mAuth.getCurrentUser().getUid());
            reference.keepSynced(true);
            ValueEventListener valueEventListner = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    logEvent("RDB_READ", "USERS");
                    if (user != null) {
                        onCompleteListener.OnComplete(user);
                    } else {
                        onCompleteListener.OnComplete(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            if (applyLister) {
                reference.addValueEventListener(valueEventListner);
            } else {
                reference.addListenerForSingleValueEvent(valueEventListner);
            }
        } else {
            onCompleteListener.OnComplete(null);
        }
    }

    public void updateUser(Map<String, Object> update, OnCompleteSingleListener listener) {
        mDatabase.child(User.TABLE).child(mAuth.getCurrentUser().getUid())
                .updateChildren(update)
                .addOnCompleteListener(task -> {
                    logEvent("RDB_WRITE", "USERS");
                    if (task.isSuccessful() && listener != null) {
                        listener.OnComplete(null);
                    }
                });
    }

    public void logEvent(String name, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, value);
        bundle.putString("value", value);
        mFirebaseAnalytics.logEvent(name, bundle);
    }

    public void getSubjects(boolean isPreviousYear, OnCompleteListener<String> onCompleteListener) {
        String key = "subjects";
        if (isPreviousYear)
            key = "subject_prev";
        mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot d : snapshot.getChildren()) {
                        list.add(d.getValue(String.class));
                    }
                }
                onCompleteListener.OnComplete(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getMCQ(Map<String, Object> filters,
                       String sortkey,
                       long limit,
                       long lastCreatedOn,
                       Query.Direction direction,
                       DocumentSnapshot startValue,
                       OnCompleteListener<MCQ> listener) {
        Query query = mFirestore.collection("mcq");

        if (!Helper.IsNullOrEmpty(sortkey)) {
            query = query.orderBy(sortkey, direction);
        }

        for (String key : filters.keySet()) {
            query = query.whereEqualTo(key, filters.get(key));
        }

        query = query.whereGreaterThan("createdOn", lastCreatedOn);

        if (limit == 0)
            query = query.limit(10);
        else
            query = query.limit(limit);

        if (startValue != null) {
            query = query.startAfter(startValue);
        }

        query.get()
                .addOnCompleteListener(task -> {
                    List<MCQ> mcqs = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        MCQ mcq = document.toObject(MCQ.class);
                        mcq.setQuestionId(document.getId());
                        mcqs.add(mcq);
                        logEvent("FIRESTORE_READ", "MCQ");
                    }
                    if (listener != null)
                        listener.OnComplete(mcqs);
                });
    }

    public void getLastQuestion(String subject, OnCompleteSingleListener<Long> listener) {
        mDatabase.child("lastQuestion")
                .child(mAuth.getCurrentUser().getUid())
                .child(subject)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.getValue() != null)
                            listener.OnComplete(dataSnapshot.getValue(Long.class));
                        else
                            listener.OnComplete(0L);

                        logEvent("RDB_READ", "LAST_QUESTION");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.OnComplete(0L);
                    }
                });

    }

    public void addMcqActivity(MCQData data, String key, long questionCreatedOn, OnCompleteSingleListener<Boolean> onCompleteSingleListener) {
        mFirestore.collection("userActivity")
                .document(mAuth.getCurrentUser().getUid())
                .collection("mcq")
                .document(data.getQuestionId())
                .set(data)
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            onCompleteSingleListener.OnComplete(true);
                        else
                            onCompleteSingleListener.OnComplete(false);
                        logEvent("FIRESTORE_WRITE", "userActivity");
                    }
                });

        mDatabase.child("lastQuestion").child(mAuth.getCurrentUser().getUid()).child(key).setValue(questionCreatedOn);
        getCurrentUser(false, user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("questionsSolved", user.getQuestionsSolved() + 1);
            updateUser(map, onComplete -> {
            });
        });
    }

    public void addMCQ(MCQ question, OnCompleteSingleListener<Boolean> onCompleteSingleListener) {
        mFirestore.collection("mcq").document().set(question).addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    onCompleteSingleListener.OnComplete(true);
                else
                    onCompleteSingleListener.OnComplete(false);
            }
        });
    }

    public void addBook(Books books, OnCompleteSingleListener<Boolean> onCompleteSingleListener) {
        mFirestore.collection("books").document().set(books).addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    onCompleteSingleListener.OnComplete(true);
                else
                    onCompleteSingleListener.OnComplete(false);
            }
        });
    }

    public void getBooksBySubject(String subject, OnCompleteListener<Books> onCompleteListener) {
        Query query = mFirestore.collection("books");
        query = query.whereEqualTo("subject", subject);

        query.get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Books> books = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    Books book = document.toObject(Books.class);
                    books.add(book);
                    logEvent("FIRESTORE_READ", "BOOKS");
                }
                onCompleteListener.OnComplete(books);
            }
        });
    }

    public void getBooksSubjectList(OnCompleteListener<String> onCompleteListener) {
        mDatabase.child("books_subject").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot d : snapshot.getChildren()) {
                        list.add(d.getValue(String.class));
                    }
                }
                onCompleteListener.OnComplete(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getNotesList(OnCompleteListener<Notes> listener) {
        Query query = mFirestore.collection("notes")
                .orderBy("title");

        query.get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Notes> notes = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    Notes note = document.toObject(Notes.class);
                    notes.add(note);
                    logEvent("FIRESTORE_READ", "NOTES");
                }
                listener.OnComplete(notes);
            }
        });
    }

    public void getNotesPdf(String url, OnCompleteSingleListener<byte[]> listener) {
        final long HUNDRED_MB = 1024 * 1024 * 100;
        mStorage.getReferenceFromUrl(url).getBytes(HUNDRED_MB).addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if (task.isSuccessful()) {
                    listener.OnComplete(task.getResult());
                } else
                    listener.OnComplete(null);
            }
        });
    }

    public void getCsv(OnCompleteSingleListener<String> onCompleteSingleListener) {
        final long ONE_MB = 1024 * 1024;
        mStorage.getReference("questions.csv").getBytes(ONE_MB).addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if (task.isSuccessful()) {
                    String s = new String(task.getResult());
                    onCompleteSingleListener.OnComplete(s);
                }
            }
        });
    }

    public void deleteMcq(String id) {
        mFirestore.collection("mcq").document(id).delete();
    }

    public void createNewMockTest(int numberOfQuestions, OnCompleteSingleListener<MockTest> onCompleteSingleListener) {
        mFirestore.collection("mcq").orderBy("questionNumber", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int questionNumber = task.getResult().getDocuments().get(0).toObject(MCQ.class).getQuestionNumber();
                List<Integer> questionsList = new ArrayList<>();
                MockTest mockTest = new MockTest();
                mockTest.setMockTestId(Helper.md5(new Date().toString()));
                mockTest.setCreatedOn(new Timestamp(new Date()));
                while (questionsList.size() != numberOfQuestions) {
                    int random = new Random().nextInt(questionNumber);
                    if (!questionsList.contains(random)) {
                        questionsList.add(random);
                    }
                }
                mockTest.setQuestionNumbers(questionsList);
                mockTest.setTotalTimeAllowed(questionsList.size());
                mockTest.setTotalQuestions(questionsList.size());

                for (int number : mockTest.getQuestionNumbers()) {
                    MockData data = new MockData();
                    data.setQuestionNumber(number);
                    data.setMockId(mockTest.getMockTestId());
                    data.setUserAnswer(0);
                    data.setCorrect(false);
                    addMockTestActivity(data, onComplete->{});
                }
                mFirestore.collection("mock").document(mAuth.getCurrentUser().getUid()).collection("mockTest").document(mockTest.getMockTestId()).set(mockTest);
                onCompleteSingleListener.OnComplete(mockTest);
                logEvent("FIRESTORE_WRITE", "newMock");
            }
        });
    }

    public void updateMockTest(MockTest mockTest, OnCompleteSingleListener<Boolean> onCompleteSingleListener) {
        mFirestore.collection("mock").document(mAuth.getCurrentUser().getUid()).collection("mockTest").document(mockTest.getMockTestId()).set(mockTest).addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onCompleteSingleListener.OnComplete(task.isSuccessful());

                logEvent("FIRESTORE_WRITE", "updateMock");
            }
        });
    }

    public void addMockTestActivity(MockData data, OnCompleteSingleListener<Boolean> onCompleteSingleListener) {
        if (Helper.IsNullOrEmpty(data.getId())) {
            data.setId(data.getMockId() + "_" + data.getQuestionNumber());
        }
        mFirestore.collection("mock")
                .document(mAuth.getCurrentUser().getUid())
                .collection("mockTestActivity")
                .document(data.getId())
                .set(data)
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            onCompleteSingleListener.OnComplete(true);
                        else
                            onCompleteSingleListener.OnComplete(false);
                        logEvent("FIRESTORE_WRITE", "mockTestActivity");
                    }
                });
    }

    public void getMockQuestions(MockTest mockTest, OnCompleteListener<MCQ> onCompleteListener) {
        List<MCQ> mcqs = new ArrayList<>();
        mFirestore.collection("mcq").whereIn("questionNumber", mockTest.getQuestionNumbers().subList(0,10)).get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    MCQ mcq = document.toObject(MCQ.class);
                    mcq.setQuestionId(document.getId());
                    mcqs.add(mcq);
                    logEvent("FIRESTORE_READ", "MCQ");
                }
                mFirestore.collection("mcq").whereIn("questionNumber", mockTest.getQuestionNumbers().subList(10,20)).get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            MCQ mcq = document.toObject(MCQ.class);
                            mcq.setQuestionId(document.getId());
                            mcqs.add(mcq);
                            logEvent("FIRESTORE_READ", "MCQ");
                        }
                        mFirestore.collection("mcq").whereIn("questionNumber", mockTest.getQuestionNumbers().subList(20,30)).get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    MCQ mcq = document.toObject(MCQ.class);
                                    mcq.setQuestionId(document.getId());
                                    mcqs.add(mcq);
                                    logEvent("FIRESTORE_READ", "MCQ");
                                }
                                onCompleteListener.OnComplete(mcqs);
                            }
                        });
                    }
                });
            }
        });
    }

    public void fetchMockTests(OnCompleteListener<MockTest> onCompleteListener) {
        mFirestore.collection("mock").document(mAuth.getCurrentUser().getUid()).collection("mockTest").orderBy("createdOn", Query.Direction.DESCENDING).get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<MockTest> mockTests = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    mockTests.add(document.toObject(MockTest.class));
                }
                onCompleteListener.OnComplete(mockTests);
            }
        });
    }

    public void fetchMockTestById(String id,OnCompleteSingleListener<MockTest> onCompleteListener) {
        mFirestore.collection("mock").document(mAuth.getCurrentUser().getUid()).collection("mockTest").document(id).get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                    onCompleteListener.OnComplete(task.getResult().toObject(MockTest.class));
                else
                    onCompleteListener.OnComplete(null);
            }
        });
    }

    public void getMockData(MockTest mockTest, OnCompleteListener<MockData> onCompleteListener){
        Query query = mFirestore.collection("mock").document(mAuth.getCurrentUser().getUid()).collection("mockTestActivity").whereEqualTo("mockId",mockTest.getMockTestId());
        query.get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<MockData> mockData =new ArrayList<>();
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot snapshot : task.getResult().getDocuments()){
                        mockData.add(snapshot.toObject(MockData.class));
                    }
                }
                onCompleteListener.OnComplete(mockData);
            }
        });
    }
}
