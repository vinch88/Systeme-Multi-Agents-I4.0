package app.agents;

import javax.swing.JPanel;

import app.gui.JPanelPresse;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgentPresse extends Agent {

	protected void setup() {
		System.out.println("Démarrage de " + getLocalName());
		// Make this agent terminate
		// doDelete();

		Object[] args = getArguments();
		panelPresse = (JPanelPresse) args[0];

		RegisterToDsf("presse", panelPresse);

		addBehaviour(new OfferRequestsServer());

		addBehaviour(new PurchaseOrdersServer());

		addBehaviour(new ChargingDischargingServer());

	}

	protected void takeDown() {
		try {
			DFService.deregister(this);
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

	private void RegisterToDsf(String type, JPanel panelPresse) {
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
			((JPanelPresse) panelPresse).setStatut(getLocalName() + " failed to register to dfs");
		}
	}

	private Boolean initializeConnection() {
		// System.out.println("Trying to connect to Synapxis");
		panelPresse.setStatut(getLocalName() + " trying to connect to Twincat");

		try {
			panelPresse.setStatut(getLocalName() + " connected to Twincat");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Fail to connect to Synapxis");
			panelPresse.setStatut(getLocalName() + " fail to connect to Twincat");
			doDelete();
			return false;
		}

	}

	private Boolean closeConnection() {

		return null;
	}

	private String sendMessageToSynapxis(String message) {

		return messageFromTwincat;
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private String messageFromTwincat;
	private JPanelPresse panelPresse;

	/*------------------------------------------------------------------*\
	|*								Behaviours							*|
	\*------------------------------------------------------------------*/

	/**
	 * Inner class OfferRequestsServer. This is the behaviour used by Machine to
	 * serve incoming requests for offer from Robot.
	 */
	private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
			// mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			// msg = myAgent.receive(mt);
			// if (msg != null) {
			// // CFP Message received. Process it
			// message = msg.getContent();
			// reply = msg.createReply();
			// if (getIsChambreVide()) {
			// // La chambre de la machine est vide
			// System.out.println("La chambre de " + getLocalName() + " est
			// vide");
			// String strVide = "vide";
			// reply.setPerformative(ACLMessage.PROPOSE);
			// reply.setContent(strVide);
			// } else {
			// if (getIsWorking()) {
			// // La chambre est entrain de travailler
			// reply.setPerformative(ACLMessage.REFUSE);
			// reply.setContent("en cours de travail");
			// } else {
			// if (!waitResponse) {
			// // La chambre n'est pas vide mais le travail est
			// // terminé
			// reply.setPerformative(ACLMessage.PROPOSE);
			// waitResponse = true;
			// reply.setContent("fini");
			// }
			// }
			// }
			//
			// myAgent.send(reply);
			// } else {
			// block();
			// }
		}
	} // End of inner class OfferRequestsServer

	/**
	 * Inner class PurchaseOrdersServer. This is the behaviour used by Machine
	 * to serve incoming offer acceptances from Robot.
	 */
	private class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			// mt =
			// MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			// msg = myAgent.receive(mt);
			// if (msg != null) {
			// // ACCEPT_PROPOSAL Message received. Process it
			// message = msg.getContent();
			// reply = msg.createReply();
			// if (message.equals("chargement")) {
			// chargerChambre();
			// reply.setPerformative(ACLMessage.INFORM);
			// waitResponse = false;
			//
			// } else {
			// if (message.equals("dechargement")) {
			// viderChambre();
			// reply.setPerformative(ACLMessage.INFORM);
			// } else {
			// reply.setPerformative(ACLMessage.FAILURE);
			// reply.setContent("machine non vide");
			// }
			//
			// }
			// myAgent.send(reply);
			// } else {
			// block();
			// }
		}
	} // End of inner class OfferRequestsServer

	/**
	 * Inner class ChargingDischargingServer. This is the behaviour used by
	 * Machine to serve incoming offer acceptances from Robot.
	 */
	private class ChargingDischargingServer extends CyclicBehaviour {
		public void action() {
			// mt =
			// MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			// msg = myAgent.receive(mt);
			// if (msg != null) {
			// // ACCEPT_PROPOSAL Message received. Process it
			// message = msg.getContent();
			// reply = msg.createReply();
			// if (message.equals("chargement")) {
			// chargerChambre();
			// reply.setPerformative(ACLMessage.INFORM);
			// waitResponse = false;
			//
			// } else {
			// if (message.equals("dechargement")) {
			// viderChambre();
			// reply.setPerformative(ACLMessage.INFORM);
			// } else {
			// reply.setPerformative(ACLMessage.FAILURE);
			// reply.setContent("machine non vide");
			// }
			//
			// }
			// myAgent.send(reply);
			// } else {
			// block();
			// }
		}
	} // End of inner class ChargingDischargingServer
}