import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;


public class Messwertfenster extends JFrame {
	JLabel lblMessung;
	public Messwertfenster() {
		setTitle("Messwerte");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 258, 331);
		
		lblMessung = new JLabel("<html>\r\n<h2>Messung:</h2>\r\n<table>\r\n\t<tr>\r\n\t\t<td>\r\n\t\t\t<b>Motor 1: </b><br>\r\n\t\t\t<b>Motor 2: </b><br>\r\n\t\t\t<b>Motor 3: </b><br>\r\n\t\t\t<b>Motor 4: </b><br>\r\n\t\t</td>\r\n\t\t<td>\r\n\t\t\t0.0 %<br>\r\n\t\t\t0.0 %<br>\r\n\t\t\t0.0 %<br>\r\n\t\t\t0.0 %<br>\r\n\t\t</td>\r\n\t</tr>\r\n\t<tr>\r\n\t\t<td>\t\t\t\r\n\t\t\t<b>Winkel X: </b><br>\r\n\t\t\t<b>Winkel Y: </b><br>\r\n\t\t\t<b>Winkel Z: </b><br>\r\n\t\t</td>\r\n\t\t<td>\r\n\t\t\t0.0 \u00B0<br>\r\n\t\t\t0.0 \u00B0<br>\r\n\t\t\t0.0 \u00B0<br>\r\n\t\t</td>\r\n\t</tr>\r\n\t<tr>\r\n\t\t<td>\r\n\t\t\t<b>Beschl. X: </b><br>\r\n\t\t\t<b>Beschl. Y: </b><br>\r\n\t\t\t<b>Beschl. Z: </b><br>\r\n\t\t</td>\r\n\t\t<td>\r\n\t\t\t0.0 m/s\u00B2<br>\r\n\t\t\t0.0 m/s\u00B2<br>\r\n\t\t\t0.0 m/s\u00B2<br>\r\n\t\t</td>\r\n\t</tr>\r\n\t<tr>\r\n\t\t<td>\r\n\t\t\t<b>Rate X: </b><br>\r\n\t\t\t<b>Rate Y: </b><br>\r\n\t\t\t<b>Rate Z: </b><br>\r\n\t\t</td>\r\n\t\t<td>\r\n\t\t\t0.0 \u00B0/s<br>\r\n\t\t\t0.0 \u00B0/s<br>\r\n\t\t\t0.0 \u00B0/s<br>\r\n\t\t</td>\r\n\t</tr>\r\n</table>\r\n</html>");
		lblMessung.setVerticalAlignment(SwingConstants.TOP);
		lblMessung.setHorizontalAlignment(SwingConstants.LEFT);
		getContentPane().add(lblMessung, BorderLayout.CENTER);
	}
	
	public void setLabelText( float[] values ) {
		lblMessung.setText("<html>"
				+ "<h2>Messung:</h2>"
				+	"<table>"
				+		"<tr>"
				+			"<td>"
				+				"<b>Motor 1: </b><br>"
				+				"<b>Motor 2: </b><br>"
				+				"<b>Motor 3: </b><br>"
				+				"<b>Motor 4: </b><br>"
				+			"</td>"
				+			"<td>"
				+				String.valueOf( values[0]) + " %<br>"
				+				String.valueOf( values[1]) + " %<br>"
				+				String.valueOf( values[2]) + " %<br>"
				+				String.valueOf( values[3]) + " %<br>"
				+			"</td>"
				+		"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Winkel X: </b><br>"
				+			"<b>Winkel Y: </b><br>"
				+			"<b>Winkel Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+			String.valueOf( values[4]) + " °<br>"
				+			String.valueOf( values[5]) + " °<br>"
				+			String.valueOf( values[6]) + " °<br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Beschl. X: </b><br>"
				+ 			"<b>Beschl. Y: </b><br>"
				+			"<b>Beschl. Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+				String.valueOf( values[7]) + " m/s²<br>"
				+				String.valueOf( values[8]) + " m/s²<br>"
				+				String.valueOf( values[9]) + " m/s²<br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Rate X: </b><br>"
				+			"<b>Rate Y: </b><br>"
				+			"<b>Rate Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+			String.valueOf( values[10]) + " °/s<br>"
				+			String.valueOf( values[11]) + " °/s<br>"
				+			String.valueOf( values[12]) + " °/s<br>"
				+		"</td>"
				+	"</tr>"
				+"</table>"
				+"</html>");
	}

}
