package app.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import app.gestion.GestionUsine;

public class JFrameUI extends JFrame {

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

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
		JPanel panel = new JPanel();
		btnDemarrer = new JButton("Démarrer la simulation");
		btnAjouterAgent = new JButton("Ajouter Agent");

		// Layout : Specification
		{
			GridLayout layout = new GridLayout(1, 2);
		}

		// JComponent : add
		panel.add(btnDemarrer);
		panel.add(btnAjouterAgent);
		add(panel);
	}

	private void control() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		btnDemarrer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = "C:\\Program Files (x86)\\Synapxis\\Synapxis.exe";
				try {
					Runtime r = Runtime.getRuntime();
					Process p = r.exec(cmd);
					p.waitFor();// si l'application doit attendre a ce que ce
								// process fini
				} catch (Exception e1) {
					System.out.println("erreur d'execution " + cmd + e1.toString());
				}

			}
		});

		btnAjouterAgent.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gestionUsine.createAgent("Robot", "app.agents.AgentRobot");
			}
		});

	}

	private void appearance() {
		setSize(600, 450);
		setResizable(false);
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

}
