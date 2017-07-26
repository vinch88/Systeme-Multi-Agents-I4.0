package app.gestion;

import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JPanel;

import app.gui.JFrameCopyright;
import app.gui.JFrameUI;
import app.gui.JPanelPresse;
import app.gui.JPanelRobot;
import app.gui.JPanelSuiviProd;
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

	/**
	 * @param liste
	 *            of agent
	 */
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

	/**
	 * @param list
	 *            of agent
	 * @param nbAgent
	 *            Number of agent
	 * @return the list
	 */
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

	/**
	 * @param nomAgent
	 * @param typeAgent
	 * @return AgentController
	 */
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

	/**
	 * @param nomAgent
	 * @param typeAgent
	 * @param panelRobot
	 * @return The agent controller
	 */
	public AgentController createAgent(String nomAgent, String typeAgent, JPanelRobot panelRobot,
			JPanelSuiviProd panelSuiviProd) {

		try {
			Object[] args = new Object[3];
			args[0] = panelRobot;
			args[1] = panelSuiviProd;

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

	/**
	 * @param nomAgent
	 * @param typeAgent
	 * @param panelPresse
	 * @return the agent controller
	 */
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

	public void killAgent(AgentController agent) {
		try {

			agent.kill();
			countAgent--;

		} catch (StaleProxyException e) {
			System.out.println("Error killing agent");
		}

	}

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/
	/**
	 * @return the number of agent
	 */
	public int getCountAgent() {
		return countAgent;
	}

	/**
	 * @return the agent controller
	 */
	public AgentController getAgentController() {
		return t1;
	}

	/**
	 * @return the agent container
	 */
	public AgentContainer getAgentContainer() {
		return ac;
	}

	/**
	 * @return the runtime
	 */
	public Runtime getRuntime() {
		return rt;
	}

	/**
	 * @return the profile implementation
	 */
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
