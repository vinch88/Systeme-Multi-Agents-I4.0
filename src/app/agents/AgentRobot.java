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
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

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

		// panelConfig = (JPanelConfigurationMoulage) args[0];
		// panelAgent = (JPanelAgentsMoulage) args[1];
		// tempsInjectionMillisecondes = panelConfig.getTempsInjection() * 1000;

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
		}

		// Test partie sale
		// Boolean test = true;
		// while (test) {
		// initializeConnection();
		// }

		// Test partie correcte
		try {
			initializeConnection(InetAddress.getByName(addrIP), port);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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

	// Version sale
	// private Boolean initializeConnection() {
	// System.out.println("Trying to connect to Synapxis");
	// messageRobot = "chargement";
	// DataInputStream userInput;
	// PrintStream theOutputStream;
	// try {
	// InetAddress serveur = InetAddress.getByName("localhost");
	// int port = 1111;
	// Socket socket = new Socket(serveur, port);
	// BufferedReader in = new BufferedReader(new
	// InputStreamReader(socket.getInputStream()));
	// PrintStream out = new PrintStream(socket.getOutputStream());
	// out.print(messageRobot);
	// System.out.println(in.readLine());
	// } catch (Exception e) {
	// e.printStackTrace();
	// System.out.println("Fail connexion");
	// }
	//
	// return true;
	// }

	private Boolean initializeConnection(InetAddress addr, int port) {
		System.out.println("Trying to connect to Synapxis");
		try {
			socket = new Socket(addr, port);
			inputMessageFromRobot = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outSendMessage = new PrintStream(socket.getOutputStream());
			System.out.println("Connected to Synapxis");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fail to connect to Synapxis");
			return false;
		}

	}

	private Boolean closeConnection() {
		try {
			socket.close();
			System.out.println("Connection closed");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Connection close failed");
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
		System.out.println("FROM Synapxis: " + messageFromRobot);
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private String messageFromRobot;
	private Socket socket;
	private PrintStream outSendMessage;
	private BufferedReader inputMessageFromRobot;

	private JPanelRobot panelRobot;

	private String message;
	private int i;
	private Boolean test;

}
