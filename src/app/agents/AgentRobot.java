package app.agents;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import app.gui.JPanelRobot;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentRobot extends Agent {

	protected void setup() {
		System.out.println("Démarrage de " + getLocalName());
		// Make this agent terminate
		// doDelete();
		messageFromRobot = "";

		Object[] args = getArguments();
		panelRobot = (JPanelRobot) args[0];
		String addrIP = panelRobot.getAddrIP();
		int port = panelRobot.getPort();

		// Enregistrement de l'agent dans les pages jaunes
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("robot");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
			// System.out.println(getLocalName() + " failed to register to
			// dfs");
			panelRobot.setStatut(getLocalName() + " failed to register to dfs");

		}

		try {
			initializeConnection(InetAddress.getByName(addrIP), port);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			panelRobot.setStatut("Fail to convert IP address or port, please verify your entries");
			doDelete();
		}

		// Debug
		i = 0;
		message = "";
		test = true;
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				while (test) {

					try {
						Thread.sleep(10000);
						if (i % 2 == 0) {
							message = "chargement";
						} else {
							message = "dechargement";
						}
						sendMessageToSynapxis(message);
						if (i > 10)
							test = false;
						i++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		});
		t1.start();

		addBehaviour(new TickerBehaviour(this, 1000) {

			@Override
			protected void onTick() {

				// Update de la liste des presses
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("presse");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					// System.out.println("Le robot: " + getLocalName() + " a
					// trouvé les agents presse suivants:");
					// listAgentsPresse = new AID[result.length];
					// for (int i = 0; i < result.length; ++i) {
					// listAgentsPresse[i] = result[i].getName();
					// // System.out.println(listAgentsPresse[i].getName());
					// }
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}

				// Update de la liste des machines
				sd.setType("machine");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					// System.out.println("Le robot: " + getLocalName() + " a
					// trouvé les agents machines suivants:");
					// listAgentsMachine = new AID[result.length];
					// for (int i = 0; i < result.length; ++i) {
					// listAgentsMachine[i] = result[i].getName();
					// // System.out.println(listAgentsMachine[i].getName());
					// }
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
				// Envoie la requête
				addBehaviour(new RequestPerformer());

			}
		});
	}

	protected void takeDown() {
		try {
			DFService.deregister(this);
			closeConnection();
		} catch (Exception e) {
		}
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private Boolean initializeConnection(InetAddress addr, int port) {
		// System.out.println("Trying to connect to Synapxis");
		panelRobot.setStatut(getLocalName() + " trying to connect to Synapxis");

		try {
			socket = new Socket(addr, port);
			inputMessageFromRobot = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outSendMessage = new PrintStream(socket.getOutputStream());
			// System.out.println("Connected to Synapxis");
			panelRobot.setStatut(getLocalName() + " connected to Synapxis");

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Fail to connect to Synapxis");
			panelRobot.setStatut(getLocalName() + " fail to connect to Synapxis");
			doDelete();
			return false;
		}

	}

	private Boolean closeConnection() {
		try {
			socket.close();
			// System.out.println("Connection closed");
			panelRobot.setStatut(getLocalName() + " connection close");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			// System.out.println("Connection close failed");
			panelRobot.setStatut(getLocalName() + " connection close failed");
			return false;
		}
	}

	private void sendMessageToSynapxis(String message) {
		outSendMessage.println(message);
		try {
			messageFromRobot = inputMessageFromRobot.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("FROM Synapxis: " + messageFromRobot);
		panelRobot.setStatut("FROM Synapxis: " + messageFromRobot);
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private String messageFromRobot;
	private Socket socket;
	private PrintStream outSendMessage;
	private BufferedReader inputMessageFromRobot;

	private JPanelRobot panelRobot;

	// debug
	private String message;
	private int i;
	private Boolean test;

	/*------------------------------------------------------------------*\
	|*								Behaviours							*|
	\*------------------------------------------------------------------*/

	/**
	 * Inner class RequestPerformer. This is the behaviour used by Robot agent
	 * to request if the machine is working or not.
	 */
	private class RequestPerformer extends Behaviour {
		private AID machineChambreVide; // La machine dont la chambre n'est pas
										// vide
		private AID machineFiniWork;
		private AID presseASoustraire;
		private String message;
		private MessageTemplate mtpresse; // The template to receive replies
		private MessageTemplate mtmachine; // The template to receive replies

		private int step;

		private ACLMessage cfp; // la demande
		private ACLMessage order; // l'ordre
		private ACLMessage reply; // la réponse

		public void action() {
			switch (step) {
			case 0:
				message = "isVide";
				// Envoie le cfp à toutes les machines à fabrication additives
				cfp = new ACLMessage(ACLMessage.CFP);
				// for (int i = 0; i < listAgentsMachine.length; ++i) {
				// cfp.addReceiver(listAgentsMachine[i]);
				// }
				cfp.setContent(message);
				cfp.setConversationId("machine");
				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																		// value
				// myAgent.send(cfp);
				// Prepare the template to get proposals
				mtmachine = MessageTemplate.and(MessageTemplate.MatchConversationId("machine"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				// panelAgent.setMessage(
				// "Le robot recherche une machine ne travaillant pas et dont la
				// chambre n'est pas vide");
				step = 1;
				break;
			}

		}

		@Override
		public boolean done() {
			if (step == 8) {
				step = 0;
			}
			return true;
		}
	} // End of inner class RequestPerformer

}
