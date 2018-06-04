package crazypet.heuristic;

//Required Imports
import core.game.StateObservation;
import tools.ElapsedCpuTimer;
import ontology.Types;

//Agent-Dependent Imports
import java.util.Random;
import java.util.ArrayList;

abstract public class GameAgent {
    protected StateObservation stateObservation;
    protected ElapsedCpuTimer elapsedTime;

    public GameAgent(StateObservation stateObservation, ElapsedCpuTimer elapsedTime){
        this.stateObservation = stateObservation;
        this.elapsedTime = elapsedTime;
    }

    abstract public Types.ACTIONS run(StateObservation stateObservation, ElapsedCpuTimer elapsedTime);


}
