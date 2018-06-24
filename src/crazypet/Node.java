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

    public Node(StateObservation state, Types.ACTIONS root_action, Types.ACTIONS action, double value){
        this.state = state;
        this.root_action = root_action;
        this.value = value;
        this.action = action;

    }

    public void updateNodeState(MyHeuristic heuristic){
        StateObservation nextState = state.copy();
        nextState.advance(action);
        value += heuristic.evaluateState(nextState);
        state = nextState.copy();
    }

    public double getValue(){
        return this.value;
    }

    public Types.ACTIONS getRootAction(){
        return this.root_action;
    }
}