package app.gestion;

import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JPanel;

import app.gui.JFrameCopyright;
import app.gui.JFrameUI;
import app.gui.JPanelPresse;
import app.gui.JPanelRobot;
import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.tools.sniffer.PanelCanvas;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class GestionUsine extends Agent {

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	protected void setup() {

		System.out.println("Démarrage de jade");
		new JFrameCopyright(this);

		// Get a hold on JADE runtime
		rt = Runtime.instance();
		// Create a default profile
		p = new ProfileImpl(false);
		// Create a new non-main container, connecting to the default
		// main container (i.e. on this host, port 1099)
		ac = rt.createAgentContainer(p);

		// Make this agent terminate
		doDelete();

	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void cleanListeAgent(LinkedList<AgentController> liste) {
		while (!liste.isEmpty()) {

			try {
				t1 = liste.getLast();
				t1.kill();
				liste.removeLast();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public LinkedList<AgentController> killAgent(LinkedList<AgentController> liste, int nbAgent) {

		try {
			countAgent = nbAgent;
			t1 = liste.getLast();
			t1.kill();
			liste.removeLast();
			countAgent--;

		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return liste;

	}

	public AgentController createAgent(String nomAgent, String typeAgent) {

		try {

			t1 = ac.createNewAgent(nomAgent, typeAgent, null);
			// fire-up the agent
			t1.start();
			countAgent++;

		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t1;
	}

	public AgentController createAgent(String nomAgent, String typeAgent, JPanelRobot panelRobot) {

		try {
			Object[] args = new Object[3];
			args[0] = panelRobot;

			t1 = ac.createNewAgent(nomAgent, typeAgent, args);
			// fire-up the agent
			t1.start();
			countAgent++;

		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t1;
	}

	public AgentController createAgent(String nomAgent, String typeAgent, JPanelPresse panelPresse) {

		try {
			Object[] args = new Object[3];
			args[0] = panelPresse;

			t1 = ac.createNewAgent(nomAgent, typeAgent, args);
			// fire-up the agent
			t1.start();
			countAgent++;

		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t1;
	}

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/
	public int getCountAgent() {
		return countAgent;
	}

	public AgentController getAgentController() {
		return t1;
	}

	public AgentContainer getAgentContainer() {
		return ac;
	}

	public Runtime getRuntime() {
		return rt;
	}

	public ProfileImpl getProfileImple() {
		return p;
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private AgentContainer ac = null;
	private AgentController t1 = null;
	private Runtime rt;
	private ProfileImpl p;

	private int countAgent = 0;
}
