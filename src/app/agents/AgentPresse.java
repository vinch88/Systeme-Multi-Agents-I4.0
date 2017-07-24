package app.agents;

import javax.swing.JPanel;

import app.gui.JPanelPresse;
import de.beckhoff.jni.Convert;
import de.beckhoff.jni.JNIByteBuffer;
import de.beckhoff.jni.tcads.AdsCallDllFunction;
import de.beckhoff.jni.tcads.AdsSymbolEntry;
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

		// Open connection to Twincat
		openConnection(panelPresse.getAddrIP(), panelPresse.getPort());

		// Click on the reset button of the press
		resetPress();

		// Set the automode of the press
		setModePressAuto();

		// Set the motor of the press on power
		setPowerOnMotor();

		// Set the speed of the press
		changeSpeed(11);
		changePosBasse(-50);
		resetNbPiece();

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

	public void openConnection(String addresse, int port) {

		addr = new AmsAddr();
		handleBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);
		dataBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);

		// AdsCallDllFunction.getLocalAddress(addr);
		addr.setNetIdStringEx(addresse);
		addr.setPort(port);

		// Open communication
		AdsCallDllFunction.adsPortOpen();

		panelPresse.setStatut("Connexion open");
		// System.out.println("Connexion open");

	}

	public Boolean closeConnection() {

		// Close communication
		err = AdsCallDllFunction.adsPortClose();
		if (err != 0) {
			panelPresse.setStatut("Error: Close Communication: 0x" + Long.toHexString(err));
			// System.out.println("Error: Close Communication: 0x" +
			// Long.toHexString(err));
			return false;
		}
		// System.out.println("Connexion closed");
		panelPresse.setStatut("Connexion closed");

		return true;

	}

	private void setModePressAuto() {
		writeBoolValue("GVL.fbStation.fbBtn.Auto.xEtat", true);
	}

	private void setPowerOnMotor() {
		writeBoolValue("GVL.fbStation.fbAxeX.fbPower.Enable", true);
	}

	private void startPress() {

		writeBoolValue("GVL.fbStation.fbBtn.HmiStart.xEtat", true);
	}

	private void resetPress() {

		writeBoolValue("GVL.fbStation.fbBtn.HmiReset.xEtat", true);
	}

	private void stopPress() {
		writeBoolValue("GVL.fbStation.fbBtn.HmiStop.xEtat", true);
	}

	private String getNbPieceFromPress() {

		return readValue("GVL.stParam.stStatus.iCounterPiecesProduites");
	}

	private void resetNbPiece() {
		writeIntValue("GVL.stParam.stStatus.iCounterPiecesProduites", 0);
	}

	private String getStatut() {
		return readValue("GVL.fbStation.fbCycleAuto.xBusy");
		// "GVL.fbStation.fbCycleAuto.xDone"
		// "GVL.fbStation.fbCycleAuto.xBusy"

	}

	private void setStatut(Boolean value) {
		writeBoolValue("GVL.fbStation.fbCycleAuto.xBusy", value);
	}

	private void changeSpeed(int speed) {
		writeIntValue("GVL.stParam.stRecipe.lrVitesse", speed);
	}

	private void changePosBasse(int position) {

		writeIntValue("GVL.stParam.stRecipe.lrPositionBasse", position);
	}

	private String getMotorPosition() {

		return readValue("GVL.stParam.stStatus.lrPositionMoteur");
	}

	public void releaseHandle() {

		// Release handle
		err = AdsCallDllFunction.adsSyncWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_RELEASEHND, 0,
				handleBuff.getUsedBytesCount(), handleBuff);

		if (err != 0) {
			panelPresse.setStatut("Error: Release Handle: 0x" + Long.toHexString(err));
			// System.out.println("Error: Release Handle: 0x" +
			// Long.toHexString(err));
		} else {
			// System.out.println("Success: Release Handle!");
		}
	}

	public String readValue(String variable) {

		setIGroupIOffAdsSymbolEntry(variable);

		JNIByteBuffer symbolBuff = new JNIByteBuffer(Convert.StringToByteArr(variable, true));
		dataBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);
		String val = "";

		// Get handle by symbol name
		err = AdsCallDllFunction.adsSyncReadWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_HNDBYNAME, 0x0,
				handleBuff.getUsedBytesCount(), handleBuff, symbolBuff.getUsedBytesCount(), symbolBuff);

		if (err != 0) {
			return "Error: Get handle: 0x" + Long.toHexString(err);

		} else {
			// System.out.println("Success: Get handle!");
		}

		// Handle: byte[] to int
		int hdlBuffToInt = Convert.ByteArrToInt(handleBuff.getByteArray());

		// Read value by handle
		err = AdsCallDllFunction.adsSyncReadReq(addr, AdsCallDllFunction.ADSIGRP_SYM_VALBYHND, hdlBuffToInt, 0x4,
				dataBuff);
		if (err != 0) {
			return "Error: Read by handle: 0x" + Long.toHexString(err);
		} else {

			val = Convert.ByteArrToInt(dataBuff.getByteArray()) + "";

			if (adsSymbolEntry.getType().equals("BOOL")) {
				if (Convert.ByteArrToInt(dataBuff.getByteArray()) != 0) {
					// System.out.println(Convert.ByteArrToInt(dataBuff.getByteArray())
					// + "=> True");
					val = "True";
				} else {
					// System.out.println(Convert.ByteArrToInt(dataBuff.getByteArray())
					// + "=> False");
					val = "False";
				}

			}
			if (adsSymbolEntry.getType().equals("STRING(50)")) {
				val = Convert.ByteArrToString(dataBuff.getByteArray()) + "";
			}
			// System.out.println("Success: " + adsSymbolEntry.getName() + "
			// value: " + val);
		}
		return val;
	}

	public void setIGroupIOffAdsSymbolEntry(String variable) {
		JNIByteBuffer symbolBuff = new JNIByteBuffer(Convert.StringToByteArr(variable, true));
		dataBuff = new JNIByteBuffer(0xFFFF);
		// Get variable declaration
		err = AdsCallDllFunction.adsSyncReadWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_INFOBYNAMEEX, 0,
				dataBuff.getUsedBytesCount(), dataBuff, symbolBuff.getUsedBytesCount(), symbolBuff);
		if (err != 0) {
			System.out.println("Error: AdsSyncReadReq: 0x" + Long.toHexString(err));
		} else {
			// Convert stream to AdsSymbolEntry
			adsSymbolEntry = new AdsSymbolEntry(dataBuff.getByteArray());

			// Watch informations (debug)
			// System.out.println("Name:\t\t" + adsSymbolEntry.getName());
			// System.out.println("Index Group:\t" +
			// adsSymbolEntry.getiGroup());
			// System.out.println("Index Offset:\t" +
			// adsSymbolEntry.getiOffs());
			// System.out.println("Size:\t\t" + adsSymbolEntry.getSize());
			// System.out.println("Type:\t\t" + adsSymbolEntry.getType());
			// System.out.println("Comment:\t" + adsSymbolEntry.getComment());

			iGroup = adsSymbolEntry.getiGroup();
			iOff = adsSymbolEntry.getiOffs();
		}
	}

	public String writeIntValue(String variable, int value) {

		String result = "done";

		// Be sure that the handle is the good one
		readValue(variable);

		dataBuff = new JNIByteBuffer(Convert.IntToByteArr(value));

		// Specify IndexGroup, IndexOffset and write PLCVar
		err = AdsCallDllFunction.adsSyncWriteReq(addr, iGroup, iOff, dataBuff.getUsedBytesCount(), dataBuff);
		if (err != 0) {
			result = "Error: Write by adress: 0x" + Long.toHexString(err);
			System.out.println("Error: Write by adress: 0x" + Long.toHexString(err));
		}

		releaseHandle();

		return result;
	}

	public String writeBoolValue(String variable, Boolean value) {

		String result = "done";

		// Be sure that the handle is the good one
		readValue(variable);

		JNIByteBuffer dbuff = new JNIByteBuffer(Convert.BoolToByteArr(value));

		// Specify IndexGroup, IndexOffset and write PLCVar
		err = AdsCallDllFunction.adsSyncWriteReq(addr, iGroup, iOff, dbuff.getUsedBytesCount(), dbuff);
		if (err != 0) {
			result = "Error: Write by adress: 0x" + Long.toHexString(err);
			System.out.println("Error: Write by adress: 0x" + Long.toHexString(err));
		}

		releaseHandle();
		return result;
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private String messageFromTwincat;
	private JPanelPresse panelPresse;
	private Boolean isFull;
	private Boolean isWorking;

	// For Twincat
	private AmsAddr addr;
	private long err;
	private JNIByteBuffer handleBuff;
	private JNIByteBuffer dataBuff;
	private long iGroup;
	private long iOff;
	private AdsSymbolEntry adsSymbolEntry;

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
					System.out.println(getStatut());
					if (getMotorPosition().equals("0")) {
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
					isFull = true;
					// debug
					startPress();
					System.out.println("STATUT: " + getStatut());

					// The job is done
					while (getMotorPosition().equals("0")) {
						panelPresse.setStatut("The press is working");
					}
					System.out.println("STATUT: " + getStatut());
					panelPresse.setStatut("The press finished her work");

					// set the number of piece finished
					System.out.println("NB PIECES: " + getNbPieceFromPress());
					panelPresse.setNbPiece(getNbPieceFromPress());

					// Thread t1 = new Thread(new Runnable() {
					//
					// @Override
					// public void run() {
					// try {
					// Thread.sleep(5000);
					// // isWorking = false;
					// panelPresse.setStatut("Job done");
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					//
					// }
					// });
					// t1.start();

				}
				if (messageBehaviour.equals("dechargement done")) {
					// The robot has discharge this press

					// Thread t1 = new Thread(new Runnable() {
					//
					// @Override
					// public void run() {
					// try {
					// Thread.sleep(2000);
					// isFull = false;
					// panelPresse.setStatut("The press is empty");
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					//
					// }
					// });
					// t1.start();

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