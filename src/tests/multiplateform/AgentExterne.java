package tests.multiplateform;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class AgentExterne extends Agent{
	
	//A MODIFIER POUR LE TRANSFORMER en CLASSE
	
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/
	public AgentExterne(String host, String port,String name )
	{
		// R�cup�ration du Runtime de Jade
		Runtime rt = Runtime.instance();
		
		//Cr�er un container pour cet agent
		Profile p = new ProfileImpl();
		p.setParameter(Profile.MAIN_HOST, host);
		p.setParameter(Profile.MAIN_PORT, port);
		ContainerController cc = rt.createAgentContainer(p);
		if(cc != null)
		{
			//Cr�er l'agent et le d�marrer
			try{
				AgentController ac = cc.createNewAgent(name, "asd.AgentExterne", null);
				ac.start();
				return ac;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
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
	
	String host; //host du Main Container
	String port; //port du Main Container
	String name; //Nom de cet agent
}
