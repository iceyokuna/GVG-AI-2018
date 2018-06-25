package crazypet;

//Required Imports
import core.game.Observation;
import core.game.StateObservation;
import crazypet.heuristic.GameAgent;
import crazypet.heuristic.MyHeuristic;
import tools.ElapsedCpuTimer;
import ontology.Types;
import tools.Vector2d;

//Agent-Dependent Imports
import java.util.ArrayList;
import java.util.Random;

public class MCTSAgent extends GameAgent {

    private ArrayList<Types.ACTIONS> action_list;
    private ArrayList<Node> root_list;
    private ArrayList<Node> current_list;
    private ArrayList<Node> expand_list;
    private MyHeuristic customHeuristic;
    private Node backupNode;
    private Random randomGenerator;
    private boolean randomEnable;
    private int roll_dept;
    //Constructor. It must return in 1 second maximum.
    public MCTSAgent(StateObservation stateObservation, ElapsedCpuTimer elapsedTime)
    {
        super(stateObservation, elapsedTime);
        //initialize game parameter
        customHeuristic = new MyHeuristic();
        action_list = new ArrayList<>();
        root_list = new ArrayList<>();
        current_list = new ArrayList<>();
        expand_list = new ArrayList<>();
        backupNode = null;
        randomGenerator = new Random();
        randomEnable = false;
        roll_dept = 0;

        //initialize path finder (A star algorithm) ignored obstracle objects
        initializeGame(stateObservation);
    }

    public void initializeGame(StateObservation stateObservation){
        action_list = stateObservation.getAvailableActions();
        for(Types.ACTIONS action : action_list){
            Node node = new Node(stateObservation, null, action, 0 );
            node.updateNodeState(customHeuristic);
            root_list.add(node);
            current_list.add(node);
        }
    }

    //expand the tree with all possible actions
    public void expand(){
        for(int i = 0 ; i < current_list.size() ; i++){
            for(int j = 0 ; j < action_list.size() ; j++){
                Node new_node = simulate(new Node(current_list.get(i).getState(), current_list.get(i),
                        action_list.get(j), current_list.get(i).getValue()));
                expand_list.add(new_node);
            }
        }
        current_list.clear();
        for(Node node : expand_list){
            current_list.add(node);
        }
        expand_list.clear();
    }

    //simulate the world using value iteration methods(Node)
    //instead of roll-out until game end
    //because to save the time of tree expansion
    //(so we use out custom heuristic)
    public Node simulate(Node node){
        node.updateNodeState(customHeuristic);
        return node;
    }

    //update the tree with value (reset value)
    //back up the tree value to continue expand the tree
    public void backup(Node selected_node){
        backupNode = selected_node;
        root_list.clear();
        current_list.clear();
        expand_list.clear();
        for(Types.ACTIONS action : action_list){
            Node node = new Node(selected_node.getState(), null, action, 0 );
            root_list.add(node);
            current_list.add(node);
        }
    }

    //select the best action
    //and agent return action to controller
    public Node select(){
        Node maxNode = null;
        double maxValue = -999999;

        for(Node node : root_list) {
            if(node.getValue() > maxValue){
                maxNode = node;
                maxValue = maxNode.getValue();
            }
        }
        return maxNode;
    }

    public Types.ACTIONS lookAhead(StateObservation stateObservation){
        double maxValue = Double.NEGATIVE_INFINITY;
        Types.ACTIONS selected_action = null;

        for (Types.ACTIONS action : stateObservation.getAvailableActions()){
            StateObservation nextState = stateObservation.copy();
            nextState.advance(action);
            double value = customHeuristic.evaluateState(nextState);

            if(value > maxValue){
                maxValue = value;
                selected_action = action;
            }
        }
        return selected_action;
    }

    public Types.ACTIONS roll_out(StateObservation stateObservation){
        ArrayList<Types.ACTIONS> actions_list = stateObservation.getAvailableActions();

        int index = randomGenerator.nextInt(actions_list.size());
        Types.ACTIONS action = actions_list.get(index);

        StateObservation nextState = stateObservation.copy();
        nextState.advance(action);

        Vector2d curPosition = stateObservation.getAvatarPosition();
        Vector2d nextPosition = nextState.getAvatarPosition();

        while((nextState.isGameOver() && !nextState.isAvatarAlive()) || curPosition.equals(nextPosition)) {
            actions_list.remove(action);
            index = randomGenerator.nextInt(actions_list.size());
            action = actions_list.get(index);

            nextState = stateObservation.copy();
            nextState.advance(action);
            nextPosition = nextState.getAvatarPosition();
        }
        return action;
    }

    //core function to return action to controller
    @Override
    public Types.ACTIONS run(StateObservation stateObservation, ElapsedCpuTimer elapsedTime) {
        long remaining = elapsedTime.remainingTimeMillis();
        int dept = 0;
        Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;
        Node selected_node = null;
        //System.out.println(elapsedTime.remainingTimeMillis());

        while(dept < 0 && remaining > 30) {
            expand();
            //System.out.println(elapsedTime.remainingTimeMillis());
            dept++;
        }
        selected_node = select();
        action = selected_node.getAction();
        backup(selected_node);

        if(roll_dept < 20){
            action = lookAhead(stateObservation);
            roll_dept++;
        }
        else{
            action = roll_out(stateObservation);
        }
        //System.out.println(elapsedTime.remainingTimeMillis());
        //System.out.println(action);
        return action;
    }
}