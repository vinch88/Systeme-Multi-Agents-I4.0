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

		panelRobot = new JPanelRobot();
		panelRobot.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Robot", 0, 0,
				new Font("Dialog", 1, 16), Color.BLACK));

		JPanel panel = new JPanel();
		btnDemarrer = new JButton("D�marrer la simulation");
		btnAjouterAgent = new JButton("Ajouter Agent");

		Box b1 = Box.createVerticalBox();

		// Layout : Specification
		{
			BorderLayout layout = new BorderLayout();
		}

		// JComponent : add
		panel.add(btnDemarrer);
		panel.add(btnAjouterAgent);
		b1.add(panel);
		b1.add(panelRobot);
		// b1.add(panelMachine);
		// b1.add(panelBoutons);

		add(b1, BorderLayout.CENTER);
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
				gestionUsine.createAgent("Robot", "app.agents.AgentRobot", panelRobot);
			}
		});

	}

	private void appearance() {
		setSize(450, 300);
		setResizable(false);
		setTitle("Syst�me Multi-Agents - Industrie 4.0");
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

	private JPanelRobot panelRobot;

}
