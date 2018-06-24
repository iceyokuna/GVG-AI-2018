package crazypet;

//Required Imports
import core.game.Observation;
import core.game.StateObservation;
import crazypet.heuristic.GameAgent;
import tools.ElapsedCpuTimer;
import ontology.Types;
import tools.Vector2d;
import tools.pathfinder.PathFinder;
import tools.pathfinder.Node;

import java.lang.reflect.Type;
import java.util.Random;


//Agent-Dependent Imports
import java.util.ArrayList;


public class AstarAgent extends GameAgent {

    //necessary parameters
    private int block_size;
    protected Random randomGenerator;

    //Path finder (A star algorithm)
    private PathFinder pathFinder;

    //Agent state
    private int step_count;
    private Types.ACTIONS prev_action;
    private Types.ACTIONS prev_move;
    private Vector2d current_position;
    private Vector2d prev_position;

    //Item state
    private ArrayList<Observation> itemsList[];

    //Portal state
    private ArrayList<Observation> portalsList[];

    //Npc state
    private ArrayList<Observation> npcsList[];

    //Constructor. It must return in 1 second maximum.
    public AstarAgent(StateObservation stateObservation, ElapsedCpuTimer elapsedTime)
    {
        super(stateObservation, elapsedTime);
        //initialize game parameter
        step_count = 0;
        prev_action = Types.ACTIONS.ACTION_NIL;
        prev_move = Types.ACTIONS.ACTION_NIL;
        prev_position = new Vector2d();
        current_position = stateObservation.getAvatarPosition();
        block_size = stateObservation.getBlockSize();
        randomGenerator = new Random();

        //initialize list of game objects
        itemsList = stateObservation.getResourcesPositions();
        portalsList = stateObservation.getPortalsPositions();
        npcsList = stateObservation.getNPCPositions();

        //initialize path finder (A star algorithm) ignored obstracle objects
        initializeGame();
    }

    public void initializeGame(){
        ArrayList<Integer> obstracleTypes = new ArrayList<>();
        obstracleTypes.add(stateObservation.getObservationGrid()[0][0].get(0).itype);
        pathFinder = new PathFinder(obstracleTypes);
        pathFinder.run(stateObservation);
    }

    public int FindBestItem(StateObservation stateObservation, ElapsedCpuTimer elapsedTime){
        //use manhattan distance to closest object
        return 0;
    }

    public int FindBestPortal(StateObservation stateObservation, ElapsedCpuTimer elapsedTime){
        //use manhattan distance to closest object
        return 0;
    }

    public int FindBestNpc(StateObservation stateObservation, ElapsedCpuTimer elapsedTime){
        //use manhattan distance to closest object
        return 0;
    }

    public Types.ACTIONS walkToItem(StateObservation stateObservation, ElapsedCpuTimer elapsedTime){
        //System.out.println("Enter walk to Item");
        itemsList = stateObservation.getResourcesPositions();
        if(itemsList != null){
            Vector2d playerPosition = stateObservation.getAvatarPosition();
            //upgrade best item should collect first(index get)
            Vector2d goalPosition = itemsList[0].get(0).position;

            //need to interact with item
            if(playerPosition.equals(goalPosition)){
                return Types.ACTIONS.ACTION_USE;
            }
            Types.ACTIONS action = getActionToDestination(stateObservation, playerPosition,goalPosition);
            if(action != null){
                return action;
            }
        }
        return null;
    }

    public Types.ACTIONS walkToPortal(StateObservation stateObservation, ElapsedCpuTimer elapsedTime){
        //System.out.println("Enter walk to Portal");
        portalsList = stateObservation.getPortalsPositions();
        if(portalsList != null){
            Vector2d playerPosition = stateObservation.getAvatarPosition();

            //upgrade later (return best index)
            Vector2d goalPosition = portalsList[0].get(0).position;

            //need to check that portal is not the key to win the game
            if(playerPosition.equals(goalPosition)){
                return null;
            }
            Types.ACTIONS action = getActionToDestination(stateObservation, playerPosition,goalPosition);
            if(action != null){
                return action;
            }

        }
        return null;
    }

