package de.sgpggb.surveys.events;

import de.sgpggb.surveys.model.Group;
import de.sgpggb.surveys.model.Reward;
import net.md_5.bungee.api.plugin.Event;

public class SurveysSurveyCompletedEvent extends Event {

    private final Group group;
    private final Reward reward;

    public SurveysSurveyCompletedEvent(Group group, Reward reward) {
        this.group = group;
        this.reward = reward;
    }

    public Group getGroup() {
        return group;
    }

    public Reward getReward() {
        return reward;
    }
}
