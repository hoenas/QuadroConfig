package LiveGraph;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class LiveGauge extends Canvas {
	private Color color;
	private Color panelBackgroundColor;
	private boolean useRaster;
	private Color gaugeBackgroundColor;
	private BufferStrategy bufferstrategy;
	private boolean fillPercentage;
	private int max;

	public LiveGauge(Color panelBackgroundColor, Color gaugeForegroundColor, Color gaugeBackgroundColor, boolean fillPercentage, int max) {
		this.panelBackgroundColor = panelBackgroundColor;
		this.color = gaugeForegroundColor;
		this.gaugeBackgroundColor = gaugeBackgroundColor;
		this.fillPercentage = fillPercentage;
		this.max = max;
	}

	public void update(float percentage) {

		if (this.isVisible() && bufferstrategy == null) {
			this.createBufferStrategy(2);
			bufferstrategy = this.getBufferStrategy();
		} else {

			Graphics2D g2 = (Graphics2D) bufferstrategy.getDrawGraphics();
			// Graph loeschen
			g2.setColor(panelBackgroundColor);
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());

			// Alpha berechnen
			float alpha = percentage / 100.0f * (float) Math.PI;
			// Radius berechnen
			int radius = 0;
			if (this.getWidth() > this.getHeight()) {
				radius = this.getWidth() / 2;
			} else {
				radius = this.getHeight() / 2;
			}

			// Beschriftung zeichnen
			g2.setColor(gaugeBackgroundColor);

			// Hintergrund zeichnen
			g2.fillArc(0, this.getHeight() - radius, 2 * radius, 2 * radius, 0, 180);

			// Nadel zeichnen
			g2.setColor(color);

			// x und y berechnen
			if( fillPercentage ) {
				// Arc zeichnen
				g2.fillArc(0, this.getHeight() - radius, 2 * radius, 2 * radius, 180, -1 * (int)(percentage * 180 / max));
			} else {
				// Zeiger zeichnen
				int x = this.getWidth() / 2 - (int) (Math.cos((double) alpha) * radius);
				int y = this.getHeight() - (int) (Math.sin((double) alpha) * radius);
				
				g2.drawLine(this.getWidth() / 2, this.getHeight(), x, y);
			}
			

			// untere Abdeckung zeichnen
			g2.setColor(panelBackgroundColor);
			g2.fillArc((int) (this.getWidth() / 2 - 0.1f * radius), (int) (this.getHeight() - 0.1f * radius),
					(int) (0.2f * radius), (int) (0.2f * radius), 0, 180);

			bufferstrategy.show();
			Toolkit.getDefaultToolkit().sync();
		}
	}
}
