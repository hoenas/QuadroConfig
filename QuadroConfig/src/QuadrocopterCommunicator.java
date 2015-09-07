import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import jssc.SerialPort;

public class QuadrocopterCommunicator {

	// Timeout in ms
	private static long COMMUNICATION_TIMEOUT = 100;
	private static int COMMINICATION_FAILED = 0;
	private static int COMMUNICATION_SUCCESSFUL = 1;

	/**************************************************************************/
	/*
	 * USB_CMD
	 */

	/* resend received */
	public static int USB_CMD_LOOP = 0x01;

	public static int USB_CMD_SEND_STATUS_8BIT = 0x02;

	/* old */
	public static int USB_CMD_SEND_STATUS_FLOAT = 0x03;

	public static int USB_CMD_GLOBAL_FLAGS = 0x04;
	public static int USB_CMD_GLOBAL_FLAGS_RESPONSE_LENGTH = 1;

	/*******************/
	/*
	 * Status Data sending Commands
	 */

	/*
	 * sends all sensor data: accelerometer gyro magnetometer barometer rc
	 * receiver
	 */
	public static int USB_CMD_SEND_SENSOR_DATA = 0x10;
	public static int USB_CMD_SEND_SENSOR_DATA_RESPONSE_LENGTH = 80;

	/*
	 * angle angle setpoint
	 * 
	 * velocity velocity setpoint
	 * 
	 * motorSetpoint motorValues
	 */
	public static int USB_CMD_SEND_FLIGHT_DATA = 0x11;
	public static int USB_CMD_SEND_FLIGHT_DATA_RESPONSE_LENGTH = 88;

	/*
	 * system states
	 * 
	 * uptime akkuVoltage cpuLoad
	 */
	public static int USB_CMD_SEND_SYSTEM_STATE = 0x12;
	public static int USB_CMD_SEND_SYSTEM_STATE_RESPONSE_LENGTH = 12;

	/* gps data */

	public static int USB_CMD_SEND_GPS_DATA_TIME = 0x13;
	public static int USB_CMD_SEND_GPS_DATA_TIME_RESPONSE_LENGTH = 15;
	public static int USB_CMD_SEND_GPS_DATA_POSITION = 0x14;
	public static int USB_CMD_SEND_GPS_DATA_POSITION_RESPONSE_LENGTH = 107;

	/*******************/
	/*
	 * Config Commands (except USB_CMD_CONFIG_MODE)
	 */

	/* enter and leave config mode */
	public static int USB_CMD_CONFIG_MODE = 0xC0;
	public static int USB_CMD_CONFIG_MODE_RESPONSE_LENGTH = 1;
	public static int USB_CMD_CONFIG_MODE_ACK_BYTE = 0xC0;

	/* get or update whole configuration */
	public static int USB_CMD_GET_CONFIG = 0xC1;
	public static int USB_CMD_GET_CONFIG_RESPONSE_LENGTH = 48;
	public static int USB_CMD_UPDATE_CONFIG = 0xC2;
	public static int USB_CMD_UPDATE_CONFIG_RESPONSE_LENGTH = 1;
	public static int USB_CMD_UPDATE_CONFIG_ACK_BYTE = 0xC2;

	/*
	 * direct EEPROM access only working if you are in config mode
	 * 
	 * needs Address: MSB first structure: USB_CMD; ADDR_MSB; ADDR_LSB; DATA;
	 */
	public static int USB_CMD_READ_BYTE = 0xC3;
	public static int USB_CMD_READ_BYTE_RESPONSE_LENGTH = 1;
	public static int USB_CMD_READ_2BYTES = 0xC4;
	public static int USB_CMD_READ_2BYTES_RESPONSE_LENGTH = 1;
	public static int USB_CMD_READ_4BYTES = 0xC5;
	public static int USB_CMD_READ_4BYTES_RESPONSE_LENGTH = 1;

	public static int USB_CMD_WRITE_BYTE = 0xC6;
	public static int USB_CMD_WRITE_BYTE_RESPONSE_LENGTH = 1;
	public static int USB_CMD_WRITE_BYTE_ACK_BYTE = 0xC6;
	public static int USB_CMD_WRITE_2BYTES = 0xC7;
	public static int USB_CMD_WRITE_2BYTES_RESPONSE_LENGTH = 1;
	public static int USB_CMD_WRITE_2BYTES_ACK_BYTE = 0xC7;
	public static int USB_CMD_WRITE_4BYTES = 0xC8;
	public static int USB_CMD_WRITE_4BYTES_RESPONSE_LENGTH = 1;
	public static int USB_CMD_WRITE_4BYTES_ACK_BYTE = 0xC8;

