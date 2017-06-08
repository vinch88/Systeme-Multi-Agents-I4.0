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

import javax.swing.JPanel;

import app.gui.JPanelRobot;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentRobot extends Agent {

	protected void setup() {
		messageFromRobot = "";

		Object[] args = getArguments();
		panelRobot = (JPanelRobot) args[0];
		String addrIP = panelRobot.getAddrIP();
		int port = panelRobot.getPort();

		panelRobot.setStatut("Starting" + getLocalName());

		// Registery not obligatory in this case
		RegisterToDsf("robot", panelRobot);

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

		// Add a CyclicBehaviour
		addBehaviour(new RequestPerformer());
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

	private void RegisterToDsf(String type, JPanel panelRobot) {
		// Register the agent into the dsf
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
			// System.out.println(getLocalName() + " failed to register to
			// dfs");
			((JPanelRobot) panelRobot).setStatut(getLocalName() + " failed to register to dfs");
		}

	}

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

	private String sendMessageToSynapxis(String message) {
		outSendMessage.println(message);
		try {
			messageFromRobot = inputMessageFromRobot.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("FROM Synapxis: " + messageFromRobot);
		panelRobot.setStatut("FROM Synapxis: " + messageFromRobot);
		return messageFromRobot;
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private String messageFromRobot;
	private Socket socket;
	private PrintStream outSendMessage;
	private BufferedReader inputMessageFromRobot;
	private AID[] listAgentsPresse;
	private AID agentPresse;
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
	private class RequestPerformer extends CyclicBehaviour {

		public void action() {

			// Update the list of presses
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("presse");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				// System.out.println("Le robot: " + getLocalName() + " a trouvé
				// les agents presse suivants:");
				listAgentsPresse = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					listAgentsPresse[i] = result[i].getName();
					// System.out.println(listAgentsPresse[i].getName());
				}
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}

			step = 0;

			switch (step) {
			case 0:

				messageBehaviour = "isWorking"; // ask all the press if they are
												// working
				agentPresse = null;
				makeAndSendCfp(listAgentsPresse, "presse", messageBehaviour, mtpresse);
				step = 1;
				break;
			case 1:
				// Get all the responses from the press
				reply = myAgent.receive(mtpresse);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer
						agentPresse = reply.getSender();
						step = 2;
					}
					repliesCnt++;
					if (repliesCnt >= listAgentsPresse.length) {
						// We received all replies and don't have any
						// proposition
						repliesCnt = 0;
						step = 5;
					}
				} else {
					block();
				}
				break;
			case 2:
				messageBehaviour = "isFull"; // ask to the press if it is full
				makeAndSendCfp(agentPresse, "presse", messageBehaviour, mtpresse);
				step = 3;
				break;
			case 3:
				// Get the response from the press
				reply = myAgent.receive(mtpresse);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer
						if (reply.getContent().equals("full")) {
							// The machine is full
							messageBehaviour = "dechargement"; // send to the
																// press that it
																// have to
																// discharge
							makeAndSendAcceptProposal(agentPresse, "presse", messageBehaviour, mtpresse);
						} else {
							if (reply.getContent().equals("empty")) {
								// The machine is empty
								messageBehaviour = "chargement"; // send to the
																	// press
																	// that it
																	// have to
																	// charge
								makeAndSendAcceptProposal(agentPresse, "presse", messageBehaviour, mtpresse);
							} else {
								// Got an unexpected message, we refuse the
								// proposal
								messageBehaviour = "refuse";
								makeAndSendRejectProposal(agentPresse, "presse", messageBehaviour);
								repliesCnt = 0;
								step = 5;
							}
						}
					}
					repliesCnt++;
					if (repliesCnt > 0) {
						// We received the reply
						repliesCnt = 0;
						step = 4;
					}
				} else {
					block();
				}
				break;
			case 4:
				// Receive the purchase order reply
				reply = myAgent.receive(mtpresse);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can start the robot and say
						// to the press when the job is done
						if (messageBehaviour.equals("chargement") || messageBehaviour.equals("dechargement")) {
							String response = sendMessageToSynapxis(messageBehaviour);
							while (response == null) {
								// wait the response from Synapxis
							}
							// job done, send a message to the press that it can
							// start is job if it has to be
							sendMessageToAgent(agentPresse, messageBehaviour + " done");

						}
					} else {
						System.out.println("Attempt failed: the robot didn't " + messageBehaviour);
					}

					step = 5;
				} else {
					block();
				}
				break;
			}

		}

		/*------------------------------------------------------------------*\
		|*						Attributs Private Behaviour					*|
		\*------------------------------------------------------------------*/
		private String messageBehaviour;
		private MessageTemplate mtpresse; // The template to receive replies
		private ACLMessage cfp; // The demand
		private ACLMessage order; // The order
		private ACLMessage reply; // The response
		private int repliesCnt = 0; // The counter of replies from agents

		private int step; // The current step of this behaviour

		/*------------------------------------------------------------------*\
		|*						Methodes Private Behavious					*|
		\*------------------------------------------------------------------*/

		private void makeAndSendCfp(AID[] listAgent, String convID, String message, MessageTemplate template) {

			cfp = new ACLMessage(ACLMessage.CFP);
			for (int i = 0; i < listAgent.length; ++i) {
				cfp.addReceiver(listAgent[i]);
			}
			cfp.setContent(message);
			cfp.setConversationId(convID);
			cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																	// value
			myAgent.send(cfp);
			// Prepare the template to get proposals
			template = MessageTemplate.and(MessageTemplate.MatchConversationId(convID),
					MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
		}

		private void makeAndSendCfp(AID agent, String convID, String message, MessageTemplate template) {

			cfp = new ACLMessage(ACLMessage.CFP);
			cfp.addReceiver(agent);
			cfp.setContent(message);
			cfp.setConversationId(convID);
			cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																	// value
			myAgent.send(cfp);
			// Prepare the template to get proposals
			template = MessageTemplate.and(MessageTemplate.MatchConversationId(convID),
					MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
		}

		private void makeAndSendAcceptProposal(AID agent, String convID, String message, MessageTemplate template) {
			order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			order.addReceiver(agent);
			order.setContent(message);
			order.setConversationId(convID);
			order.setReplyWith("order" + System.currentTimeMillis());
			myAgent.send(order);
			// Préparation du template pour obtenir la réponse de la demande
			template = MessageTemplate.and(MessageTemplate.MatchConversationId(convID),
					MessageTemplate.MatchInReplyTo(order.getReplyWith()));
		}

		private void makeAndSendRejectProposal(AID agent, String convID, String message) {
			ACLMessage order = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
			order.addReceiver(agent);
			order.setContent(message);
			order.setConversationId(convID);
			order.setReplyWith("order" + System.currentTimeMillis());
			myAgent.send(order);
		}

		private void sendMessageToAgent(AID agent, String content) {
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.addReceiver(agent);
			message.setContent(content);
			send(message);
		}

	} // End of inner class RequestPerformer

}
