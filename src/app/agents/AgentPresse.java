package app.agents;

import javax.swing.JPanel;

import app.gui.JPanelPresse;
import de.beckhoff.jni.Convert;
import de.beckhoff.jni.JNIByteBuffer;
import de.beckhoff.jni.tcads.AdsCallDllFunction;
import de.beckhoff.jni.tcads.AmsAddr;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentPresse extends Agent {

	/**
	 * Starting the AgentPresse
	 */
	protected void setup() {

		isWorking = false;
		isFull = false;
		Object[] args = getArguments();
		panelPresse = (JPanelPresse) args[0];

		panelPresse.setStatut("Starting" + getLocalName());

		RegisterToDsf("presse", panelPresse);

		initializeConnection();

		addBehaviour(new OfferRequestsServer());

		addBehaviour(new PurchaseOrdersServer());

		addBehaviour(new ChargingDischargingServer());

	}

	/**
	 * Stoping the AgentPresse
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
	 * Method for registering the agentPresse to the dsf
	 */
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

			AmsAddr addr = new AmsAddr();
			JNIByteBuffer handleBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);
			JNIByteBuffer symbolBuff = new JNIByteBuffer(Convert.StringToByteArr("MAIN.PLCVar", true));
			JNIByteBuffer dataBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);

			// Open communication
			AdsCallDllFunction.adsPortOpen();
			AdsCallDllFunction.getLocalAddress(addr);
			addr.setPort(AdsCallDllFunction.AMSPORT_R0_PLC_RTS1);

			// Get handle by symbol name
			err = AdsCallDllFunction.adsSyncReadWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_HNDBYNAME, 0x0,
					handleBuff.getUsedBytesCount(), handleBuff, symbolBuff.getUsedBytesCount(), symbolBuff);
			if (err != 0) {
				System.out.println("Error: Get handle: 0x" + Long.toHexString(err));
			} else {
				System.out.println("Success: Get handle!");
			}

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

		// Close communication
		err = AdsCallDllFunction.adsPortClose();
		if (err != 0) {
			System.out.println("Error: Close Communication: 0x" + Long.toHexString(err));
			return false;
		}
		return true;

	}

	private String sendMessageToTwinCat(String message) {

		try {
			System.in.read();
		} catch (Exception e) {
			System.out.println("Error: Close program");
		}

		return messageFromTwincat;
	}

	private String changeModePresse() {
		return null;
	}

	private String startPresse() {

		return null;
	}

	private String resetPresse() {

		return null;
	}

	private String getNombrePieceFromPresse() {

		return null;
	}

	private void changerVitesse(int vitesse) {

	}

	private void startMoteur() {

	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private String messageFromTwincat;
	private JPanelPresse panelPresse;
	private Boolean isWorking;
	private Boolean isFull;
	private long err;

	/*------------------------------------------------------------------*\
	|*								Behaviours							*|
	\*------------------------------------------------------------------*/

	/**
	 * Inner class OfferRequestsServer. This is the behaviour used by Machine to
	 * serve incoming requests for offer from Robot.
	 */
	private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
			mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				messageBehaviour = msg.getContent();
				reply = msg.createReply();
				if (messageBehaviour.equals("isWorking")) {
					// The robot search one press who is not working
					if (!isWorking) {
						// The press is not working, can be charge or discharge
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent("is not working");
					} else {
						// The press is working, we refuse
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("is working");
					}

				}
				if (messageBehaviour.equals("isFull")) {
					if (isFull) {
						// The press is full, it has to be discharge
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent("full");
					} else {
						// The press is not full, it has to be charge
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent("empty");
					}
				}

				myAgent.send(reply);

				// debug
				System.out.println("Response to CFP send: " + reply);
			} else {
				block();
			}
		}

		/*------------------------------------------------------------------*\
		|*						Attributs Private Behaviour					*|
		\*------------------------------------------------------------------*/

		private MessageTemplate mt;
		private String messageBehaviour;
		private ACLMessage reply;
		private ACLMessage msg;

		/*------------------------------------------------------------------*\
		|*						Methodes Private Behavious					*|
		\*------------------------------------------------------------------*/

	} // End of inner class OfferRequestsServer

	/**
	 * Inner class PurchaseOrdersServer. This is the behaviour used by Machine
	 * to serve incoming offer acceptances from Robot.
	 */
	private class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			msg = myAgent.receive(mt);
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Process it
				messageBehaviour = msg.getContent();
				reply = msg.createReply();
				if (messageBehaviour.equals("chargement") || messageBehaviour.equals("dechargement")) {
					// The robot want to charge or discharge the press, we
					// inform it that it can and wait for his response
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(messageBehaviour);

				} else {
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("There was a problem during the charge/discharge event");

				}
				myAgent.send(reply);
				// debug
				System.out.println("Response to Accept-Proposal send: " + reply);
			} else {
				block();
			}
		}

		/*------------------------------------------------------------------*\
		|*						Attributs Private Behaviour					*|
		\*------------------------------------------------------------------*/

		private MessageTemplate mt;
		private String messageBehaviour;
		private ACLMessage reply;
		private ACLMessage msg;

		/*------------------------------------------------------------------*\
		|*						Methodes Private Behavious					*|
		\*------------------------------------------------------------------*/

	} // End of inner class OfferRequestsServer

	/**
	 * Inner class ChargingDischargingServer. This is the behaviour used by
	 * Machine to start his job when the Robot has done his.
	 */
	private class ChargingDischargingServer extends CyclicBehaviour {
		public void action() {
			mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			msg = myAgent.receive(mt);
			if (msg != null) {
				// We received a message from the robot - Process it
				messageBehaviour = msg.getContent();
				if (messageBehaviour.equals("chargement done")) {
					// The robot has charge this press we can start the job
					panelPresse.setStatut("Starting the job");

					isWorking = true;
					isFull = true;
					// debug
					String strMess = "Have to change this message when connexion with Twincat will be done";
					sendMessageToTwinCat(strMess);
					// when the job is done by twincat, we can put isWorking at
					// false

					Thread t1 = new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								Thread.sleep(5000);
								isWorking = false;
								panelPresse.setStatut("Job done");
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
					t1.start();

				}
				if (messageBehaviour.equals("dechargement done")) {
					// The robot has discharge this press

					Thread t1 = new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								Thread.sleep(2000);
								isFull = false;
								panelPresse.setStatut("The press is empty");
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
					t1.start();

					isFull = false;
					panelPresse.setStatut("The press is empty");
				}
			} else {
				block();
			}
		}
		/*------------------------------------------------------------------*\
		|*						Attributs Private Behaviour					*|
		\*------------------------------------------------------------------*/

		private MessageTemplate mt;
		private String messageBehaviour;
		private ACLMessage msg;

		/*------------------------------------------------------------------*\
		|*						Methodes Private Behavious					*|
		\*------------------------------------------------------------------*/

	} // End of inner class ChargingDischargingServer
}