	public static int USB_CMD_RELOAD_EEPROM = 0xC9;
	public static int USB_CMD_RELOAD_EEPROM_RESPONSE_LENGTH = 1;
	public static int USB_CMD_RELOAD_EEPROM_ACK_BYTE = 0xC9;
	/*
	 * save config triggers reset
	 */
	public static int USB_CMD_SAVE_CONFIG = 0xCE;
	public static int USB_CMD_SAVE_CONFIG_RESPONSE_LENGTH = 1;
	public static int USB_CMD_SAVE_CONFIG_ACK_BYTE = 0xCE;

	/* restore hardcoded config values */
	public static int USB_CMD_RESTORE_CONFIG = 0xCF;
	public static int USB_CMD_RESTORE_CONFIG_RESPONSE_LENGTH = 1;
	public static int USB_CMD_RESTORE_CONFIG_ACK_BYTE = 0xCF;

	/*******************/

	public static int USB_CMD_RESET = 0xFF;
	public static int USB_CMD_RESET_RESPONSE_LENGTH = 1;
	public static int USB_CMD_RESET_ACK_BYTE = 0xFF;

	/* receive custom frame */
	public static int USB_CMD_SEND_CUSTOM_FRAME = 0x15;
	public static int USB_CMD_SEND_CUSTOM_FRAME_EOF = 0x00;

	
	/* public sensor values **/
	/* Accelerometer */
	public float accelX;
	public float accelY;
	public float accelZ;
	/* Gyro */
	public float gyroX;
	public float gyroY;
	public float gyroZ;
	/* magnetometer */
	public float magnX;
	public float magnY;
	public float magnZ;
	/* angles */
	public float angleX;
	public float angleY;
	public float angleZ;
	/* angle sp */
	public float angleSPX;
	public float angleSPY;
	public float angleSPZ;
	/* velocity */
	public float velocityX;
	public float velocityY;
	public float velocityZ;
	/* velocity sp */
	public float velocitySPX;
	public float velocitySPY;
	public float velocitySPZ;
	/* height */
	public float height;
	public float heightRel;
	public float heightDelta;
	/* rc values */
	public float rcNick;
	public float rcRoll;
	public float rcYaw;
	public float rcThrottle;
	public float rcLinPoti;
	public boolean rcEnableMotors;
	public boolean rcSwitch;
	/* motor values */
	public float motor1;
	public float motor2;
	public float motor3;
	public float motor4;
	/* motor sp */
	public float motor1SP;
	public float motor2SP;
	public float motor3SP;
	public float motor4SP;
	/* cpu */
	public float cpuLoad;
	/* battery */
	public float batteryVoltage;
	/* temperature */
	public float temperature;
	
	
	
	public enum CUSTOM_FRAME_IDENTIFIERS {
		// wenn hier was geändert wird dann bitte auch unten in der switch case #scheissjava
		GYRO(0x01, 12), ACCEL(0x02, 12), MAGNETOMETER(
				0x03, 12), ANGLE(0x04, 12), ANGLE_SP(0x05, 12), VELOCITY(
				0x06, 12), VELOCITY_SP(0x07, 12), HEIGHT(0x08,
				12), RC(0x09, 22), MOTOR(0x0A, 16), MOTOR_SP(
				0x0B, 12), CPU(0x0C, 4), AKKU(0x0D, 4), TEMP(
				0x0E, 4), BUFFER_OVERRUN(0xFF, 1);

		private final int i;
		private final int responseLength;

		private CUSTOM_FRAME_IDENTIFIERS(int i, int responseLength) {
			this.i = i;
			this.responseLength = responseLength;
		}

		public int getIdentifier() {
			return i;
		}

		public int getResponseLength() {
			return responseLength;
		}
	}

	private SerialPort port;
	private boolean portIsBusy;
	private int frameBufferSize;

	public QuadrocopterCommunicator(int frameBufferSize) {
		this.port = port;
		portIsBusy = false;
		this.frameBufferSize = frameBufferSize;
	}

