package app.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.Box;

import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.SwingConstants;

public class JPanelSuiviProd extends JPanel {
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public JPanelSuiviProd() {

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
	public void addPiece() {
		int i = Integer.parseInt(lblNbPiecesProduites.getText());
		i++;
		lblNbPiecesProduites.setText(i + "");
	}

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void geometry() {

		lblNbPiecesProduites = new JLabel("0");
		JPanel panel = new JPanel();

		// Layout : Specification
		{
			BorderLayout layout = new BorderLayout();

			setLayout(layout);
		}

		// JComponent : add

		GridLayout gridlayout = new GridLayout(1, 2);

		Box bv1 = Box.createVerticalBox();

		Box bh1 = Box.createHorizontalBox();

		panel.setLayout(gridlayout);

		panel.add(new JLabel("Nombre de pièces produites", SwingConstants.CENTER));
		panel.add(lblNbPiecesProduites);

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
	private JLabel lblNbPiecesProduites;

}
