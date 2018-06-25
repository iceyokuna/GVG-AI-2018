package crazypet.heuristic;

import core.game.StateObservation;

import java.util.Map;

public class ResourceHeuristic implements Heuristic{
    @Override
    public double evaluate(StateObservation stateObs, Double max_point){
        double cost = 0;
        for (Map.Entry resource : stateObs.getAvatarResources().entrySet()) {
            cost += (Integer)resource.getValue();
        }
        return (cost > max_point) ? max_point : cost;
    }
}