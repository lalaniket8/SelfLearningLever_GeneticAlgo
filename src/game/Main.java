package game;

import java.util.Random;

public class Main {
	public static void main(String[] args) {
		GASimulator g = new GASimulator();
		GASimulatorPanel.initGASimulatorPanel(g);
		g.run();
		
		/*Random random = new Random();
		int a;
		
		for(int i = 0; i<1000; i++){
			 a = random.nextInt(1000) - 500;
			System.out.println(a);
		}*/
		
	}
}