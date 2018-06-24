package crazypet;

//Required Imports
import core.game.StateObservation;
import crazypet.heuristic.MyHeuristic;
import ontology.Types;


public class Node {
    private StateObservation state;
    private Types.ACTIONS root_action;
    private Types.ACTIONS action;
    private double value;

    //initialize node using parent node and node expansion
    public Node(StateObservation state, Types.ACTIONS root_action, Types.ACTIONS action, double value){
        this.state = state;
        this.root_action = root_action;
        this.value = value;
        this.action = action;

    }

    //update state (advance node state 1 step)
    public void updateNodeState(MyHeuristic heuristic){
        StateObservation nextState = state.copy();
        nextState.advance(action);
        value += heuristic.evaluateState(nextState);
        state = nextState.copy();
    }

    //get value from this node state
    //value evaluated using MyHeuristic Class
    public double getValue(){
        return this.value;
    }

    public StateObservation getState(){
        return state;
    }

    //get Root Action of the this Node
    //to return get step to the controller
    public Types.ACTIONS getRootAction(){
        return this.root_action;
    }
}