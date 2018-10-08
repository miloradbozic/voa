package com.videotel.voa.model;

import java.lang.reflect.Field;

public class Question {
    private String question;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String choice5;
    private int answer;
    private String tag;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getChoice(int i) {
        // field choice = Question.getClass().getField("choice" + i);
        try {
            Field choice = this.getClass().getDeclaredField("choice" + i);
            choice.setAccessible(true);
            return (String) choice.get(this);
        } catch (Exception e) {
            return null;
        }
    }
    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public String getChoice4() {
        return choice4;
    }

    public void setChoice4(String choice4) {
        this.choice4 = choice4;
    }

    public String getChoice5() {
        return choice5;
    }

    public void setChoice5(String choice5) {
        this.choice5 = choice5;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) { this.tag = tag; }
}
