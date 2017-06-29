package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class JPanelTest extends JPanel {
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public JPanelTest() {
		twinCat = new ConnexionTwincat();
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

	public String getAddr() {
		return tfIP.getText();
	}

	public int getPort() {
		return Integer.parseInt(tfPort.getText());
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void geometry() {

		lblStatut = new JLabel("Programme non démarré");
		tfIP = new JTextField("192.168.56.1.1.1");
		tfPort = new JTextField("851");
		JPanel panel = new JPanel();
		btnConnect = new JButton("Connexion");
		btnDisconnect = new JButton("Déconnexion");

		btnStartPresse = new JButton("Start Presse");
		btnResetPresse = new JButton("Reset Presse");
		btnSendMessage = new JButton("Send Message");
		btnChangeVitesse = new JButton("Changer Vitesse");
		btnChangeMode = new JButton("Changer Mode");
		btnStartMoteur = new JButton("Start Moteur");

		// Layout : Specification
		{
			BorderLayout layout = new BorderLayout();

			setLayout(layout);
		}

		// JComponent : add

		GridLayout gridlayout = new GridLayout(6, 2);

		Box bv1 = Box.createVerticalBox();
		// Box bv2 = Box.createVerticalBox();
		Box bh1 = Box.createHorizontalBox();
		// Box bh2 = Box.createHorizontalBox();
		// Box bh3 = Box.createHorizontalBox();
		//
		bh1.add(lblStatut);
		bh1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Statut de l'agent", 0, 0,
				new Font("Dialog", 1, 16), Color.BLACK));
		panel.setLayout(gridlayout);
		//

		panel.add(new JLabel("Adresse", SwingConstants.CENTER));
		panel.add(tfIP);
		panel.add(new JLabel("Port", SwingConstants.CENTER));
		panel.add(tfPort);
		panel.add(btnConnect);
		panel.add(btnDisconnect);
		panel.add(btnStartPresse);
		panel.add(btnResetPresse);
		panel.add(btnSendMessage);
		panel.add(btnChangeVitesse);
		panel.add(btnChangeMode);
		panel.add(btnStartMoteur);

		bv1.add(bh1);
		bv1.add(panel);
		add(bv1, BorderLayout.CENTER);
	}

	private void control() {

		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				twinCat.initializeConnection(getAddr(), getPort());
			}
		});

		btnDisconnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				twinCat.closeConnection();
			}
		});

		btnStartPresse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				twinCat.startPresse();
			}
		});

		btnResetPresse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				twinCat.resetPresse();
			}
		});

		btnSendMessage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String message = "test";
				twinCat.sendMessageToTwinCat(message);
			}
		});

		btnChangeVitesse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				twinCat.changerVitesse(100);
			}
		});

		btnChangeMode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				twinCat.changeModePresse();
			}
		});

		btnStartMoteur.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				twinCat.startMoteur();
			}
		});
	}

	private void appearance() {
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// Tools

	// input
	private JTextField tfIP;
	private JTextField tfPort;
	private JLabel lblStatut;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JButton btnStartPresse;
	private JButton btnResetPresse;
	private JButton btnSendMessage;
	private JButton btnChangeVitesse;
	private JButton btnChangeMode;
	private JButton btnStartMoteur;

	private ConnexionTwincat twinCat;
}
