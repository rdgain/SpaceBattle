package altgame;

import core.game.ForwardModel;
import core.game.Observation;
import core.game.StateObservation;

import java.util.ArrayList;

/**
 * Created by simonmarklucas on 23/10/2016.
 */
public class SimpleStateObservationAdapter extends StateObservation {
    /**
     * Constructor for StateObservation. Requires a forward model
     *
     * @param a_model forward model of the game.
     */

    public SimpleStateObservationAdapter(ForwardModel a_model) {
        super(a_model);
    }

    public SimpleStateObservationAdapter() {
        this(null);
    }

    public static void main(String[] args) {
        SimpleStateObservationAdapter test = new SimpleStateObservationAdapter();

        System.out.println(test);
    }

    public ArrayList<Observation>[][] getObservationGrid() {
        return null;

    }

    public int getBlockSize() {
        return 1;
    }

    public ArrayList<Observation>[] getNPCPositions() {

        return null;
    }

    public ArrayList<Observation>[] getImmovablePositions() {
        return null;
    }

    public ArrayList<Observation>[] getMovablePositions() {
        return null;
    }

    public ArrayList<Observation>[] getResourcesPositions() {
        return null;
    }

    public ArrayList<Observation>[] getPortalsPositions() {
        return null;
    }







}
