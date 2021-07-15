package com.upscrks.iesesecivil.Database.Model;


import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class MockTest implements Serializable {
    String mockTestId;
    List<Integer> questionNumbers;
    int totalTimeAllowed;
    int totalTimeTaken;
    int totalQuestions;
    int totalQuestionsAnswered;
    int correctAnswers;
    Timestamp createdOn;
    Timestamp finishedOn;

    public MockTest() {
    }

    public String getMockTestId() {
        return mockTestId;
    }

    public void setMockTestId(String mockTestId) {
        this.mockTestId = mockTestId;
    }

    public List<Integer> getQuestionNumbers() {
        return questionNumbers;
    }

    public void setQuestionNumbers(List<Integer> questionNumbers) {
        this.questionNumbers = questionNumbers;
    }

    public int getTotalTimeAllowed() {
        return totalTimeAllowed;
    }

    public void setTotalTimeAllowed(int totalTimeAllowed) {
        this.totalTimeAllowed = totalTimeAllowed;
    }

    public int getTotalTimeTaken() {
        return totalTimeTaken;
    }

    public void setTotalTimeTaken(int totalTimeTaken) {
        this.totalTimeTaken = totalTimeTaken;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTotalQuestionsAnswered() {
        return totalQuestionsAnswered;
    }

    public void setTotalQuestionsAnswered(int totalQuestionsAnswered) {
        this.totalQuestionsAnswered = totalQuestionsAnswered;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getFinishedOn() {
        return finishedOn;
    }

    public void setFinishedOn(Timestamp finishedOn) {
        this.finishedOn = finishedOn;
    }
}