    public Types.ACTIONS walkToNpc(StateObservation stateObservation, ElapsedCpuTimer elapsedTime){
        //System.out.println("Enter walk to NPC");
        npcsList = stateObservation.getNPCPositions();
        if(npcsList != null){
            Vector2d playerPosition = stateObservation.getAvatarPosition();
            //upgrade later (return best index)
            Vector2d goalPosition = npcsList[0].get(0).position;

            Types.ACTIONS action = getActionToDestination(stateObservation, playerPosition, goalPosition);
            if(action != null) {
                return action;
            }
        }
        return null;
    }

    //use path finder (A star algorithm to find the action to destination)
    public Types.ACTIONS getActionToDestination(StateObservation stateObs, Vector2d playerPos, Vector2d goalPos){
        //System.out.println("Passed before find path");
        Vector2d playerPosition = new Vector2d(playerPos);
        Vector2d goalPosition = new Vector2d(goalPos);
        playerPosition.mul(1.0 / block_size);
        goalPosition.mul(1.0 / block_size);

        ArrayList<Node> path = pathFinder.getPath(playerPosition, goalPosition);

        if(path != null) {
            //System.out.println("Passed after find path");
            Vector2d nextPath = path.get(0).position;
            Types.ACTIONS action  = getActionFromPosition(playerPosition, nextPath);

            //System.out.println(stateObservation.getAvailableActions().contains(Types.ACTIONS.ACTION_USE));
            //System.out.println(isAbleToAttack(stateObservation, action));

            //in case there is a wall block the way, or the NPC that agent need to kill
            if(isAbleToAttack(stateObservation, action)){
                action = Types.ACTIONS.ACTION_USE;
                prev_position = new Vector2d(current_position);
                current_position = new Vector2d(playerPos);
                prev_action = action;
                //System.out.println("H : " + action);
                return action;
            }

            //in case there is a trap or enemy that agent need to avoid
            StateObservation nextState = stateObs.copy();
            nextState.advance(action);

            if((nextState.isGameOver() && !nextState.isAvatarAlive()) || action == getOpositeMove(prev_move)){
                //updateObstracleTypes(nextState);
                //getActionToDestination(stateObs, playerPos, goalPos);

                action = actRandom(stateObs,action);
                prev_position = new Vector2d(current_position);
                current_position = new Vector2d(playerPos);
                prev_action = action;
                prev_move = action;
                //System.out.println("R : " + action);
                return action;
            }

            prev_position = new Vector2d(current_position);
            current_position = new Vector2d(playerPos);
            prev_action = action;
            prev_move = action;
            //System.out.println("A : " + action);
            return action;
        }
        //System.out.println("Passed after cannot find path");
        return null;
    }

    public Types.ACTIONS getOpositeMove(Types.ACTIONS act){
        if(act == Types.ACTIONS.ACTION_UP){
            return Types.ACTIONS.ACTION_DOWN;
        }
        if(act == Types.ACTIONS.ACTION_DOWN){
            return Types.ACTIONS.ACTION_UP;
        }
        if(act == Types.ACTIONS.ACTION_LEFT){
            return Types.ACTIONS.ACTION_RIGHT;
        }
        if(act == Types.ACTIONS.ACTION_RIGHT){
            return Types.ACTIONS.ACTION_LEFT;
        }
        return Types.ACTIONS.ACTION_NIL;
    }

