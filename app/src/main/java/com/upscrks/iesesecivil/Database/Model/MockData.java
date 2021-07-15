package com.upscrks.iesesecivil.Database.Model;

import com.google.firebase.Timestamp;

public class MockData {
    String id;
    int questionNumber;
    boolean correct;
    int userAnswer;
    String mockId;
    Timestamp answeredOn;

    public MockData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getMockId() {
        return mockId;
    }

    public void setMockId(String mockId) {
        this.mockId = mockId;
    }

    public Timestamp getAnsweredOn() {
        return answeredOn;
    }

    public void setAnsweredOn(Timestamp answeredOn) {
        this.answeredOn = answeredOn;
    }

    public int getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(int userAnswer) {
        this.userAnswer = userAnswer;
    }
}