	private boolean checkTimeout(LocalTime begin, long timeout_in_ms) {
		if (LocalTime.now().isAfter(
				begin.plus(timeout_in_ms, ChronoUnit.MILLIS))) {
			return true;
		} else {
			return false;
		}
	}

	public void setPort( SerialPort port ) {
		this.port = port;
	}
	
	private byte[] getReturnData(boolean communicationSuccessful, byte[] data) {
		byte[] tmp = new byte[data.length + 1];
		tmp = data;
		// letztes byte indiziert Timeout
		if (communicationSuccessful) {
			tmp[tmp.length - 1] = (byte) COMMUNICATION_SUCCESSFUL;
		} else {
			tmp[tmp.length - 1] = (byte) COMMUNICATION_TIMEOUT;
		}

		return tmp;
	}

	// read from quadrocopter
	private byte[] readFromQuadrocopter(int command, int responseLength,
			long timeout_in_ms) {
		try {
			if (!portIsBusy) {
				// Daten anfordern
				portIsBusy = true;
				port.writeByte((byte) command);

				// Warten auf Daten
				LocalTime begin = LocalTime.now();
				while (port.getInputBufferBytesCount() != responseLength) {
					// warte bis alle bytes da sind
					// auf Timeout prÃ¼fen
					if (checkTimeout(begin, timeout_in_ms)) {

					}
				}
				byte[] data = getReturnData(true, port.readBytes());
				portIsBusy = false;
				return data;
			}
			return getReturnData(false, new byte[1]);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			portIsBusy = false;
			return getReturnData(false, new byte[1]);
		}
	}

	// write to quadrocopter
	private boolean writeToQuadrocopter(int command, byte[] data, byte ackByte,
			long timeout_in_ms) {
		try {
			if (!portIsBusy) {
				// Daten anfordern
				portIsBusy = true;
				port.writeByte((byte) command);
				port.writeBytes(data);

				// Warten auf Daten
				LocalTime begin = LocalTime.now();
				while (port.getInputBufferBytesCount() != 1) {
					// warte bis alle bytes da sind
					// auf Timeout prÃ¼fen
					if (checkTimeout(begin, timeout_in_ms)) {
						portIsBusy = false;
						return false;
					}
				}

				if (port.readBytes()[0] == ackByte) {
					portIsBusy = false;
					return true;
				} else {
					portIsBusy = false;
					return false;
				}
			}
			portIsBusy = false;
			return false;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			portIsBusy = false;
			return false;
		}
	}

	// needed for floatToByteArray(..)
	private static byte[] float2ByteArray(float value) {
		return ByteBuffer.allocate(4).putFloat(value).array();
	}

	// convert float to byte array
	public byte[] floatToByteArray(float f, byte[] byteArray, int offset) {
		// int integer = Float.floatToIntBits( f );
		// byteArray[0 + offset] = (byte)(integer>>>24);
		// byteArray[1 + offset] = (byte)(integer>>>16);
		// byteArray[2 + offset] = (byte)(integer>>>8);
		// byteArray[3 + offset] = (byte)(integer>>>0);

		byte tmp[] = float2ByteArray(f);
		byteArray[offset] = tmp[3];
		byteArray[offset + 1] = tmp[2];
		byteArray[offset + 2] = tmp[1];
		byteArray[offset + 3] = tmp[0];
		return byteArray;
	}