    //update Obstracle Types to path finder(in case found TRAP or Enemy)
    public void updateObstracleTypes(StateObservation nextState){
        //this code use to set new Path Finder
        //but it is not good enough because something this methods block the agent path
        //the agent may not be able to find path to the destination
        /*
        Vector2d player_pos = nextState.getAvatarPosition();
        player_pos.mul(1.0 / nextState.getBlockSize());

        ArrayList<Integer> obstracleTypes = new ArrayList<>();
        int type = nextState.getObservationGrid()[(int)player_pos.x][(int)player_pos.y].get(0).itype;

        obstracleTypes.add(stateObservation.getObservationGrid()[0][0].get(0).itype);
        obstracleTypes.add(type);
        System.out.println(obstracleTypes.size());
        pathFinder = new PathFinder(obstracleTypes);
        pathFinder.run(stateObservation);
        */
    }

    public Types.ACTIONS actRandom(StateObservation stateObs, Types.ACTIONS die_action){
        ArrayList<Types.ACTIONS> actions_list = stateObs.getAvailableActions();
        actions_list.remove(die_action);
        actions_list.remove(getOpositeMove(prev_move));
        if(actions_list.contains(Types.ACTIONS.ACTION_USE)){
            actions_list.remove(Types.ACTIONS.ACTION_USE);
        }

        int index = randomGenerator.nextInt(actions_list.size());
        Types.ACTIONS action = actions_list.get(index);

        StateObservation nextState = stateObs.copy();
        nextState.advance(action);

        Vector2d curPosition = stateObservation.getAvatarPosition();
        Vector2d nextPosition = nextState.getAvatarPosition();

        int count_iteration = 0;

        while((nextState.isGameOver() && !nextState.isAvatarAlive()) || curPosition.equals(nextPosition)) {
            actions_list.remove(action);
            index = randomGenerator.nextInt(actions_list.size());
            action = actions_list.get(index);

            nextState = stateObs.copy();
            nextState.advance(action);
            nextPosition = nextState.getAvatarPosition();
            count_iteration++;
            if(count_iteration > 20){
                break;
            }
        }
        return action;
    }

    //get action from vector of the next step
    public Types.ACTIONS getActionFromPosition(Vector2d playerPos, Vector2d goalPos){
        Vector2d moveVector = new Vector2d(goalPos.x - playerPos.x, goalPos.y - playerPos.y);
        return Types.ACTIONS.fromVector(moveVector);
    }

    //use to check that be able to attack or not
    public boolean isAbleToAttack(StateObservation stateObservation, Types.ACTIONS nextAct){
        StateObservation nextState = stateObservation.copy();
        nextState.advance(nextAct);
        //System.out.println(actionCausedDeath(nextState));
        //System.out.println(actionNoMoving(stateObservation));
        if(actionCausedDeath(nextState) || actionNoMoving(stateObservation)){
            return true;
        }
        return false;
    }

    //activate action move but player death in next action = attack enemy
    public boolean actionCausedDeath(StateObservation nextStateObservation){
        //System.out.println("------------next step died--------------");
        return (nextStateObservation.isGameOver() && nextStateObservation.getGameWinner() == Types.WINNER.PLAYER_LOSES);
    }


    //activate action move but player sprite not move = attack the immovable wall(need to destroy)
    public boolean actionNoMoving(StateObservation stateObservation){
        //System.out.println(current_position);
        //System.out.println(prev_position);
        return current_position.equals(prev_position) && prev_action != Types.ACTIONS.ACTION_USE;
    }


    //core function to return action to controller
    @Override
    public Types.ACTIONS run(StateObservation stateObservation, ElapsedCpuTimer elapsedTime) {

        Types.ACTIONS action = walkToItem(stateObservation, elapsedTime);
        if (action != null) {
            //System.out.println("Game Tick return walk to item");
            return action;
        }

        action = walkToPortal(stateObservation, elapsedTime);
        //System.out.println(action);
        if (action != null) {
            //System.out.println("Game Tick return walk to portal");
            return action;
        }

        action = walkToNpc(stateObservation, elapsedTime);
        if (action != null) {
            //System.out.println("Game Tick return walk to NPC");
            return action;
        }
        //System.out.println("Game Tick");

        return null;
    }
}