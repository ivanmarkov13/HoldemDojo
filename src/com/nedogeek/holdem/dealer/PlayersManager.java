package com.nedogeek.holdem.dealer;

import com.nedogeek.holdem.PlayerStatus;
import com.nedogeek.holdem.connections.PlayerAction;
import com.nedogeek.holdem.gamingStuff.Desk;

/**
 * User: Konstantin Demishev
 * Date: 21.11.12
 * Time: 23:48
 */
public class PlayersManager {
    private final Desk desk;
    private final int playersQuantity;

    PlayersManager(Desk desk) {
        this.desk = desk;
        playersQuantity = desk.getPlayersQuantity();
    }

    int nextPlayer(int playerNumber) {
        if (playerNumber == playersQuantity - 1) {
            return 0;
        } else {
            return playerNumber + 1;
        }
    }

    boolean hasAvailableMovers() {
        return moreThanOnePlayerDoNotFolds() && getMoverNumber() != -1;
    }

    private boolean moreThanOnePlayerDoNotFolds() {
        int playersWithoutFold = 0;
        for (int i = 0; i < playersQuantity; i++) {
            if (desk.getPlayerStatus(i) != PlayerStatus.Fold) {
                playersWithoutFold++;
            }
        }
        return playersWithoutFold > 1;
    }

    PlayerAction getPlayerMove() {
        return desk.getPlayersMove(getMoverNumber());
    }

    int getMoverNumber() {
        int lastMovedPlayer = desk.getLastMovedPlayer();

        if (lastMovedPlayer == -1) {
            return nextPlayer(desk.getDealerPlayerNumber());
        }
        int nextPlayerNumber = nextPlayer(lastMovedPlayer);

        while (nextPlayerNumber != lastMovedPlayer) {
            if (desk.getPlayerStatus(nextPlayerNumber) == PlayerStatus.NotMoved ||
                    (desk.getPlayerStatus(nextPlayerNumber) != PlayerStatus.Fold &&
                            desk.getPlayerBet(nextPlayerNumber) < desk.getCallValue())) {
                return nextPlayerNumber;
            }
            nextPlayerNumber = nextPlayer(nextPlayerNumber);
        }
        return -1;
    }
}