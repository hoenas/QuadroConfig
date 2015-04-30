import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;

import jssc.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PortConfig extends JDialog {

	private final JPanel contentPanel = new JPanel();

	public String portName;
	public int baudRate;
	public int databits;
	public int stoppbits;
	public int parity;
	public boolean ok;

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		try {
			PortConfig dialog = new PortConfig();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PortConfig() {
		setBounds(100, 100, 213, 214);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblPortname = new JLabel("Port-Name:");
			lblPortname.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_lblPortname = new GridBagConstraints();
			gbc_lblPortname.anchor = GridBagConstraints.EAST;
			gbc_lblPortname.insets = new Insets(0, 0, 5, 5);
			gbc_lblPortname.gridx = 0;
			gbc_lblPortname.gridy = 0;
			contentPanel.add(lblPortname, gbc_lblPortname);
		}
		{
			JComboBox comboBox = new JComboBox();
			String[] portNames = SerialPortList.getPortNames();
	        for(int i = 0; i < portNames.length; i++){
	            comboBox.addItem(portNames[i]);
	        }
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.insets = new Insets(0, 0, 5, 0);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 1;
			gbc_comboBox.gridy = 0;
			contentPanel.add(comboBox, gbc_comboBox);
		}
		{
			JLabel lblBaudrate = new JLabel("Baud-Rate:");
			lblBaudrate.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_lblBaudrate = new GridBagConstraints();
			gbc_lblBaudrate.anchor = GridBagConstraints.WEST;
			gbc_lblBaudrate.insets = new Insets(0, 0, 5, 5);
			gbc_lblBaudrate.gridx = 0;
			gbc_lblBaudrate.gridy = 1;
			contentPanel.add(lblBaudrate, gbc_lblBaudrate);
		}
		{
			JComboBox comboBox = new JComboBox();
			comboBox.addItem(SerialPort.BAUDRATE_110);
			comboBox.addItem(SerialPort.BAUDRATE_300);
			comboBox.addItem(SerialPort.BAUDRATE_600);
			comboBox.addItem(SerialPort.BAUDRATE_1200);
			comboBox.addItem(SerialPort.BAUDRATE_4800);
			comboBox.addItem(SerialPort.BAUDRATE_9600);
			comboBox.addItem(SerialPort.BAUDRATE_14400);
			comboBox.addItem(SerialPort.BAUDRATE_38400);
			comboBox.addItem(SerialPort.BAUDRATE_57600);
			comboBox.addItem(SerialPort.BAUDRATE_115200);
			comboBox.addItem(SerialPort.BAUDRATE_128000);
			comboBox.setSelectedIndex(9);
			baudRate = SerialPort.BAUDRATE_115200;
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.insets = new Insets(0, 0, 5, 0);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 1;
			gbc_comboBox.gridy = 1;
			contentPanel.add(comboBox, gbc_comboBox);
		}
		{
			JLabel label = new JLabel("");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 0;
			gbc_label.gridy = 2;
			contentPanel.add(label, gbc_label);
		}
		{
			JLabel lblDatenbits = new JLabel("Datenbits:");
			lblDatenbits.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_lblDatenbits = new GridBagConstraints();
			gbc_lblDatenbits.anchor = GridBagConstraints.WEST;
			gbc_lblDatenbits.insets = new Insets(0, 0, 5, 5);
			gbc_lblDatenbits.gridx = 0;
			gbc_lblDatenbits.gridy = 3;
			contentPanel.add(lblDatenbits, gbc_lblDatenbits);
		}
		{
			JComboBox comboBox = new JComboBox();
			comboBox.addItem(SerialPort.DATABITS_5);
			comboBox.addItem(SerialPort.DATABITS_6);
			comboBox.addItem(SerialPort.DATABITS_7);
			comboBox.addItem(SerialPort.DATABITS_8);
			comboBox.setSelectedIndex(3);
			databits = SerialPort.DATABITS_8;
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.insets = new Insets(0, 0, 5, 0);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 1;
			gbc_comboBox.gridy = 3;
			contentPanel.add(comboBox, gbc_comboBox);
		}
		{
			JLabel lblStoppbits = new JLabel("Stoppbits:");
			lblStoppbits.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_lblStoppbits = new GridBagConstraints();
			gbc_lblStoppbits.anchor = GridBagConstraints.WEST;
			gbc_lblStoppbits.insets = new Insets(0, 0, 5, 5);
			gbc_lblStoppbits.gridx = 0;
			gbc_lblStoppbits.gridy = 4;
			contentPanel.add(lblStoppbits, gbc_lblStoppbits);
		}
		{
			JComboBox comboBox = new JComboBox();
			comboBox.addItem(SerialPort.STOPBITS_1);
			comboBox.addItem(SerialPort.STOPBITS_2);
			comboBox.addItem(SerialPort.STOPBITS_1_5);
			comboBox.setSelectedIndex(0);
			stoppbits = SerialPort.STOPBITS_1;
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.insets = new Insets(0, 0, 5, 0);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 1;
			gbc_comboBox.gridy = 4;
			contentPanel.add(comboBox, gbc_comboBox);
		}
		{
			JLabel lblPartitt = new JLabel("Partit\u00E4t:");
			lblPartitt.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_lblPartitt = new GridBagConstraints();
			gbc_lblPartitt.anchor = GridBagConstraints.WEST;
			gbc_lblPartitt.insets = new Insets(0, 0, 0, 5);
			gbc_lblPartitt.gridx = 0;
			gbc_lblPartitt.gridy = 5;
			contentPanel.add(lblPartitt, gbc_lblPartitt);
		}
		{
			JComboBox comboBox = new JComboBox();
			comboBox.addItem("keine");
			comboBox.addItem("gerade");
			comboBox.addItem("ungerade");
			comboBox.setSelectedIndex(2);
			parity = SerialPort.PARITY_EVEN;
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 1;
			gbc_comboBox.gridy = 5;
			contentPanel.add(comboBox, gbc_comboBox);
		}
		{

		}

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
