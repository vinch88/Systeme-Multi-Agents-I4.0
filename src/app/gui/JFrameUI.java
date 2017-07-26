package app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import app.gestion.GestionUsine;
import jade.wrapper.AgentController;

public class JFrameUI extends JFrame {

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	/**
	 * @param gu
	 */
	public JFrameUI(GestionUsine gu) {
		gestionUsine = gu;
		geometry();
		control();
		appearance();
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

	private void geometry() {
		// JComponent : Instanciation

		panelRobot = new JPanelRobot();
		panelRobot.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Robot", 0, 0,
				new Font("Dialog", 1, 16), Color.BLACK));

		panelPresse = new JPanelPresse();
		panelPresse.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Presse", 0, 0,
				new Font("Dialog", 1, 16), Color.BLACK));

		panelSuiviProd = new JPanelSuiviProd();
		panelSuiviProd.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"Suivi de production", 0, 0, new Font("Dialog", 1, 16), Color.BLACK));

		JPanel panel = new JPanel();
		// btnDemarrer = new JButton("Démarrer la simulation");
		btnAjouterAgent = new JButton("Démarrer Agents");

		Box b1 = Box.createVerticalBox();

		// Layout : Specification
		{
			BorderLayout layout = new BorderLayout();
		}

		// JComponent : add
		// panel.add(btnDemarrer);
		panel.add(btnAjouterAgent);

		b1.add(panelSuiviProd);
		b1.add(panelRobot);
		b1.add(panelPresse);
		b1.add(panel);
		// b1.add(panelBoutons);

		add(b1, BorderLayout.CENTER);
	}

	private void control() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		btnAjouterAgent.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnAjouterAgent.getText().equals("Démarrer Agents")) {
					btnAjouterAgent.setText("Stoper Agents");
					agentRobot = gestionUsine.createAgent("Robot", "app.agents.AgentRobot", panelRobot, panelSuiviProd);
					agentPress = gestionUsine.createAgent("Presse", "app.agents.AgentPresse", panelPresse);
				} else {
					btnAjouterAgent.setText("Démarrer Agents");
					gestionUsine.killAgent(agentRobot);
					gestionUsine.killAgent(agentPress);
				}

			}
		});

	}

	private void appearance() {
		setSize(450, 500);
		setResizable(true);
		setTitle("Système Multi-Agents - Industrie 4.0");
		setLocationRelativeTo(null); // frame centrer
		setVisible(true); // last!

	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// Tools
	private GestionUsine gestionUsine;
	private JButton btnDemarrer;
	private JButton btnAjouterAgent;

	private AgentController agentPress;
	private AgentController agentRobot;

	private JPanelRobot panelRobot;
	private JPanelPresse panelPresse;
	private JPanelSuiviProd panelSuiviProd;

}
