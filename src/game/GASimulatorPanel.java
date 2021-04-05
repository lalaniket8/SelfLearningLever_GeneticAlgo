package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class GASimulatorPanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static GASimulator simulator;
	private static final int totalNoOfGenerations = 50;
	private static final long TIMEPERIOD = 250;
	private static Individual soln;

	public static void initGASimulatorPanel(GASimulator simulator1) {
		simulator = simulator1;
		JFrame f = new JFrame("GA Simulator Panel");
		JTextField t1, t2;
		t1 = new JTextField("1");
		t1.setBounds(50, 10, 200, 20);
		t2 = new JTextField("1");
		t2.setBounds(50, 40, 200, 20);
		JButton b = new JButton("Simulate");
		b.setBounds(50, 70, 200, 20);
		JButton b1 = new JButton("Solution");
		b1.setBounds(50, 100, 200, 20);
		/*JButton b2 = new JButton("fittest");
		b2.setBounds(50, 130, 200, 20);
		JButton b3 = new JButton("nextGen");
		b3.setBounds(50, 160, 200, 20);*/
		f.add(t1);
		f.add(t2);
		f.add(b);
		f.add(b1);
		//f.add(b2);
		//f.add(b3);

		f.setSize(400, 400);
		f.setLayout(null);
		f.setVisible(true);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				soln = startGAEngine();
			}

		});

		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("SOLUTION:"+soln);
				simulateIndividual(soln,0);
			}
		});

		/*b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});

		b3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
*/
	}

	public static void simulateGeneration(Generation generation) {
		for (int i = 0; i < generation.getPopulation().size(); i++) {
			Individual ind = generation.getPopulation().get(i);

			simulateIndividual(ind, i);

		}
		System.out.println("GEN SIMULATION DONE:"+generation.getGenerationNo());
	}

	private static void simulateIndividual(Individual individual, int i2) {
		// System.out.println("Simulating ind:"+i2);
		double[] solution = individual.getSolution();
		simulator.reset();
		synchronized (simulator) {
			try {
				int i = 0;
				simulator.wait(TIMEPERIOD);
				for (i = 0; i < solution.length; i++) {
					if (!simulator.isBallFallen()) {
						simulator.simulate(solution[i]);
						simulator.wait(TIMEPERIOD);
					} else {
						break;
					}
				}
				long fitness = simulator.stopStepTimer();
				individual.setFitness(fitness);
				individual.setFitnessIndex(i - 1);
				System.out.println("Individual " + i2 + " simulation complete. Fitness:" + individual.getFitness());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	private static Individual startGAEngine() {

		int generationNo = 1;
		Individual solution = null;
		
		//calculating first generation randomly
		Generation currentGen = new Generation(generationNo);
		while (generationNo <= totalNoOfGenerations) {
			System.out.println("Starting simulation for gen:"+currentGen.getGenerationNo());
			simulateGeneration(currentGen);
			generationNo++;
			ArrayList<Individual> matingPool = currentGen.getMatingPool();
			solution = matingPool.get(0);
			// calculating next generation from current generations mating pool
			currentGen = new Generation(matingPool, generationNo);

		}
		
		//return fittest individual of latest generation as solution
		return solution;
		
	}
}
