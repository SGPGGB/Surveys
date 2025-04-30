package de.sgpggb.surveys.events;

import de.sgpggb.surveys.model.Answer;
import de.sgpggb.surveys.model.Question;
import net.md_5.bungee.api.plugin.Event;

public class SurveysQuestionAnsweredEvent extends Event {

    private final Question question;
    private final Answer answer;

    public SurveysQuestionAnsweredEvent(Question question, Answer answer) {
        this.question = question;
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public Answer getAnswer() {
        return answer;
    }


}
