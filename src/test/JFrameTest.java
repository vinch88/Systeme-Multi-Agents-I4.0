package test;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class JFrameTest extends JFrame {
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public JFrameTest() {
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
		panelTest = new JPanelTest();
		// Layout : Specification
		{
			BorderLayout layout = new BorderLayout();
		}

		// JComponent : add
		add(panelTest, BorderLayout.CENTER);
	}

	private void control() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	private void appearance() {
		setSize(450, 300);
		setResizable(false);
		setTitle("Test TwinCat");
		setLocationRelativeTo(null); // frame centrer
		setVisible(true); // last!

	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// Tools

	private JPanelTest panelTest;
}
