package app;
import jade.Boot;
import jade.core.Runtime;

public class UseSystemeMultiAgents {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		// D�marrage de jade
		Boot b = new Boot();
		args = new String[2];
		// D�marrage du gui de jade
		args[0] = "-gui";
		// D�marrage sans le gui de jadeF
		// args[0] = "-agents";
		args[1] = "GestionUsine:app.gestion.GestionUsine";
		b.main(args);
		
		
	}
}
