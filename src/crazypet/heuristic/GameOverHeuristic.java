package crazypet.heuristic;

import core.game.StateObservation;
import ontology.Types;

public class GameOverHeuristic implements Heuristic {
    @Override
    public double evaluate(StateObservation stateObs, Double max_point){
        if (stateObs.isGameOver() && stateObs.getGameWinner() == Types.WINNER.PLAYER_LOSES) {
            return -max_point;
        } else if (stateObs.isGameOver() && stateObs.getGameWinner() == Types.WINNER.PLAYER_WINS){
            return max_point;
        }
        return 0;
    }
}
