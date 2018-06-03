package crazypet.heuristic;

import core.game.StateObservation;

public interface Heuristic {
    public double evaluate(StateObservation stateObs, Double max_point);
}
