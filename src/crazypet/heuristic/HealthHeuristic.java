package crazypet.heuristic;

import core.game.StateObservation;

public class HealthHeuristic implements Heuristic {
    @Override
    public double evaluate(StateObservation stateObs, Double max_point){
        if (stateObs.getAvatarMaxHealthPoints() == 0){
            return 0;
        }
        return stateObs.getAvatarHealthPoints() / stateObs.getAvatarMaxHealthPoints() * max_point;
    }
}

