package com.videotel.voa.response;

import com.videotel.voa.shared.interactions.SimpleChoiceRenderer;

public class ChoiceResponse {
    SimpleChoiceRenderer question;
    int total;
    int current;

    public ChoiceResponse(SimpleChoiceRenderer question, int total, int current) {
        this.question = question;
        this.total = total;
        this.current = current;
    }

    public SimpleChoiceRenderer getQuestion() {
        return question;
    }

    public int getTotal() {
        return total;
    }

    public int getCurrent() {
        return current;
    }

    public void setQuestion(SimpleChoiceRenderer renderer) {
        this.question = question;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setCurrent(int current) {
        this.current = current;
    }
}
