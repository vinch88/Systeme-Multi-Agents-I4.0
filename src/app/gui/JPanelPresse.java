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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class JPanelPresse extends JPanel {

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public JPanelPresse() {

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

	public void setStatut(String message) {
		lblStatut.setText(message);
	}

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	public String getAddrIP() {
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
		btnDemarrerTwincat = new JButton("Démarrer TwinCat");
		JPanel panel = new JPanel();

		// Layout : Specification
		{
			BorderLayout layout = new BorderLayout();

			setLayout(layout);
		}

		// JComponent : add

		GridLayout gridlayout = new GridLayout(3, 2);

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

		panel.add(new JLabel("Adresse IP", SwingConstants.CENTER));
		panel.add(tfIP);
		panel.add(new JLabel("Port", SwingConstants.CENTER));
		panel.add(tfPort);
		panel.add(new JPanel());
		panel.add(btnDemarrerTwincat);

		bv1.add(bh1);
		bv1.add(panel);
		add(bv1, BorderLayout.CENTER);
	}

	private void control() {

		btnDemarrerTwincat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// if we have to start TwinCat
				// Not used right now
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
	private JButton btnDemarrerTwincat;
	private JLabel lblStatut;

	public void setNbPiece(String nbPieceFromPress) {
		// TODO Auto-generated method stub

	}

}
