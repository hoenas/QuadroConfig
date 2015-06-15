import javax.swing.JFrame;

import jssc.SerialPort;

import javax.swing.JPanel;


import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.JLabel;

import javax.swing.SpinnerNumberModel;

import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFormattedTextField;


public class ConfigWindow extends JDialog {

	private SerialPort port;
	private JTextField adr_textField;
	private JTextField data_textField;
	private JLabel statuslabel;
	JButton btnEnterConfigMode;
	private boolean isConfigMode;


	JSpinner gain_ang_z_spinner = new JSpinner();
	JSpinner compFilterXY_Spinner = new JSpinner();
	JSpinner compFilterZ_Spinner = new JSpinner();
	JSpinner scale_ang_z_spinner = new JSpinner();
	JSpinner d_ang_z_spinner = new JSpinner();
	JSpinner i_ang_z_spinner = new JSpinner();
	JSpinner p_ang_z_spinner = new JSpinner();
	JSpinner gain_ang_xy_spinner = new JSpinner();
	JSpinner scale_ang_xy_spinner = new JSpinner();
	JSpinner d_ang_xy_spinner = new JSpinner();
	JSpinner i_ang_xy_spinner = new JSpinner();
	JSpinner p_ang_xy_spinner = new JSpinner();

	JLabel errorlabel = new JLabel("");
	JComboBox format_comboBox = new JComboBox();
	JComboBox nrBytes_comboBox = new JComboBox();

	JButton btnWrite = new JButton("write");
	JButton btnRead = new JButton("read");
	JButton btnReloadEeprom = new JButton("reload from eeprom");

	/* configuration */
	private static byte USB_CMD_CONFIG_MODE = (byte) 0xC0;

	private static byte USB_CMD_GET_CONFIG = (byte) 0xC1;
	private static byte USB_CMD_UPDATE_CONFIG = (byte) 0xC2;
	private static byte USB_CMD_SAVE_CONFIG = (byte) 0xCE;
	private static byte USB_CMD_RESTORE_CONFIG = (byte) 0xCF;

	/* eeprom acces */
	private static byte USB_CMD_READ_BYTE = (byte) 0xC3;
	private static byte USB_CMD_READ_2BYTES = (byte) 0xC4;
	private static byte USB_CMD_READ_4BYTES = (byte) 0xC5;

	private static byte USB_CMD_WRITE_BYTE = (byte) 0xC6;
	private static byte USB_CMD_WRITE_2BYTES = (byte) 0xC7;
	private static byte USB_CMD_WRITE_4BYTES = (byte) 0xC8;
	
	private static byte USB_CMD_RELOAD_EEPROM = (byte) 0xC9;

	private static int CONFIGURATION_FRAME_LENGTH = 48;

	public void openConfigWindow(SerialPort port, JLabel statuslabel,
			boolean isConfigMode) {
		this.statuslabel = statuslabel;
		this.port = port;
		this.setVisible(true);
		this.setLocation(433, 50);
		this.setSize(this.getSize().width, this.getSize().height);
		this.isConfigMode = isConfigMode;
		if (isConfigMode) {
			btnEnterConfigMode.setText("leave config mode");
			btnRead.setEnabled(true);
			btnWrite.setEnabled(true);
			btnReloadEeprom.setEnabled(true);
		} else {
			btnEnterConfigMode.setText("enter config mode");
			btnRead.setEnabled(false);
			btnWrite.setEnabled(false);
			btnReloadEeprom.setEnabled(false);
		}
	}

	private float byte2float(byte[] byteArray, int offset) {
		return ByteBuffer.wrap(byteArray, offset, 4)
				.order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}

	private byte[] float2byte(float f, byte[] byteArray, int offset) {

		byte tmp[] = ByteBuffer.allocate(4).putFloat(f).array();
		byteArray[offset] = tmp[3];
		byteArray[offset + 1] = tmp[2];
		byteArray[offset + 2] = tmp[1];
		byteArray[offset + 3] = tmp[0];
		return byteArray;
	}

