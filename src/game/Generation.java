package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Generation {
	private ArrayList<Individual> population = new ArrayList<Individual>();
	private int populationSize = 20;
	private int matingPoolSize = 1; 
	private int generationNo;

	Generation(int generationNo) {
		this.setGenerationNo(generationNo);
		for (int i = 0; i < populationSize; i++) {
			population.add(new Individual());
		}
	}

	Generation(ArrayList<Individual> matingPool, int generationNo) {
		this.setGenerationNo(generationNo);
		Individual parent = matingPool.get(0);
		for(int i=0;i<this.populationSize; i++){
			population.add(new Individual(parent.getSolution(),parent.getFitnessIndex()));
		}
		
	}

	public ArrayList<Individual> getPopulation() {
		return population;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Individual i : this.population) {
			sb.append(i);
		}
		return sb.toString();
	}

	public Individual getFittestIndividual() {
		System.out.println("BEFORE:" + this.population);
		Collections.sort(this.population);
		Collections.reverse(this.population);
		System.out.println("AFTER: " + this.population);
		System.out.println("FITTEST:" + this.population.get(0));
		return this.population.get(0);
	}

	public ArrayList<Individual> getMatingPool() {
		Collections.sort(this.population);
		Collections.reverse(this.population); //descending order by fitness value so that fittest individual is first
		ArrayList<Individual> matingPool = new ArrayList<Individual>();
		for (int i = 0; i < this.matingPoolSize; i++) {
			matingPool.add(this.population.get(i));
		}
		return matingPool;
	}

	public int getGenerationNo() {
		return generationNo;
	}

	public void setGenerationNo(int generationNo) {
		this.generationNo = generationNo;
	}
}
