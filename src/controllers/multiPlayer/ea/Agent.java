package controllers.multiPlayer.ea;

import controllers.multiPlayer.discountOLMCTS.SingleMCTSPlayer;
import core.game.StateObservation;
import core.game.StateObservationMulti;
import core.player.AbstractMultiPlayer;
import evodef.EvoAlg;
import evodef.GameActionSpaceAdapter;
import evodef.GameActionSpaceAdapterMulti;
import evodef.SearchSpaceUtil;
import ga.SimpleRMHC;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sml on 23/01/2017.
 */
public class Agent extends AbstractMultiPlayer {

    public int num_actions;
    public static int SEQUENCE_LENGTH = 10;
    public static boolean useShiftBuffer = true;
    public static boolean useHeuristic = false;
    public static boolean useDiscountFactor = true;

    int nEvals;

    public EvoAlg evoAlg;

    public static void main(String[] args) {
        System.out.println();
    }


    public int[] NUM_ACTIONS;
    public Types.ACTIONS[][] actions;
    public int id, oppID, no_players;

    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservationMulti so, ElapsedCpuTimer elapsedTimer, int playerID)
    {
        //get game information

        double kExplore = 10;
        int nNeighbours = 100;

        // evoAlg = new NTupleBanditEA(kExplore, nNeighbours);

        int nResamples = 3;
        this.evoAlg = new SimpleRMHC(nResamples);

        no_players = so.getNoPlayers();
        id = playerID;
        oppID = (id + 1) % so.getNoPlayers();
//        this.nEvals = 1000;

        //Get the actions for all players in a static array.

        NUM_ACTIONS = new int[no_players];
        actions = new Types.ACTIONS[no_players][];
        for (int i = 0; i < no_players; i++) {

            ArrayList<Types.ACTIONS> act = so.getAvailableActions(i);

            actions[i] = new Types.ACTIONS[act.size()];
            for (int j = 0; j < act.size(); ++j) {
                actions[i][j] = act.get(j);
            }
            NUM_ACTIONS[i] = actions[i].length;
        }
    }



    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */

    int index;
    int[] solution;

    // will only recalculate after this number of steps
    static int playoutLength = 1;


    @Override
    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedCpuTimer) {
        //Set the state observation object as the new root of the tree.

        // we'll set up a game adapter and run the algorithm independently each
        // time at least to being with

        int action1;
        GameActionSpaceAdapterMulti game = new GameActionSpaceAdapterMulti(stateObs, SEQUENCE_LENGTH, id, oppID);

        GameActionSpaceAdapterMulti.useHeuristic = useHeuristic;
        GameActionSpaceAdapterMulti.useDiscountFactor = useDiscountFactor;

        if (solution != null) {
            solution = SearchSpaceUtil.shiftLeftAndRandomAppend(solution, game);
            evoAlg.setInitialSeed(solution);
        }

        solution = evoAlg.runTrial(game, elapsedCpuTimer);

//        System.out.println(Arrays.toString(solution) + "\t " + game.evaluate(solution));

        action1 = solution[0];
        // already return the first element, so now set it to 1 ...

        if (!useShiftBuffer) solution = null;

        index = 1;

        //... and return it.
        // return actions[action];

        return actions[id][action1];
    }


}
