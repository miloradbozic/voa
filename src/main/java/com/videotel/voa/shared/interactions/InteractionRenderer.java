package com.videotel.voa.shared.interactions;

public interface InteractionRenderer {
    void setId(String id);
    void setQuestion(String question);
    void addChoice(String choice);
    void render();
}
