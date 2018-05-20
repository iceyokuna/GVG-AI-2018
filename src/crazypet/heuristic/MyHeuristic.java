package crazypet.heuristic;

import core.game.StateObservation;
import ontology.Types;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;
import java.util.*;

public class MyHeuristic extends StateHeuristic {
    private Map<Heuristic, Double> heuristics = new HashMap<>();
    private double cost = 0;

    public MyHeuristic() {
        heuristics.put(new GameOverHeuristic(), 10e7);
        heuristics.put(new HealthHeuristic(), 10.0);
        heuristics.put(new ScoreHeuristic(), 10.0);
        heuristics.put(new ScoreHeuristic(), 10.0);
    }

    @Override
    public double evaluateState(StateObservation stateObs) {
        for (Map.Entry<Heuristic, Double> h : heuristics.entrySet())
        {
            cost += h.getKey().evaluate(stateObs, h.getValue());
        }
        return cost;
    }

    public static void main(String[] args) {
        MyHeuristic mh = new MyHeuristic();
        System.out.println(mh.evaluateState(null));
    }
}
