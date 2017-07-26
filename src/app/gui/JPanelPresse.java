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

	/**
	 * @param message
	 */
	public void setStatut(String message) {
		lblStatut.setText(message);
	}

	/**
	 * @param nbPieceFromPress
	 */
	public void setNbPiece(String nbPieceFromPress) {
		NbPiecesProduites = nbPieceFromPress;

	}

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	/**
	 * @return the IP
	 */
	public String getAddrIP() {
		return tfIP.getText();
	}

	/**
	 * @return the port
	 */
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
		NbPiecesProduites = "0";
		JPanel panel = new JPanel();

		// Layout : Specification
		{
			BorderLayout layout = new BorderLayout();

			setLayout(layout);
		}

		// JComponent : add

		GridLayout gridlayout = new GridLayout(2, 2);

		Box bv1 = Box.createVerticalBox();

		Box bh1 = Box.createHorizontalBox();

		bh1.add(lblStatut);

		panel.setLayout(gridlayout);

		panel.add(new JLabel("Adresse IP", SwingConstants.CENTER));
		panel.add(new JPanelDecorator(tfIP, 10));
		panel.add(new JLabel("Port", SwingConstants.CENTER));
		panel.add(new JPanelDecorator(tfPort, 10));

		bv1.add(bh1);
		bv1.add(panel);
		add(bv1, BorderLayout.CENTER);
	}

	private void control() {

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
	private String NbPiecesProduites;

}
