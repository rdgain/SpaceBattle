package evodef;

import core.game.StateObservation;
import core.game.StateObservationMulti;
import gvglink.SpaceBattleLinkState;
import gvglink.SpaceBattleLinkStateTwoPlayer;
import ontology.Types;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sml on 20/01/2017.
 */
public class GameActionSpaceAdapterMulti implements FitnessSpace {
    public StateObservationMulti stateObservation;
    int sequenceLength;
    EvolutionLogger logger;
    int nEvals;
    public static boolean useDiscountFactor = true;
    public static boolean useHeuristic = true;
    static Random random = new Random();
    static double noiseLevel = 0;

    public int numActions;
    public Types.ACTIONS[][] gvgaiActions;


    // this is used to value future rewards less
    // than immediate ones via an exponential decay
    double discountFactor = 0.99;

    int playerID;
    int opponentID;

    private final double HUGE_NEGATIVE = -10000000.0;
    private final double HUGE_POSITIVE =  10000000.0;


    /**
     * For now assume that the number of actions available at each game tick is always
     * the same and may be found with a call to stateObservation
     *
     * @param stateObservation
     * @param sequenceLength
     */
    public GameActionSpaceAdapterMulti(StateObservationMulti stateObservation, int sequenceLength, int playerID, int opponentID) {
        this.stateObservation = stateObservation;
        this.sequenceLength = sequenceLength;

        gvgaiActions = new Types.ACTIONS[stateObservation.getNoPlayers()][];
        for (int i = 0; i < stateObservation.getNoPlayers(); i++) {
            ArrayList<Types.ACTIONS> act = stateObservation.getAvailableActions(i);
            gvgaiActions[i] = new Types.ACTIONS[act.size()];
            for (int j = 0; j < act.size(); ++j) {
                gvgaiActions[i][j] = act.get(j);
            }
        }

        numActions = gvgaiActions[playerID].length;
        logger = new EvolutionLogger();
        nEvals = 0;
        this.playerID = playerID;
        this.opponentID = opponentID;
    }


    @Override
    public int nDims() {
        return sequenceLength;
    }

    @Override
    public int nValues(int i) {
        // we assume that the nummber of actions available (and hence number of possible
        // values at each point in the search space
        // is always the same
        return numActions;
    }

    public void setNumActions(int numActions) {this.numActions = numActions;}

    @Override
    public void reset() {
        // no action is needed apart from resetting the count;
        // the state is defined by the stateObservation that is passed to this
        logger.reset();
        nEvals = 0;
    }

    @Override
    public double evaluate(int[] actions) {
        // take a copy of the current game state and accumulate the score as we go along
        StateObservationMulti obs = stateObservation.copy();

        gvgaiActions = new Types.ACTIONS[obs.getNoPlayers()][];
        for (int i = 0; i < obs.getNoPlayers(); i++) {
            ArrayList<Types.ACTIONS> act = obs.getAvailableActions(i);
            gvgaiActions[i] = new Types.ACTIONS[act.size()];
            for (int j = 0; j < act.size(); ++j) {
                gvgaiActions[i][j] = act.get(j);
            }
        }

        numActions = gvgaiActions[playerID].length;
        int numActionsOpp = gvgaiActions[opponentID].length;

        // note the score now - for normalisation reasons
        // we wish to track the change in score, not the absolute score
        double initScore = obs.getGameScore(playerID);
        double discount = 1.0;
        double denom = 0;
        double discountedTot = 0;

        for (int i=0; i<sequenceLength; i++) {

            if (obs.isGameOver()) {
                break;
            }

            // Note here that we need to look at the advance method which takes multiple players
            // hence an array of actions
            // the idea is that we'll pad out the
            int myAction = actions[i];
            int opAction = random.nextInt(numActionsOpp);
            Types.ACTIONS[] acts = new Types.ACTIONS[2];
            acts[playerID] = gvgaiActions[playerID][myAction];
            acts[opponentID] = gvgaiActions[opponentID][opAction];

            obs.advance(acts);

            discountedTot += discount * (obs.getGameScore(playerID) - initScore);
            if (useHeuristic && obs instanceof SpaceBattleLinkStateTwoPlayer) {
                SpaceBattleLinkStateTwoPlayer state = (SpaceBattleLinkStateTwoPlayer) obs;
                discountedTot += state.getHeuristicScore();
            }
            denom += discount;
            discount *= discountFactor;
        }

        nEvals++;
        double delta;
        if (useDiscountFactor) {
            delta = discountedTot / denom;
        } else {
            delta = obs.getGameScore(playerID) - initScore;
        }

        if (obs.isGameOver()) {
            boolean iWon = obs.getMultiGameWinner()[playerID] == Types.WINNER.PLAYER_WINS;
            boolean theyWon = obs.getMultiGameWinner()[opponentID] == Types.WINNER.PLAYER_WINS;
            if (iWon)
                delta += HUGE_POSITIVE;
            else delta += HUGE_NEGATIVE;
        }

        delta += noiseLevel * random.nextGaussian();

        return delta;
    }

    @Override
    public boolean optimalFound() {
        return false;
    }

    @Override
    public SearchSpace searchSpace() {
        return this;
    }

    @Override
    public int nEvals() {
        return nEvals;
    }

    @Override
    public EvolutionLogger logger() {
        return logger;
    }

    @Override
    public Double optimalIfKnown() {
        return null;
    }
}
