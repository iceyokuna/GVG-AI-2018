package crazypet;

//Required Imports
import core.game.Observation;
import core.game.StateObservation;
import crazypet.heuristic.GameAgent;
import crazypet.heuristic.MyHeuristic;
import tools.ElapsedCpuTimer;
import ontology.Types;

//Agent-Dependent Imports
import java.util.ArrayList;


public class MCTSAgent extends GameAgent {

    private ArrayList<Types.ACTIONS> action_list;
    private ArrayList<Node> node_stack;
    private ArrayList<Node> root_action;
    private MyHeuristic customHeuristic;

    //Constructor. It must return in 1 second maximum.
    public MCTSAgent(StateObservation stateObservation, ElapsedCpuTimer elapsedTime)
    {
        super(stateObservation, elapsedTime);
        //initialize game parameter


        //initialize path finder (A star algorithm) ignored obstracle objects
        initializeGame();
    }

    public void initializeGame(){

    }

    public void expand(){

    }

    public void simulate(){

    }

    public void backup(){

    }

    public void select(){

    }

    //core function to return action to controller
    @Override
    public Types.ACTIONS run(StateObservation stateObservation, ElapsedCpuTimer elapsedTime) {
        Types.ACTIONS action = null;

        return action;
    }
}