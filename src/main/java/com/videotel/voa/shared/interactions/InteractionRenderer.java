package com.videotel.voa.shared.interactions;

public interface InteractionRenderer {
    void setQuestion(String question);
    void addChoice(String choice);
    void render();
}
