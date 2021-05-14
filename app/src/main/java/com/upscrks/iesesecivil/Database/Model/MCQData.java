package com.upscrks.iesesecivil.Database.Model;

import com.google.firebase.Timestamp;

public class MCQData {
    String userId;
    String questionId;
    boolean correct;
    String subject;
    Timestamp answeredOn;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Timestamp getAnsweredOn() {
        return answeredOn;
    }

    public void setAnsweredOn(Timestamp answeredOn) {
        this.answeredOn = answeredOn;
    }
}
