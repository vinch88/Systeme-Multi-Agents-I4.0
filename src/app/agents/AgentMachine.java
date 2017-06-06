package app.agents;

import app.gui.JPanelMachine;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgentMachine extends Agent {

	protected void setup() {
		System.out.println("Démarrage de " + getLocalName());
		// Make this agent terminate
		// doDelete();

		Object[] args = getArguments();
		panelMachine = (JPanelMachine) args[0];

		// Enregistrement de l'agent dans les pages jaunes
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("machine");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
			System.out.println(getLocalName() + " failed to register to dfs");
		}

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
				// addBehaviour(new RequestPerformer());

			}
		});
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

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private JPanelMachine panelMachine;

	/*------------------------------------------------------------------*\
	|*								Behaviours							*|
	\*------------------------------------------------------------------*/

}