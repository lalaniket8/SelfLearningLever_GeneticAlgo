package game;

import java.util.Random;

public class Individual implements Comparable<Individual> {
	private Random random = new Random();
	private double fitness;
	private int fitnessIndex;
	private final int solutionLen = 30;
	private double[] solution = new double[solutionLen]; // array of torque
	private static final double mutationProb = 0.15;
	
	
	Individual() {
		for (int i = 0; i < solutionLen; i++) {
			this.solution[i] = (random.nextFloat() * 1000) - 500;
		}

	}
	
	Individual(double[] solutionArray, int n) {
		int i;
		//copy first n torque values of passed solution array
		for (i = 0; i < n; i++){
			double randomDouble = random.nextDouble();
			if(randomDouble < mutationProb){
				this.solution[i] = random.nextInt(1000) - 500;
			}else{
				this.solution[i] = solutionArray[i];
			}
			
		}
		for (; i < solutionLen; i++) {
			this.solution[i] = random.nextInt(1000) - 500;
		}

	}

	Individual(double[] solution) {
		this.solution = solution;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (double d : this.solution) {
			sb.append(d + ",");
		}
		return "[" + sb + ":::" + fitness + "][" + fitnessIndex + "]";
	}

	public double[] getSolution() {
		return solution;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	@Override
	public int compareTo(Individual o) {
		return (int) (this.getFitness() * 10000) - (int) (((Individual) o).getFitness() * 10000);
	}

	public int getFitnessIndex() {
		return fitnessIndex;
	}

	public void setFitnessIndex(int fitnessIndex) {
		this.fitnessIndex = fitnessIndex;
	}

}
