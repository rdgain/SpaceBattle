package altgame;

import core.game.Game;
import core.game.StateObservation;

/**
 * Created by simonmarklucas on 23/10/2016.
 */
public class AltGame extends Game {

    StateObservation obs;

    public AltGame(StateObservation obs) {
        this.obs = obs;
    }

    @Override
    public boolean isGameOver() {
        return obs.isGameOver();
    }

    @Override
    public void buildStringLevel(String[] levelString, int randomSeed) {

    }



}
