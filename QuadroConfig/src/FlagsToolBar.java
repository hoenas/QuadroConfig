import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JToolBar;

public class FlagsToolBar extends JToolBar {

	public boolean isConfigMode;
	/* byte 1 */
	private static int FLIGHT_MODE_FLAG = 0x01;
	private static int CONFIG_MODE_FLAG = 0x02;
	private static int ERROR_FLAG = 0x04;
	private static int USB_ERROR_FLAG = 0x08;

	private static int CPU_OVERLOAD_FLAG = 0x10;
	private static int NO_RC_SIGNAL_FLAG = 0x20;
	private static int LOW_VOLTAGE_FLAG = 0x40;
	// private static int FREE_FLAG3 = 0x80;

	/* byte 2 */
	private static int MPU9150_OK_FLAG = 0x01;
	private static int RC_RECEIVER_OK_FLAG = 0x02;
	private static int BMP180_OK_FLAG = 0x04;
	private static int EEPROM_OK_FLAG = 0x08;

	/* byte 3 */

	/* byte 4 */
	private static int EMERGENCY_FLAG = 0x80;

	JLabel flagEEPROM;
	JLabel flagBMP;
	JLabel flagRC;
	JLabel flagMPU;
	JLabel flagVoltage;
	JLabel flagOverrun;
	JLabel flagUSB;
	JLabel flagError;
	JLabel flagConfigMode;
	JLabel flagFlightMode;

	public FlagsToolBar() {

		/* toolbar config */
		this.setFloatable(false);
		this.setToolTipText("Global Flags");
		this.setFont(new Font("Dialog", Font.BOLD, 10));
		this.setBounds(20, 265, 400, 19);
		this.setBackground(Color.LIGHT_GRAY);

		/* flags */
		flagEEPROM = new JLabel("<html> <font color='gray'>EEPROM </font> </html>");
		flagEEPROM.setFont(new Font("Dialog", Font.BOLD, 10));
		this.add(flagEEPROM);

		flagMPU = new JLabel("<html> <font color='gray'>MPU </font> </html>");
		flagMPU.setFont(new Font("Dialog", Font.BOLD, 10));
		this.add(flagMPU);

		flagBMP = new JLabel("<html> <font color='gray'>BMP </font> </html>");
		flagBMP.setFont(new Font("Dialog", Font.BOLD, 10));
		this.add(flagBMP);

		flagRC = new JLabel("<html> <font color='gray'>RC </font> </html>");
		flagRC.setFont(new Font("Dialog", Font.BOLD, 10));
		this.add(flagRC);

		flagVoltage = new JLabel("<html> <font color='gray'>Voltage </font> </html>");
		flagVoltage.setFont(new Font("Dialog", Font.BOLD, 10));
		this.add(flagVoltage);

		flagOverrun = new JLabel("<html> <font color='gray'>CPU </font> </html>");
		flagOverrun.setFont(new Font("Dialog", Font.BOLD, 10));
		this.add(flagOverrun);

		flagUSB = new JLabel("<html> <font color='gray'>USB </font> </html>");
		flagUSB.setFont(new Font("Dialog", Font.BOLD, 10));
		this.add(flagUSB);

		flagError = new JLabel("<html> <font color='gray'>Error </font> </html>");
		flagError.setFont(new Font("Dialog", Font.BOLD, 10));
		this.add(flagError);

		flagConfigMode = new JLabel("<html> <font color='gray'>Config </font> </html>");
		flagConfigMode.setFont(new Font("Dialog", Font.BOLD, 10));
		this.add(flagConfigMode);

		flagFlightMode = new JLabel("<html> <font color='gray'>Flight </font> </html>");
		flagFlightMode.setFont(new Font("Dialog", Font.BOLD, 10));
		this.add(flagFlightMode);
	}

	public void update(byte[] flag_array) {

		/* byte 0 */
		if ((flag_array[0] & (FLIGHT_MODE_FLAG)) == FLIGHT_MODE_FLAG) {
			flagFlightMode.setText("<html> <font color='green'>Flight </font> </html>");
		} else {
			flagFlightMode.setText("<html> <font color='gray'>Flight </font> </html>");
		}

		if ((flag_array[0] & CONFIG_MODE_FLAG) == CONFIG_MODE_FLAG) {
			flagConfigMode.setText("<html> <font color='green'>Config </font> </html>");
			isConfigMode = true;
		} else {
			flagConfigMode.setText("<html> <font color='gray'>Config </font> </html>");
			isConfigMode = false;
		}
		if ((flag_array[0] & ERROR_FLAG) == ERROR_FLAG) {
			flagError.setText("<html> <font color='red'>Error </font> </html>");
		} else {
			flagError.setText("<html> <font color='gray'>Error </font> </html>");
		}
		if ((flag_array[0] & USB_ERROR_FLAG) == USB_ERROR_FLAG) {
			flagUSB.setText("<html> <font color='red'>USB </font> </html>");
		} else {
			flagUSB.setText("<html> <font color='green'>USB </font> </html>");

		}
		if ((flag_array[0] & CPU_OVERLOAD_FLAG) == CPU_OVERLOAD_FLAG) {
			flagOverrun.setText("<html> <font color='red'>CPU </font> </html>");
		} else {
			flagOverrun.setText("<html> <font color='gray'>CPU </font> </html>");

		}
		if ((flag_array[0] & LOW_VOLTAGE_FLAG) == LOW_VOLTAGE_FLAG) {
			flagVoltage.setText("<html> <font color='red'>Voltage </font> </html>");
		} else {
			flagVoltage.setText("<html> <font color='green'>Voltage </font> </html>");
		}

		// if ((flag_array[3] & LOW_VOLTAGE_FLAG) == 1) {
		// } else {
		// }
		/* byte 1 */
		if ((flag_array[1] & MPU9150_OK_FLAG) == MPU9150_OK_FLAG) {
			flagMPU.setText("<html> <font color='green'>MPU </font> </html>");
		} else {
			flagMPU.setText("<html> <font color='red'>MPU </font> </html>");
		}
		if ((flag_array[1] & RC_RECEIVER_OK_FLAG) == RC_RECEIVER_OK_FLAG) {
			flagRC.setText("<html> <font color='green'>RC </font> </html>");
		} else {
			flagRC.setText("<html> <font color='red'>RC </font> </html>");
		}
		if ((flag_array[1] & BMP180_OK_FLAG) == BMP180_OK_FLAG) {
			flagBMP.setText("<html> <font color='green'>BMP </font> </html>");
		} else {
			flagBMP.setText("<html> <font color='red'>BMP </font> </html>");
		}
		if ((flag_array[1] & EEPROM_OK_FLAG) == EEPROM_OK_FLAG) {
			flagEEPROM.setText("<html> <font color='green'>EEPROM </font> </html>");
		} else {
			flagEEPROM.setText("<html> <font color='red'>EEPROM </font> </html>");
		}

		/* byte 2 */
		// TODO
		/* byte 3 */
		// TODO
	}

}
