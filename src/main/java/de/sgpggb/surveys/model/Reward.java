package de.sgpggb.surveys.model;


import de.sgpggb.pluginutilitieslibbungee.Logging;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.rewardsbungee.RewardsAPI;
import de.sgpggb.sgpggbeconomy.EconomyAPI;
import de.sgpggb.surveys.SurveysPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Reward {

    int id;
    String claimText;
    String infoText;
    String reward;
    RewardType rewardType;

    Logging log = SurveysPlugin.getInstance().getLog();

    public Reward() {
        this.id = -1;
        this.claimText = "ClaimText";
        this.infoText = "InfoText";
        this.reward = "1";
        this.rewardType = RewardType.MONEY;
    }

    public Reward(int id, String claimText, String infoText, String reward, RewardType rewardType) {
        this.id = id;
        this.claimText = claimText;
        this.infoText = infoText;
        this.reward = reward;
        this.rewardType = rewardType;
    }

    public void give(User user) {
        ProxiedPlayer player = SurveysPlugin.getInstance().getProxy().getPlayer(user.getUuid());
        if (player == null || !player.isConnected()) {
            log.error("tried to give player " + user.getName() + " a rewardID, but its not online");
            return;
        }

        switch (rewardType) {
            case REWARD -> {
                int id;
                try {
                    id = Integer.parseInt(reward);
                } catch (NumberFormatException e) {
                    log.error("could not create money with value " + reward);
                    return;
                }
                RewardsAPI.addReward(player.getUniqueId(), id);
            }
            case MONEY -> {
                int money;
                try {
                    money = Integer.parseInt(reward);
                } catch (NumberFormatException e) {
                    log.error("could not create money with value " + reward);
                    return;
                }
                EconomyAPI.give(player.getUniqueId(), money, "SURVEYS", "Belohnung für Survey");
            }
        }

        if (!claimText.isEmpty()) {
            ChatUtils.send(player, SurveysPlugin.CHATPREFIX + claimText);
        }
        log.info("player " + player.getName() + " received rewardID " + this.id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public String getInfoText() {
        return infoText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    public String getClaimText() {
        return claimText;
    }

    public void setClaimText(String claimText) {
        this.claimText = claimText;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }
}
