package com.videotel.voa.response;

import com.videotel.voa.shared.interactions.SimpleChoiceRenderer;

public class ChoiceResponse {
    SimpleChoiceRenderer interaction;
    int total;
    int current;

    public ChoiceResponse(SimpleChoiceRenderer interaction, int total, int current) {
        this.interaction = interaction;
        this.total = total;
        this.current = current;
    }

    public SimpleChoiceRenderer getInteraction() {
        return interaction;
    }

    public int getTotal() {
        return total;
    }

    public int getCurrent() {
        return current;
    }

    public void setInteraction(SimpleChoiceRenderer renderer) {
        this.interaction = interaction;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setCurrent(int current) {
        this.current = current;
    }
}
