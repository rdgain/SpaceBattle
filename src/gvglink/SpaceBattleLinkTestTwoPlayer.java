package gvglink;

import asteroids.Controller;
import battle.BattleGameParameters;
import battle.BattleView;
import battle.SampleEvolvedParams;
import controllers.singlePlayer.discountOLMCTS.SingleTreeNode;
import core.game.StateObservationMulti;
import core.player.AbstractMultiPlayer;
import core.player.AbstractPlayer;
import evodef.EvoAlg;
import evodef.GameActionSpaceAdapter;
import evodef.GameActionSpaceAdapterMulti;
import evogame.Mutator;
import ga.SimpleRMHC;
import ontology.Types;
import tools.ElapsedCpuTimer;
import utilities.ElapsedTimer;
import utilities.JEasyFrame;
import utilities.StatSummary;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sml on 24/10/2016.
 */
public class SpaceBattleLinkTestTwoPlayer {

    public static boolean runVisible = false;

    public static void main(String[] args) {
        StatSummary ss = new StatSummary();
        int nTrials = 20;
        ElapsedTimer t = new ElapsedTimer();

        ArrayList<Double> results = new ArrayList<>();

        for (int i=0; i<nTrials; i++) {
            double result = runTrial(runVisible);
            ss.add(result);
            results.add(result);
        }
        System.out.println(ss);
        System.out.println();
        System.out.println(results);
        System.out.println(t);
    }

    public static double runTrial(boolean runVisible) {
        // make an agent to test

        // StateObservation stateObs = new SimpleMaxGame();

        // BattleGameSearchSpace.inject(BattleGameSearchSpace.getRandomPoint());

        // SampleEvolvedParams.solutions[1][2] = 5;
        // BattleGameSearchSpace.inject(SampleEvolvedParams.solutions[1]);
        BattleGameSearchSpace.inject(SampleEvolvedParams.solutions[2]);
        // BattleGameSearchSpace.inject(SampleEvolvedParams.solutions[1]);

        System.out.println("Params are:");
        System.out.println(BattleGameParameters.params);

        // can also overide parameters by setting them directly as follows:
        // BattleGameParameters.loss = 1.1;
        SpaceBattleLinkStateTwoPlayer linkState = new SpaceBattleLinkStateTwoPlayer();
        StateObservationMulti multi = linkState;

        Mutator.totalRandomChaosMutation = false;

        // // supercl
        // StateObservation stateObs = linkState;

        ElapsedCpuTimer timer = new ElapsedCpuTimer();

        AbstractMultiPlayer player1, player2;

//        controllers.singlePlayer.sampleOLMCTS.Agent olmcts =
//                new controllers.singlePlayer.sampleOLMCTS.Agent(linkState, timer);


        int nEvals = 1000;
        int idPlayer1 = 0;
        int idPlayer2 = 1;
        player1 = new controllers.multiPlayer.discountOLMCTS.Agent(linkState, timer, 0, nEvals);


        // try the evolutionary players

        int nResamples = 3;
        EvoAlg evoAlg = new SimpleRMHC(nResamples);

        double kExplore = 10;
        int nNeighbours = 100;

        // evoAlg = new NTupleBanditEA(kExplore, nNeighbours);

        player2 = new controllers.multiPlayer.ea.Agent(linkState, timer, evoAlg, idPlayer2, nEvals);


        // player1  = new controllers.multiPlayer.smlrand.Agent();

        EvoAlg evoAlg2 = new SimpleRMHC(1);

        // player1 = new controllers.multiPlayer.ea.Agent(linkState, timer, evoAlg2, idPlayer1, nEvals / 5);

        int thinkingTime = 50; // in milliseconds
        int delay = 10;

        // player = new controllers.singlePlayer.sampleRandom.Agent(stateObs, timer);

        // check that we can play the game

        Random random = new Random();
        int nSteps = 500;

        ElapsedTimer t = new ElapsedTimer();
        BattleView view = new BattleView(linkState.state);

        // set view to null to run fast with no visuals
         if (!runVisible) view = null;

        if (view != null) {
            new JEasyFrame(view, "Simple Battle Game");
        }

        for (int i=0; i<nSteps && !linkState.isGameOver(); i++) {
            timer = new ElapsedCpuTimer();
            timer.setMaxTimeMillis(thinkingTime);


            Types.ACTIONS action1 = player1.act(multi.copy(), timer);
            Types.ACTIONS action2 = player2.act(multi.copy(), timer);

            multi.advance(new Types.ACTIONS[]{action1, action2});

            if (view != null) {
                view.repaint();
                try {
                    Thread.sleep(delay);
                } catch (Exception e) {}
            }

            System.out.println(multi.getGameScore());
        }

        System.out.println(multi.getGameScore());
        System.out.println(multi.isGameOver());

        // System.out.println(SingleTreeNode.rollOutScores);

        return multi.getGameScore(0);

    }
}
