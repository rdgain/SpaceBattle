package altgame;

import core.game.Game;
import core.game.StateObservation;
import core.player.AbstractMultiPlayer;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;

import static controllers.singlePlayer.sampleFlatMCTS.Agent.random;

/**
 * Created by simonmarklucas on 23/10/2016.
 *
 *  The aim of this is to provide
 *
 */
public class AltGameTest {
    public static void main(String[] args) {
        // make an agent to test


        StateObservation stateObs = new SimpleMaxGame();

        ElapsedCpuTimer timer = new ElapsedCpuTimer();

        AbstractPlayer player;

        controllers.singlePlayer.sampleOLMCTS.Agent olmcts =
                new controllers.singlePlayer.sampleOLMCTS.Agent(stateObs, timer);

        player = olmcts;

        int thinkingTime = 2000; // in milliseconds

        // player = new controllers.singlePlayer.sampleRandom.Agent(stateObs, timer);

        // check that we can play the game

        Random random = new Random();
        int nSteps = 10;

        for (int i=0; i<nSteps && !stateObs.isGameOver(); i++) {
            ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();

            timer = new ElapsedCpuTimer();
            timer.setMaxTimeMillis(thinkingTime);
            Types.ACTIONS action = player.act(stateObs.copy(), timer);

            // use this for a random action
            // action = actions.get(random.nextInt(actions.size()));
            System.out.println("Selected: " + action); //  + "\t " + action.ordinal());
            stateObs.advance(action);
            System.out.println(stateObs.getGameScore());
        }

        System.out.println(stateObs.getGameScore());
        System.out.println(stateObs.isGameOver());

    }
}
