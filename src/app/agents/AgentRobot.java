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
import app.gui.JPanelSuiviProd;
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

	/**
	 * Starting the AgentRobot
	 */
	protected void setup() {
		messageFromRobot = "";

		Object[] args = getArguments();
		panelRobot = (JPanelRobot) args[0];
		panelSuiviProd = (JPanelSuiviProd) args[1];
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

		// Add a CyclicBehaviour
		addBehaviour(new RequestPerformer());
	}

	/**
	 * Stoping the AgentRobot
	 */
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

	/**
	 * Method for registering the agentRobot to the dsf
	 * 
	 * @param type
	 * @param panelRobot
	 */
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
			((JPanelRobot) panelRobot).setStatut(getLocalName() + " failed to register to dfs");
		}

	}

	/**
	 * Method for initialize a TCP connection
	 * 
	 * @param addr
	 * @param port
	 * @return
	 */
	private Boolean initializeConnection(InetAddress addr, int port) {
		panelRobot.setStatut(getLocalName() + " trying to connect to Synapxis");

		try {
			socket = new Socket(addr, port);
			inputMessageFromRobot = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outSendMessage = new PrintStream(socket.getOutputStream());
			panelRobot.setStatut(getLocalName() + " connected to Synapxis");

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			panelRobot.setStatut(getLocalName() + " fail to connect to Synapxis");
			doDelete();
			return false;
		}

	}

	/**
	 * Method for closing a TCP connection
	 */
	private Boolean closeConnection() {
		try {
			sendMessageToSynapxis("disconnect");
			socket.close();
			panelRobot.setStatut(getLocalName() + " connection close");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			panelRobot.setStatut(getLocalName() + " connection close failed");
			return false;
		}
	}

	/**
	 * Method for sending a message to the Synapxis robot
	 * 
	 * @param message
	 * @return Message from synpaxis
	 */
	private String sendMessageToSynapxis(String message) {
		System.out.println(message);
		outSendMessage.println(message);
		try {
			messageFromRobot = inputMessageFromRobot.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	private JPanelSuiviProd panelSuiviProd;

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
				listAgentsPresse = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					listAgentsPresse[i] = result[i].getName();
				}
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}

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
					repliesCnt++;
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						if (reply.getContent().equals("is not working")) {
							// This is an offer
							agentPresse = reply.getSender();
							// we refuse this proposal, we want to know if this
							// press is full or not
							makeAndSendRejectProposal(agentPresse, "presse", "refuse");
							step = 2;
						}
					}
					if (repliesCnt >= listAgentsPresse.length && step != 2) {
						// We received all replies and don't have any
						// proposition
						repliesCnt = 0;
						step = 5;
						panelRobot.setStatut("All presses are working");
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
							panelRobot.setStatut(messageBehaviour + " en cours");
						} else {
							if (reply.getContent().equals("empty")) {
								// The machine is empty
								messageBehaviour = "chargement"; // send to the
																	// press
																	// that it
																	// have to
																	// charge
								makeAndSendAcceptProposal(agentPresse, "presse", messageBehaviour, mtpresse);
								panelRobot.setStatut(messageBehaviour + " en cours");
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
							panelRobot.setStatut(response);
							// job done, send a message to the press that it can
							// start is job if it has to be
							sendMessageToAgent(agentPresse, response);
							if (response.equals("dechargement done")) {
								panelSuiviProd.addPiece();
							}

						}
					} else {
						panelRobot.setStatut("Attempt failed: the robot didn't " + messageBehaviour);
					}
					step = 5;
				} else {
					block();
				}
				break;
			case 5:
				step = 0;
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

		private int step = 0; // The current step of this behaviour

		/*------------------------------------------------------------------*\
		|*						Methodes Private Behavious					*|
		\*------------------------------------------------------------------*/

		/**
		 * Method for create and send a CFP
		 */
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

		/**
		 * Method for create and send a CFP
		 */
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

		/**
		 * Method for create and send an Accept-Proposal
		 */
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

		/**
		 * Method for create and send an Reject-Proposal
		 */
		private void makeAndSendRejectProposal(AID agent, String convID, String message) {
			ACLMessage order = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
			order.addReceiver(agent);
			order.setContent(message);
			order.setConversationId(convID);
			order.setReplyWith("order" + System.currentTimeMillis());
			myAgent.send(order);
		}

		/**
		 * Method for sending a message to an agent
		 */
		private void sendMessageToAgent(AID agent, String content) {
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.addReceiver(agent);
			message.setContent(content);
			send(message);
		}

	} // End of inner class RequestPerformer

}
