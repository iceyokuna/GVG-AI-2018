package crazypet;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import crazypet.heuristic.MyHeuristic;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA. User: Parin
 * The implementation of the algorithm based on SampleGA on gvgai.net
 */

public class GeneticPlayer extends AbstractPlayer {

    private double GAMMA = 0.90;
    private long BREAK_MS = 15;
    private int SIMULATION_DEPTH = 7;
    private int POPULATION_SIZE = 5;

    private double RECPROB = 0.1;
    private double MUTATION_RATE = (1.0 / SIMULATION_DEPTH);
    private final int N_ACTIONS;

    private ElapsedCpuTimer timer;

    private Genome genomes[][][];
    private final HashMap<Integer, Types.ACTIONS> actionsToInt;
    private final HashMap<Types.ACTIONS, Integer> intToActions;
    protected Random randomGenerator;

    private MyHeuristic heuristic;

    public GeneticPlayer(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        randomGenerator = new Random();
        heuristic = new MyHeuristic();
        actionsToInt = new HashMap<>();
        intToActions = new HashMap<>();
        int i = 0;

        for (Types.ACTIONS action : stateObs.getAvailableActions()) {
            actionsToInt.put(i, action);
            intToActions.put(action, i);
            i++;
        }

        N_ACTIONS = stateObs.getAvailableActions().size();
        initGenomes();
    }

    private void initGenomes() {
        genomes = new Genome[POPULATION_SIZE][SIMULATION_DEPTH][N_ACTIONS];

        // Randomize initial genome
        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int j = 0; j < SIMULATION_DEPTH; j++) {
                for (int k = 0; k < N_ACTIONS; k++) {
                    genomes[i][j][k] = new Genome(randomGenerator.nextInt(N_ACTIONS));
                }
            }
        }
    }

    double select(Genome[][] actionGenome, StateObservation stateObs) throws TimeoutException {
        int rand_a, rand_b;

        // make sure random numbers are unique
        rand_a = (int) ((POPULATION_SIZE - 1) * randomGenerator.nextDouble());
        do {
            rand_b = (int) ((POPULATION_SIZE - 1) * randomGenerator.nextDouble());
        } while (rand_a == rand_b);

        double fitness_a = calcFitness(stateObs, actionGenome[rand_a]);
        double fitness_b = calcFitness(stateObs, actionGenome[rand_b]);

        if (fitness_a > fitness_b) {
            mutate(actionGenome, rand_a, rand_b);
        } else {
            mutate(actionGenome, rand_b, rand_a);
        }

        return Math.max(fitness_a, fitness_b);
    }

    private void mutate(Genome[][] actionGenome, int win, int lose){
        for (int i = 0; i < actionGenome[0].length; i++) {
            double rand = randomGenerator.nextDouble();
            if (rand < MUTATION_RATE) {
                actionGenome[lose][i] = actionGenome[win][i];
            } else if (rand < RECPROB) {
                actionGenome[lose][i] = new Genome(randomGenerator.nextInt(N_ACTIONS));
            }
        }
    }

    private double calcFitness(StateObservation stateObs, Genome[] policy) throws TimeoutException {
        long remaining = timer.remainingTimeMillis();
        int depth = 0;
        stateObs = stateObs.copy();

        if (remaining < BREAK_MS) {
            throw new TimeoutException();
        }

        // Simulate action to find depth
        for (; depth < policy.length; depth++) {
            Types.ACTIONS action = actionsToInt.get(policy[depth].getVal());
            stateObs.advance(action);
            if (stateObs.isGameOver()) {
                break;
            }
        }

        return Math.pow(GAMMA, depth) * heuristic.evaluateState(stateObs);
    }

    private Types.ACTIONS microbial(StateObservation stateObs, int iterations) {
        double[] maxFitnesses = new double[stateObs.getAvailableActions().size()];

        for (int i = 0; i < maxFitnesses.length; i++) {
            maxFitnesses[i] = -10e7;
        }

        for (int i = 0; i < iterations; i++) {
            for (Types.ACTIONS action : stateObs.getAvailableActions()) {
                double fitness = 0;

                StateObservation stCopy = stateObs.copy();
                stCopy.advance(action);

                try {
                    fitness = select(genomes[intToActions.get(action)], stCopy) + randomGenerator.nextDouble() * 0.00001;
                } catch (TimeoutException e) {
                    return this.actionsToInt.get(Utils.argmax(maxFitnesses));
                }

                int int_act = this.intToActions.get(action);

                if (fitness > maxFitnesses[int_act]) {
                    maxFitnesses[int_act] = fitness;
                }
            }
        }

        return this.actionsToInt.get(Utils.argmax(maxFitnesses));
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.timer = elapsedTimer;

        try {
            return microbial(stateObs, 100);
        } catch (Exception err) {
            System.out.println(err);
            return Types.ACTIONS.ACTION_NIL;
        }
    }
}
