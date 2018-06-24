package crazypet;

//Required Imports
import core.player.AbstractPlayer;
import core.game.StateObservation;
import crazypet.heuristic.GameAgent;
import tools.ElapsedCpuTimer;
import ontology.Types;

//GeneticPlayer-Dependent Imports
import java.util.Random;

public class Agent extends AbstractPlayer {

    protected Random randomGenerator;
    private GameAgent astarPlayer;
    private GeneticPlayer gaPlayer;

    //Constructor. It must return in 1 second maximum.
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        astarPlayer = new AstarAgent(so, elapsedTimer);
        gaPlayer = new GeneticPlayer(so, elapsedTimer);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        Types.ACTIONS action = astarPlayer.run(stateObs,elapsedTimer);

        if(action != null){
            return action;
        }

        return gaPlayer.act(stateObs, elapsedTimer);
    }

}