	private void loadConfig() {
		try {
			byte[] outbuffer = new byte[1];
			outbuffer[0] = USB_CMD_GET_CONFIG;
			port.writeBytes(outbuffer);
			statuslabel.setText("loaded configuration");

			while (port.getInputBufferBytesCount() < CONFIGURATION_FRAME_LENGTH)
				;
			byte[] inbuffer = port.readBytes();

			p_ang_xy_spinner.setValue(byte2float(inbuffer, 0));
			i_ang_xy_spinner.setValue(byte2float(inbuffer, 4));
			d_ang_xy_spinner.setValue(byte2float(inbuffer, 8));
			gain_ang_xy_spinner.setValue(byte2float(inbuffer, 12));
			scale_ang_xy_spinner.setValue(byte2float(inbuffer, 16));

			p_ang_z_spinner.setValue(byte2float(inbuffer, 20));
			i_ang_z_spinner.setValue(byte2float(inbuffer, 24));
			d_ang_z_spinner.setValue(byte2float(inbuffer, 28));
			gain_ang_z_spinner.setValue(byte2float(inbuffer, 32));
			scale_ang_z_spinner.setValue(byte2float(inbuffer, 36));

			compFilterXY_Spinner.setValue(byte2float(inbuffer, 40));
			compFilterZ_Spinner.setValue(byte2float(inbuffer, 44));

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	private void restoreHardcodedCfg() {

		try {
			byte[] outbuffer = new byte[1];
			outbuffer[0] = USB_CMD_RESTORE_CONFIG;
			port.writeBytes(outbuffer);
			statuslabel.setText("resetting to hardcoded settings");

			// dummy read
			while (port.getInputBufferBytesCount() != 1)
				;
			byte[] dummy = port.readBytes();

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	private void reloadEEPROM(){
		try {
			byte[] outbuffer = new byte[1];
			outbuffer[0] = USB_CMD_RELOAD_EEPROM;
			port.writeBytes(outbuffer);
			statuslabel.setText("Reloading EEPROM");

			// dummy read
			while (port.getInputBufferBytesCount() != 1)
				;
			byte[] dummy = port.readBytes();

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
	}

	private void sendConfig() {

		try {

			byte[] outbuffer = new byte[CONFIGURATION_FRAME_LENGTH + 1];
			outbuffer[0] = USB_CMD_UPDATE_CONFIG;
			outbuffer = float2byte(
					Float.parseFloat(p_ang_xy_spinner.getValue().toString()),
					outbuffer, 1);
			outbuffer = float2byte(
					Float.parseFloat(i_ang_xy_spinner.getValue().toString()),
					outbuffer, 5);
			outbuffer = float2byte(
					Float.parseFloat(d_ang_xy_spinner.getValue().toString()),
					outbuffer, 9);
			outbuffer = float2byte(
					Float.parseFloat(gain_ang_xy_spinner.getValue().toString()),
					outbuffer, 13);
			outbuffer = float2byte(Float.parseFloat(scale_ang_xy_spinner
					.getValue().toString()), outbuffer, 17);

			outbuffer = float2byte(
					Float.parseFloat(p_ang_z_spinner.getValue().toString()),
					outbuffer, 21);
			outbuffer = float2byte(
					Float.parseFloat(i_ang_z_spinner.getValue().toString()),
					outbuffer, 25);
			outbuffer = float2byte(
					Float.parseFloat(d_ang_z_spinner.getValue().toString()),
					outbuffer, 29);
			outbuffer = float2byte(
					Float.parseFloat(gain_ang_z_spinner.getValue().toString()),
					outbuffer, 33);
			outbuffer = float2byte(
					Float.parseFloat(scale_ang_z_spinner.getValue().toString()),
					outbuffer, 37);

			outbuffer = float2byte(Float.parseFloat(compFilterXY_Spinner
					.getValue().toString()), outbuffer, 41);
			outbuffer = float2byte(
					Float.parseFloat(compFilterZ_Spinner.getValue().toString()),
					outbuffer, 45);

			port.writeBytes(outbuffer);
			statuslabel.setText("update Configuration");

			// dummy read
			while (port.getInputBufferBytesCount() != 1) {

			}
			byte[] dummy = port.readBytes();

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	private void saveConfig() {

		try {
			byte[] outbuffer = new byte[1];
			outbuffer[0] = USB_CMD_SAVE_CONFIG;
			port.writeBytes(outbuffer);
			statuslabel.setText("save Configuration -> reset");

			// dummy read
			while (port.getInputBufferBytesCount() != 1)
				;
			byte[] dummy = port.readBytes();

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	private boolean parseAddress(byte[] outbuffer, int offset) {
		String temp = adr_textField.getText();
		if (temp.length() != 4) {
			errorlabel.setText("wrong address");
			return false;
		}
		try {
			outbuffer[offset] = (byte) Integer.parseUnsignedInt(
					temp.substring(0, 2), 16);
			outbuffer[offset + 1] = (byte) Integer.parseUnsignedInt(
					temp.substring(2), 16);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			errorlabel.setText("wrong address");
			return false;
		}
		errorlabel.setText("");
		return true;
	}

	private boolean parseData(byte[] outbuffer, int numOfBytes) {
		String tmp = data_textField.getText();
		byte ByteTmp[] = new byte[4];

		if (format_comboBox.getSelectedIndex() == 0) {
			/* hex */
			ByteTmp= ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(Integer.valueOf(tmp, 16)).array();
			for (int i = 0 ; i< numOfBytes;i++) {
				outbuffer[3+i] = ByteTmp[i];
			}
		} else if (format_comboBox.getSelectedIndex() == 1) {
			/* dec */
			ByteTmp= ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((Integer.valueOf(tmp, 10))).array();
			for (int i = 0 ; i< numOfBytes;i++) {
				outbuffer[3+i] = ByteTmp[i];
			}

		} else if (format_comboBox.getSelectedIndex() == 2) {
			/* float */
			if (numOfBytes == 4) {
				ByteTmp = ByteBuffer.allocate(4).putFloat(Float.parseFloat(tmp)).array();
				outbuffer[3] = ByteTmp[0];
				outbuffer[4] = ByteTmp[1];
				outbuffer[5] = ByteTmp[2];
				outbuffer[6] = ByteTmp[3];
				
				//outbuffer = float2byte() , outbuffer, 3);
			} else {
				errorlabel.setText("wrong data format");
				return false;
			}

		} else {
			errorlabel.setText("not implemented");
			return false;
		}
	
		errorlabel.setText("");		
		return true;

	}

	private void interpretInbuffer(byte[] inbuffer, int numOfBytes) {
		String tmp = "";

		if (format_comboBox.getSelectedIndex() == 0) {
			/* hex */
			byte[] bytebuffer = new byte[4];
			for (int i = 0; i < numOfBytes; i++) {
				bytebuffer[i] = inbuffer[i];
			}
			for (int i = numOfBytes; i < 4; i++) {
				bytebuffer[i] = 0;
			}
			tmp = Integer.toHexString(
					ByteBuffer.wrap(bytebuffer, 0, 4)
							.order(ByteOrder.LITTLE_ENDIAN).getInt());
		} else if (format_comboBox.getSelectedIndex() == 1) {
			/* dec */
			byte[] bytebuffer = new byte[4];
			for (int i = 0; i < numOfBytes; i++) {
				bytebuffer[i] = inbuffer[i];
			}
			tmp = Integer.toUnsignedString(ByteBuffer.wrap(bytebuffer, 0, 4)
					.order(ByteOrder.LITTLE_ENDIAN).getInt());
		} else if (format_comboBox.getSelectedIndex() == 2) {
			/* float */
			if (numOfBytes == 4) {
				tmp = Float.toString(ByteBuffer.wrap(inbuffer, 0, 4)
						.order(ByteOrder.BIG_ENDIAN).getFloat());
			} else {
				errorlabel.setText("wrong data format");
				return;
			}

		} else {
			errorlabel.setText("not implemented");
			return;
		}
		data_textField.setText(tmp);
		errorlabel.setText("");
	}

	private void readEEPROM() {
		int numberOfBytes;
		byte tmp;

		if (nrBytes_comboBox.getSelectedIndex() == 0) {
			tmp = USB_CMD_READ_BYTE;
			numberOfBytes = 1;
		} else if (nrBytes_comboBox.getSelectedIndex() == 1) {
			tmp = USB_CMD_READ_2BYTES;
			numberOfBytes = 2;
		} else {
			tmp = USB_CMD_READ_4BYTES;
			numberOfBytes = 4;
		}
		byte[] outbuffer = new byte[3];
		outbuffer[0] = tmp;

		if (parseAddress(outbuffer, 1)) {
			try {
				port.writeBytes(outbuffer);

				statuslabel.setText("reading eeprom");

				while (port.getInputBufferBytesCount() < numberOfBytes)
					;
				byte[] inbuffer = port.readBytes();

				interpretInbuffer(inbuffer, numberOfBytes);

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

	}

	private void writeEEPROM() {
		int numberOfBytes;
		byte tmp;

		if (nrBytes_comboBox.getSelectedIndex() == 0) {
			tmp = USB_CMD_WRITE_BYTE;
			numberOfBytes = 1;
		} else if (nrBytes_comboBox.getSelectedIndex() == 1) {
			tmp = USB_CMD_WRITE_2BYTES;
			numberOfBytes = 2;
		} else {
			tmp = USB_CMD_WRITE_4BYTES;
			numberOfBytes = 4;
		}
		byte[] outbuffer = new byte[3 + numberOfBytes];
		outbuffer[0] = tmp;

		if (parseAddress(outbuffer, 1)) {
			if (parseData(outbuffer, numberOfBytes)) {

				try {
					port.writeBytes(outbuffer);

					statuslabel.setText("writing eeprom");

					// dummy read
					while (port.getInputBufferBytesCount() != 1)
						;
					byte[] dummy = port.readBytes();

					

				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			} else {
				errorlabel.setText("wrong data format");
			}
		}

	}

	private void toggleConfigMode() {
		if (isConfigMode) {
			isConfigMode = false;
			btnEnterConfigMode.setText("enter config mode");
			btnRead.setEnabled(false);
			btnWrite.setEnabled(false);
			btnReloadEeprom.setEnabled(false);
		} else {
			isConfigMode = true;
			btnEnterConfigMode.setText("leave config mode");
			btnRead.setEnabled(true);
			btnWrite.setEnabled(true);
			btnReloadEeprom.setEnabled(true);
		}

		try {
			byte[] outbuffer = new byte[1];
			outbuffer[0] = USB_CMD_CONFIG_MODE;
			port.writeBytes(outbuffer);

			// dummy read
			while (port.getInputBufferBytesCount() != 1)
				;
			byte[] dummy = port.readBytes();

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public ConfigWindow() {

		setTitle("Configuration");
		setBounds(433, 377, 700, 600);
		getContentPane().setLayout(null);

		JPanel PID_angle_XY_panel = new JPanel();
		PID_angle_XY_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		PID_angle_XY_panel.setBounds(12, 12, 166, 169);
		getContentPane().add(PID_angle_XY_panel);
		PID_angle_XY_panel.setLayout(null);

		p_ang_xy_spinner.setModel(new SpinnerNumberModel(new Float(1), null,
				null, new Float(0.1)));
		p_ang_xy_spinner.setBounds(77, 36, 60, 20);
		PID_angle_XY_panel.add(p_ang_xy_spinner);

		i_ang_xy_spinner.setModel(new SpinnerNumberModel(new Float(1), null,
				null, new Float(0.1)));
		i_ang_xy_spinner.setBounds(77, 60, 60, 20);
		PID_angle_XY_panel.add(i_ang_xy_spinner);

		d_ang_xy_spinner.setModel(new SpinnerNumberModel(new Float(1), null,
				null, new Float(0.1)));
		d_ang_xy_spinner.setBounds(77, 84, 60, 20);
		PID_angle_XY_panel.add(d_ang_xy_spinner);

		scale_ang_xy_spinner.setModel(new SpinnerNumberModel(new Float(1),
				null, null, new Float(0.1)));
		scale_ang_xy_spinner.setBounds(77, 132, 60, 20);
		PID_angle_XY_panel.add(scale_ang_xy_spinner);

		gain_ang_xy_spinner.setModel(new SpinnerNumberModel(new Float(1), null,
				null, new Float(0.1)));
		gain_ang_xy_spinner.setBounds(77, 108, 60, 20);
		PID_angle_XY_panel.add(gain_ang_xy_spinner);

		JLabel lblPidAngleXy = new JLabel("PID Angle XY");
		lblPidAngleXy.setBounds(27, 12, 110, 20);
		PID_angle_XY_panel.add(lblPidAngleXy);

		JLabel lblP = new JLabel("P");
		lblP.setBounds(12, 37, 50, 16);
		PID_angle_XY_panel.add(lblP);

		JLabel lblI = new JLabel("I");
		lblI.setBounds(12, 59, 50, 16);
		PID_angle_XY_panel.add(lblI);

		JLabel lblD = new JLabel("D");
		lblD.setBounds(12, 85, 50, 16);
		PID_angle_XY_panel.add(lblD);

		JLabel lblGain = new JLabel("Gain");
		lblGain.setBounds(12, 109, 50, 16);
		PID_angle_XY_panel.add(lblGain);

		JLabel lblSpScale = new JLabel("SP scale");
		lblSpScale.setBounds(12, 133, 60, 16);
		PID_angle_XY_panel.add(lblSpScale);

		JPanel PID_angle_Z_panel = new JPanel();
		PID_angle_Z_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		PID_angle_Z_panel.setLayout(null);
		PID_angle_Z_panel.setBounds(184, 12, 166, 169);
		getContentPane().add(PID_angle_Z_panel);

		p_ang_z_spinner.setModel(new SpinnerNumberModel(new Float(1), null,
				null, new Float(0.1)));
		p_ang_z_spinner.setBounds(77, 36, 60, 20);
		PID_angle_Z_panel.add(p_ang_z_spinner);

		i_ang_z_spinner.setModel(new SpinnerNumberModel(new Float(1), null,
				null, new Float(0.1)));
		i_ang_z_spinner.setBounds(77, 60, 60, 20);
		PID_angle_Z_panel.add(i_ang_z_spinner);

		d_ang_z_spinner.setModel(new SpinnerNumberModel(new Float(1), null,
				null, new Float(0.1)));
		d_ang_z_spinner.setBounds(77, 84, 60, 20);
		PID_angle_Z_panel.add(d_ang_z_spinner);

		scale_ang_z_spinner.setModel(new SpinnerNumberModel(new Float(1), null,
				null, new Float(0.1)));
		scale_ang_z_spinner.setBounds(77, 132, 60, 20);
		PID_angle_Z_panel.add(scale_ang_z_spinner);

		gain_ang_z_spinner.setModel(new SpinnerNumberModel(new Float(1), null,
				null, new Float(0.1)));
		gain_ang_z_spinner.setBounds(77, 108, 60, 20);
		PID_angle_Z_panel.add(gain_ang_z_spinner);

		JLabel label = new JLabel("PID Angle Z");
		label.setBounds(27, 12, 110, 20);
		PID_angle_Z_panel.add(label);

		JLabel label_1 = new JLabel("P");
		label_1.setBounds(12, 37, 50, 16);
		PID_angle_Z_panel.add(label_1);

		JLabel label_2 = new JLabel("I");
		label_2.setBounds(12, 59, 50, 16);
		PID_angle_Z_panel.add(label_2);

		JLabel label_3 = new JLabel("D");
		label_3.setBounds(12, 85, 50, 16);
		PID_angle_Z_panel.add(label_3);

		JLabel label_4 = new JLabel("Gain");
		label_4.setBounds(12, 109, 50, 16);
		PID_angle_Z_panel.add(label_4);

		JLabel label_5 = new JLabel("SP scale");
		label_5.setBounds(12, 133, 60, 16);
		PID_angle_Z_panel.add(label_5);

		JPanel compfilter_panel = new JPanel();
		compfilter_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		compfilter_panel.setBounds(12, 188, 166, 91);
		getContentPane().add(compfilter_panel);
		compfilter_panel.setLayout(null);

		JLabel lblComplFilter = new JLabel("Comp.  Filter");
		lblComplFilter.setBounds(12, 12, 169, 15);
		compfilter_panel.add(lblComplFilter);

		JLabel lblXy = new JLabel("XY");
		lblXy.setBounds(22, 34, 50, 15);
		compfilter_panel.add(lblXy);

		JLabel lblZ = new JLabel("Z");
		lblZ.setBounds(22, 58, 50, 15);
		compfilter_panel.add(lblZ);

		compFilterXY_Spinner.setModel(new SpinnerNumberModel(new Float(1),
				null, null, new Float(0.1)));
		compFilterXY_Spinner.setBounds(65, 34, 60, 20);
		compfilter_panel.add(compFilterXY_Spinner);

		compFilterZ_Spinner.setModel(new SpinnerNumberModel(new Float(1), null,
				null, new Float(0.1)));
		compFilterZ_Spinner.setBounds(65, 58, 60, 20);
		compfilter_panel.add(compFilterZ_Spinner);

		JPanel eeprom_access_panel = new JPanel();
		eeprom_access_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		eeprom_access_panel.setBounds(12, 397, 383, 147);
		getContentPane().add(eeprom_access_panel);
		eeprom_access_panel.setLayout(null);

		JLabel lblDirectEepromAccess = new JLabel("Direct EEPROM Access");
		lblDirectEepromAccess.setBounds(12, 12, 180, 15);
		eeprom_access_panel.add(lblDirectEepromAccess);

		btnRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				readEEPROM();
			}
		});
		btnRead.setBounds(22, 110, 125, 25);
		eeprom_access_panel.add(btnRead);

		btnWrite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeEEPROM();
			}
		});
		btnWrite.setBounds(245, 110, 125, 25);
		eeprom_access_panel.add(btnWrite);

		nrBytes_comboBox.setBounds(300, 30, 70, 20);
		nrBytes_comboBox.addItem("1");
		nrBytes_comboBox.addItem("2");
		nrBytes_comboBox.addItem("4");
		nrBytes_comboBox.setSelectedIndex(2);
		eeprom_access_panel.add(nrBytes_comboBox);

		format_comboBox.setBounds(300, 60, 70, 20);
		format_comboBox.addItem("hex");
		format_comboBox.addItem("dec");
		format_comboBox.addItem("float");
		format_comboBox.addItem("ascii");
		format_comboBox.setSelectedIndex(2);
		
		eeprom_access_panel.add(format_comboBox);

		adr_textField = new JTextField();
		adr_textField.setBounds(170, 30, 70, 20);
		eeprom_access_panel.add(adr_textField);
		adr_textField.setColumns(4);

		data_textField = new JTextField();
		data_textField.setBounds(110, 60, 130, 20);
		eeprom_access_panel.add(data_textField);
		data_textField.setColumns(10);

		JLabel lblbytes = new JLabel("bytes");
		lblbytes.setBounds(250, 30, 60, 20);
		eeprom_access_panel.add(lblbytes);

		JLabel lblAddress = new JLabel("address (16Bit hex)");
		lblAddress.setBounds(22, 30, 148, 15);
		eeprom_access_panel.add(lblAddress);

		JLabel lblData = new JLabel("data");
		lblData.setBounds(22, 60, 70, 15);
		eeprom_access_panel.add(lblData);

		JLabel lblFormat = new JLabel("format");
		lblFormat.setBounds(250, 60, 60, 20);
		eeprom_access_panel.add(lblFormat);

		errorlabel.setBounds(22, 90, 270, 15);
		eeprom_access_panel.add(errorlabel);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(434, 299, 254, 245);
		getContentPane().add(panel);
		panel.setLayout(null);

		JButton btnLoad = new JButton("load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadConfig();
			}
		});
		btnLoad.setBounds(12, 12, 230, 25);
		panel.add(btnLoad);

		JButton btnUpdate = new JButton("update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendConfig();
			}
		});
		btnUpdate.setBounds(12, 42, 230, 25);
		panel.add(btnUpdate);

		JButton btnSaveToEeprom = new JButton("save to eeprom");
		btnSaveToEeprom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveConfig();
			}
		});
		btnSaveToEeprom.setBounds(12, 72, 230, 25);
		panel.add(btnSaveToEeprom);

		btnEnterConfigMode = new JButton("config mode");
		btnEnterConfigMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleConfigMode();
			}
		});
		btnEnterConfigMode.setBounds(12, 172, 230, 25);
		panel.add(btnEnterConfigMode);

		JButton btnRestore = new JButton("restore hardcoded settings");
		btnRestore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				restoreHardcodedCfg();
			}
		});
		btnRestore.setBounds(12, 102, 230, 25);
		panel.add(btnRestore);

		JButton btnEndConfig = new JButton("End Configuration");
		btnEndConfig.setBounds(12, 212, 230, 25);
		panel.add(btnEndConfig);
		
		
		btnReloadEeprom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				reloadEEPROM();
			}
		});
		btnReloadEeprom.setBounds(12, 132, 230, 25);
		panel.add(btnReloadEeprom);
		btnEndConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});

	}
}
