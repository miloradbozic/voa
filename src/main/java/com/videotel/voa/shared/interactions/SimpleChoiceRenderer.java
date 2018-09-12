package com.videotel.voa.shared.interactions;

import java.util.Map;

public class SimpleChoiceRenderer extends InteractionAbstractRenderer {

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setQuestion(String question_text) {
        this.question_text = question_text;
    }

    @Override
    public void addChoice(String choice) {
        this.choices.put(String.valueOf(choices.values().size() + 1), choice);
    }

    @Override
    public void render() {
        System.out.println("Question is : " + this.question_text);
        System.out.println("Choices: ");
        for (String choice : this.choices.values()) {
            System.out.println("\t" + choice);
        }
    }

    public String getId() { return this.id; }

    public String getQuestionText() {
        return this.question_text;
    }

    public Map<String, String> getChoices() {
        return this.choices;
    }


}
