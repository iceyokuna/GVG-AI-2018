package crazypet;

//Required Imports
import core.game.StateObservation;
import crazypet.heuristic.MyHeuristic;
import ontology.Types;


public class Node {
    private StateObservation state;
    private Node root_node;
    private Types.ACTIONS action;
    private double value;

    //initialize node using parent node and node expansion
    public Node(StateObservation state, Node root_node, Types.ACTIONS action, double value){
        this.state = state;
        this.root_node = root_node;
        this.value = value;
        this.action = action;

    }

    //update state (advance node state 1 step)
    public void updateNodeState(MyHeuristic heuristic){
        StateObservation nextState = state.copy();
        nextState.advance(action);
        value += heuristic.evaluateState(nextState);
        state = nextState.copy();
        if(root_node != null){
            if(root_node.getValue() < value){
                root_node.updateValue(value);
            }
        }
    }

    public void updateValue(double new_value){
        value = new_value;
    }

    //get value from this node state
    //value evaluated using MyHeuristic Class
    public double getValue(){
        //return value
        return this.value;
    }

    public StateObservation getState(){
        //return Observation state of this node
        return state;
    }

    public Node getRoot(){
        //return root action of this node
        return root_node;
    }

    //get Action of the this Node
    //to return get step to the controller
    public Types.ACTIONS getAction(){
        //get action of this node
        return this.action;
    }

    public Node copy(){
        //return copy of this node
        return new Node(this.state, this.root_node, this.action, this.value);
    }
}