	public float byteArrayToFloat(byte[] data, int offset) {
		return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.LITTLE_ENDIAN)
				.getFloat();
	}

	// returns the status byte of the quadrocopter
	public byte[] getStatusFlags() {
		return readFromQuadrocopter(USB_CMD_GLOBAL_FLAGS,
				USB_CMD_GLOBAL_FLAGS_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
	}

	// returns the sensor data of the quadrocopter
	public byte[] getSensorData() {
		return readFromQuadrocopter(USB_CMD_SEND_SENSOR_DATA,
				USB_CMD_SEND_SENSOR_DATA_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
	}

	// returns the flight data of the quadrocopter
	public byte[] getFlightData() {
		return readFromQuadrocopter(USB_CMD_SEND_FLIGHT_DATA,
				USB_CMD_SEND_FLIGHT_DATA_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
	}

	// returns the system state of the quadrocopter
	public byte[] getSystemState() {
		return readFromQuadrocopter(USB_CMD_SEND_SYSTEM_STATE,
				USB_CMD_SEND_SYSTEM_STATE_RESPONSE_LENGTH,
				COMMUNICATION_TIMEOUT);
	}

	// returns the gps time of the quadrocopter
	public byte[] getGPSTime() {
		return readFromQuadrocopter(USB_CMD_SEND_GPS_DATA_TIME,
				USB_CMD_SEND_GPS_DATA_TIME_RESPONSE_LENGTH,
				COMMUNICATION_TIMEOUT);
	}

	// returns the gps position of the quadrocopter
	public byte[] getGPSPosition() {
		return readFromQuadrocopter(USB_CMD_SEND_GPS_DATA_POSITION,
				USB_CMD_SEND_GPS_DATA_POSITION_RESPONSE_LENGTH,
				COMMUNICATION_TIMEOUT);
	}

	/*
	 * quadrocopter enters or leaves config mode returns true if successful
	 */
	public boolean toggleConfigMode() {
		byte[] tmp = readFromQuadrocopter(USB_CMD_CONFIG_MODE,
				USB_CMD_CONFIG_MODE_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
		if (tmp[0] == 1 && tmp[2] == USB_CMD_CONFIG_MODE_ACK_BYTE) {
			return true;
		} else {
			return false;
		}
	}

	// returns the configuration of the quadrocopter
	public byte[] getConfig() {
		return readFromQuadrocopter(USB_CMD_GET_CONFIG,
				USB_CMD_GET_CONFIG_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
	}

	// updates the quadrocopter configuration
	public boolean updateConfig(byte[] data) {
		return writeToQuadrocopter(USB_CMD_UPDATE_CONFIG, data,
				(byte) USB_CMD_UPDATE_CONFIG_ACK_BYTE, COMMUNICATION_TIMEOUT);
	}

	// read 1 byte from eeprom
	public byte[] readByteFromEEPROM() {
		return readFromQuadrocopter(USB_CMD_READ_BYTE,
				USB_CMD_READ_BYTE_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
	}

	// read 2 bytes from eeprom
	public byte[] read2BytesFromEEPROM() {
		return readFromQuadrocopter(USB_CMD_READ_2BYTES,
				USB_CMD_READ_2BYTES_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
	}

	// read 4 bytes from eeprom
	public byte[] read4BytesFromEEPROM() {
		return readFromQuadrocopter(USB_CMD_READ_4BYTES,
				USB_CMD_READ_4BYTES_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
	}

	// write 1 byte to eeprom
	public boolean writeByteToEEPROM(int addressMSB, int addressLSB, byte data) {
		byte[] sendData = new byte[3];
		sendData[0] = (byte) addressMSB;
		sendData[1] = (byte) addressLSB;
		sendData[2] = data;
		return writeToQuadrocopter(USB_CMD_WRITE_BYTE, sendData,
				(byte) USB_CMD_WRITE_BYTE_ACK_BYTE, COMMUNICATION_TIMEOUT);
	}

	// write 2 bytes to eeprom
	public boolean write2BytesToEEPROM(int addressMSB, int addressLSB,
			byte[] data) {
		byte[] sendData = new byte[4];
		sendData[0] = (byte) addressMSB;
		sendData[1] = (byte) addressLSB;
		sendData[2] = data[0];
		sendData[3] = data[1];
		return writeToQuadrocopter(USB_CMD_WRITE_2BYTES, sendData,
				(byte) USB_CMD_WRITE_2BYTES_ACK_BYTE, COMMUNICATION_TIMEOUT);
	}

	// write 4 bytes to eeprom
	public boolean write4BytesToEEPROM(int addressMSB, int addressLSB,
			byte[] data) {
		byte[] sendData = new byte[5];
		sendData[0] = (byte) addressMSB;
		sendData[1] = (byte) addressLSB;
		sendData[2] = data[0];
		sendData[3] = data[1];
		sendData[4] = data[2];
		sendData[5] = data[3];
		return writeToQuadrocopter(USB_CMD_WRITE_2BYTES, sendData,
				(byte) USB_CMD_WRITE_2BYTES_ACK_BYTE, COMMUNICATION_TIMEOUT);
	}

	// reload configuration from eeprom
	public boolean reloadConfigurationFromEEPROM() {
		byte[] tmp = readFromQuadrocopter(USB_CMD_RELOAD_EEPROM,
				USB_CMD_RELOAD_EEPROM_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
		if (tmp[0] == 1 && tmp[2] == USB_CMD_RELOAD_EEPROM_ACK_BYTE) {
			return true;
		} else {
			return false;
		}
	}

	// save current configuration to eeprom
	public boolean saveConfigurationToEEPROM() {
		byte[] tmp = readFromQuadrocopter(USB_CMD_SAVE_CONFIG,
				USB_CMD_SAVE_CONFIG_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
		if (tmp[0] == 1 && tmp[2] == USB_CMD_SAVE_CONFIG_ACK_BYTE) {
			return true;
		} else {
			return false;
		}
	}

	// load configuration from code
	public boolean loadHardcodedConfiguration() {
		byte[] tmp = readFromQuadrocopter(USB_CMD_RESTORE_CONFIG,
				USB_CMD_RESTORE_CONFIG_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
		if (tmp[0] == 1 && tmp[2] == USB_CMD_RESTORE_CONFIG_ACK_BYTE) {
			return true;
		} else {
			return false;
		}
	}

	// reset quadrocopter
	public boolean resetQuadrocopter() {
		byte[] tmp = readFromQuadrocopter(USB_CMD_RESET,
				USB_CMD_RESET_RESPONSE_LENGTH, COMMUNICATION_TIMEOUT);
		if (tmp[0] == 1 && tmp[2] == USB_CMD_RESET_ACK_BYTE) {
			return true;
		} else {
			return false;
		}
	}

	public void getCustomFrame(CUSTOM_FRAME_IDENTIFIERS[] frameContent) {
		if (!portIsBusy) {
			// Daten anfordern
			portIsBusy = true;
			
			int responseLength = 0;
			// Index des letzten Elementes des letzten Custom Frames
			int lastFrameIndex = 0;
			
			for (int i = 0; i < frameContent.length; i++) {
				responseLength += frameContent[i].getResponseLength();
				
				/*
				 * 3 Faelle:
				 *  - 1: Frame noch nicht voll, i noch nicht maximal ==> weiter iterieren
				 *  - 2: Frame noch nicht voll, aber i maximal ==> Frame anfordern
				 *  - 3: Frame wuerde in naechster Iteration ueberlaufen, aber i noch nicht maximal ==> Frame anfordern
				 */
				if( ((i+1 < frameContent.length) && ((responseLength+frameContent[i+1].getResponseLength()) > frameBufferSize)) || (i == frameContent.length-1) ) {
					// Frame anfordern
					
					// Outputbuffer anlegen
					// Laenge: i + 1 - lastFrameIndex + 2
					// i + 1 				^= aktueller Index
					// lastFrameIndex	^= letzter Index, der noch im letzten Custom Frame war
					// +2				^= vorne und hinten muessen noch Anfangs- und Endwort angehaengt werden
					byte[] outputbuffer = new byte[i + 1 - lastFrameIndex + 2];
					outputbuffer[0] = (byte)USB_CMD_SEND_CUSTOM_FRAME;
					// Frame fuellen
					// bei 1 beginnen, da Stelle 0 USB_CMD_SEND_CUSTOM_FRAME ist
					// bis i + 1 - lastFrameIndex gehen, damit nicht zuviele Identifier in das array geladen werden
					for(int k = 1; k <= i + 1 - lastFrameIndex; k++) {
						// lastFrameIndex + k -1: nur die Identifier anhaengen, die nach dem letzten angeforderten Identifier stehen anfordern
						outputbuffer[k] = (byte)frameContent[lastFrameIndex + k - 1].getIdentifier();
					}
					outputbuffer[outputbuffer.length-1] = (byte)USB_CMD_SEND_CUSTOM_FRAME_EOF;
					
					try {
						// custom frame anfordern
						port.writeBytes(outputbuffer);

						// Warten auf Daten
						LocalTime begin = LocalTime.now();
						while (port.getInputBufferBytesCount() < responseLength) {
							// warte bis alle bytes da sind
							// auf Timeout prÃ¼fen
							if (checkTimeout(begin, COMMUNICATION_TIMEOUT)) {
								portIsBusy = false;
								return;
							}							
						}
						
						// Daten auslesen
						//inputBuffer.add(port.readBytes());
						byte[] data = new byte[responseLength];
						data = port.readBytes();
						int offset = 0;
						for(int k = lastFrameIndex; k < i + 1; k++) {
							switch ( frameContent[k].getIdentifier() ) {
								/* Gyro */
								case 0x01:
									this.gyroX = byteArrayToFloat(data, offset) ;
									offset += 4;
									this.gyroY = byteArrayToFloat(data, offset);
									offset += 4;
									this.gyroZ = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* Accelerometer */
								case 0x02:
									this.accelX = byteArrayToFloat(data, offset);
									offset += 4;
									this.accelY = byteArrayToFloat(data, offset);
									offset += 4;
									this.accelZ = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* Magnetometer */
								case 0x03:
									this.magnX = byteArrayToFloat(data, offset);
									offset += 4;
									this.magnY = byteArrayToFloat(data, offset);
									offset += 4;
									this.magnZ = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* angles */
								case 0x04:
									this.angleX = byteArrayToFloat(data, offset);
									offset += 4;
									this.angleY = byteArrayToFloat(data, offset);
									offset += 4;
									this.angleZ = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* angle sp */
								case 0x05:
									this.angleSPX = byteArrayToFloat(data, offset);
									offset += 4;
									this.angleSPY = byteArrayToFloat(data, offset);
									offset += 4;
									this.angleSPZ = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* velocity */
								case 0x06:
									this.velocityX = byteArrayToFloat(data, offset);
									offset += 4;
									this.velocityY = byteArrayToFloat(data, offset);
									offset += 4;
									this.velocityZ = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* velocity sp */
								case 0x07:
									this.velocitySPX = byteArrayToFloat(data, offset);
									offset += 4;
									this.velocitySPY = byteArrayToFloat(data, offset);
									offset += 4;
									this.velocitySPZ = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* height */
								case 0x08:
									this.height = byteArrayToFloat(data, offset);
									offset += 4;
									this.heightRel = byteArrayToFloat(data, offset);
									offset += 4;
									this.heightDelta = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* rc values */
								case 0x09:
									this.rcNick = byteArrayToFloat(data, offset);
									offset += 4;
									this.rcRoll = byteArrayToFloat(data, offset);
									offset += 4;
									this.rcYaw = byteArrayToFloat(data, offset);
									offset += 4;
									this.rcThrottle = byteArrayToFloat(data, offset);
									offset += 4;
									this.rcLinPoti = byteArrayToFloat(data, offset);
									offset += 4;
									
									if(data[offset] == 0) {
										this.rcEnableMotors = false;
									} else {
										this.rcEnableMotors = true;
									}
									offset += 1;
									
									if(data[offset] == 0) {
										this.rcSwitch = false;
									} else {
										this.rcSwitch = true;
									}
									offset += 1;
									
									break;
								/* motor values */
								case 0x0A:
									this.motor1 = byteArrayToFloat(data, offset);
									offset += 4;
									this.motor2 = byteArrayToFloat(data, offset);
									offset += 4;
									this.motor3 = byteArrayToFloat(data, offset);
									offset += 4;
									this.motor4 = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* motor sp */
								case 0x0B:
									this.motor1SP = byteArrayToFloat(data, offset);
									offset += 4;
									this.motor2SP = byteArrayToFloat(data, offset);
									offset += 4;
									this.motor3SP = byteArrayToFloat(data, offset);
									offset += 4;
									this.motor4SP = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* cpu */
								case 0x0C:
									this.cpuLoad = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* battery */
								case 0x0D:
									this.batteryVoltage = byteArrayToFloat(data, offset);
									offset += 4;
									break;
								/* temperature */
								case 0x0E:
									this.temperature = byteArrayToFloat(data, offset);
									offset += 4;
									break;
//								default:
//									System.out.println("Unknown identifier!");
//									break;
							}
						}
						
					} catch (Exception e) {
						System.out.println(e.getMessage());
						portIsBusy = false;
						return;
					}
					// responseLength zuruecksetzen
					responseLength = 0;
					// lastFrameIndex 
					lastFrameIndex = i + 1;
				}
			}
			portIsBusy = false;
		}
	}
}
