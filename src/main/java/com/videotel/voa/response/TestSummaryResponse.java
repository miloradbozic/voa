package com.videotel.voa.response;

import java.util.List;

public class TestSummaryResponse {
    List<QuestionResponse> questionResponses;
    String score;
    String duration;

    public TestSummaryResponse() {
    }

    public TestSummaryResponse(List<QuestionResponse> questionResponses, String score, String duration) {
        this.questionResponses = questionResponses;
        this.score = score;
        this.duration = duration;
    }

    public List<QuestionResponse> getQuestionResponses() {
        return questionResponses;
    }

    public void addQuestionResponse(String question, boolean respondedCorrectly, String providedAnswer, String correctAnswer, String score) {
        this.questionResponses.add(new QuestionResponse(question, respondedCorrectly, providedAnswer, correctAnswer, score));
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

class QuestionResponse {
    String question;
    String providedAnswer;
    String correctAnswer;
    String score;

    public boolean isRespondedCorrectly() {
        return respondedCorrectly;
    }

    public void setRespondedCorrectly(boolean respondedCorrectly) {
        this.respondedCorrectly = respondedCorrectly;
    }

    boolean respondedCorrectly;

    public QuestionResponse(String question, boolean respondedCorrectly, String providedAnswer, String correctAnswer, String score) {
        this.question = question;
        this.respondedCorrectly = respondedCorrectly;
        this.providedAnswer = providedAnswer;
        this.correctAnswer = correctAnswer;
        this.score = score;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getProvidedAnswer() {
        return providedAnswer;
    }

    public void setProvidedAnswer(String providedAnswer) {
        this.providedAnswer = providedAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
