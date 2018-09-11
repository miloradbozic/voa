package com.videotel.voa.shared.interactions;

import java.util.List;
import java.util.Map;

public class SimpleChoiceRenderer extends InteractionAbstractRenderer {


    @Override
    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public void addChoice(String choice) {
        this.choices.put(String.valueOf(choices.values().size() + 1), choice);
    }

    @Override
    public void render() {
        System.out.println("Question is : " + this.question);
        System.out.println("Choices: ");
        for (String choice : this.choices.values()) {
            System.out.println("\t" + choice);
        }
    }

    public String getQuestion() {
        return this.question;
    }

    public Map<String, String> getChoices() {
        return this.choices;
    }


}
