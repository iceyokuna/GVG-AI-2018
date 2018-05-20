package crazypet.heuristic;

import core.game.StateObservation;

public class ScoreHeuristic implements Heuristic{
    @Override
    public double evaluate(StateObservation stateObs, Double max_point){
        if (max_point == null) {
            return stateObs.getGameScore();
        }
        return stateObs.getGameScore() / 25.0 * max_point;
    }
}
