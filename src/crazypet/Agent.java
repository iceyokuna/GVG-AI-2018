package crazypet;

//Required Imports
import core.player.AbstractPlayer;
import core.game.StateObservation;
import crazypet.heuristic.GameAgent;
import tools.ElapsedCpuTimer;
import ontology.Types;

//Agent-Dependent Imports
import java.lang.reflect.Type;
import java.util.Random;
import java.util.ArrayList;

public class Agent extends AbstractPlayer {

    protected Random randomGenerator;
    private GameAgent astarPlayer;

    //Constructor. It must return in 1 second maximum.
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        astarPlayer = new AstarAgent(so,elapsedTimer);
        randomGenerator = new Random();
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        //ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();
        //int index = randomGenerator.nextInt(actions.size());
        //Types.ACTIONS action = actions.get(index);

        Types.ACTIONS action = astarPlayer.run(stateObs,elapsedTimer);
        if(action != null){
            return action;
        }

        //System.out.println("action commit");
        return Types.ACTIONS.ACTION_NIL;
    }

}