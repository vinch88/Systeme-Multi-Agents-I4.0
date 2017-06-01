package app.agents;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

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
		messageRobot = new String("");

		Object[] args = getArguments();
		// panelConfig = (JPanelConfigurationMoulage) args[0];
		// panelAgent = (JPanelAgentsMoulage) args[1];
		// tempsInjectionMillisecondes = panelConfig.getTempsInjection() * 1000;

		// Enregistrement de l'agent dans les pages jaunes
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("presse");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		Boolean test = true;
		while (test) {
			connexionRobot();
		}
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

	private Boolean connexionRobot() {
		System.out.println("Trying to connect to Synapxis");
		messageRobot = "chargement";
		DataInputStream userInput;
		PrintStream theOutputStream;
		try {
			InetAddress serveur = InetAddress.getByName("localhost");
			int port = 1111;
			Socket socket = new Socket(serveur, port);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream out = new PrintStream(socket.getOutputStream());
			out.print(messageRobot);
			System.out.println(in.readLine());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fail connexion");
		}

		return true;
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private String messageRobot;

	// String sentence;
	// String modifiedSentence;
	// BufferedReader inFromUser = new BufferedReader(new
	// InputStreamReader(System.in));
	// Socket clientSocket = new Socket("localhost", 6789);
	// DataOutputStream outToServer = new
	// DataOutputStream(clientSocket.getOutputStream());
	// BufferedReader inFromServer = new BufferedReader(new
	// InputStreamReader(clientSocket.getInputStream()));
	// sentence = inFromUser.readLine();
	// outToServer.writeBytes(sentence + '\n');
	// modifiedSentence = inFromServer.readLine();
	// System.out.println("FROM SERVER: " + modifiedSentence);
	// clientSocket.close();

}
