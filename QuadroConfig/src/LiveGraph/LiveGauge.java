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
	private java.util.List<Dataset> graphlist = new ArrayList<Dataset>();
	private Color color;
	private Color backgroundColor;
	private boolean useRaster;
	private Color rasterColor;
	private BufferStrategy bufferstrategy;
	private int rasterWidth;
	private int rasterCount;
	private int max;
	private BasicStroke stroke;
	private String unit;

	public boolean isUseRaster() {
		return useRaster;
	}

	public void setUseRaster(boolean useRaster) {
		this.useRaster = useRaster;
	}

	public Color getRasterColor() {
		return rasterColor;
	}

	public void setRasterColor(Color rasterColor) {
		this.rasterColor = rasterColor;
	}

	public LiveGauge(Color backgroundColor, Color foregroundColor, Color rasterColor, boolean useRaster,
			int rasterWidth, int rasterCount, int max, String unit) {
		this.backgroundColor = backgroundColor;
		this.color = foregroundColor;
		this.rasterColor = rasterColor;
		this.useRaster = useRaster;
		this.rasterWidth = rasterWidth;
		this.rasterCount = rasterCount;
		stroke = new BasicStroke(rasterWidth);
		this.max = max;
		this.unit = unit;
	}

	public void update(float percentage) {

		if (this.isVisible() && bufferstrategy == null) {
			this.createBufferStrategy(2);
			bufferstrategy = this.getBufferStrategy();
		} else {

			Graphics2D g2 = (Graphics2D) bufferstrategy.getDrawGraphics();
			// Graph loeschen
			g2.setColor(backgroundColor);
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
			g2.setColor(rasterColor);

			// Hintergrund zeichnen
			g2.fillArc(0, this.getHeight() - radius, 2 * radius, 2 * radius, 0, 180);

			// Nadel zeichnen
			g2.setColor(color);

			// x und y berechnen
			int x = x = this.getWidth() / 2 - (int) (Math.cos((double) alpha) * radius);
			int y = y = this.getHeight() - (int) (Math.sin((double) alpha) * radius);

			g2.drawLine(this.getWidth() / 2, this.getHeight(), x, y);

			// untere Abdeckung zeichnen
			g2.setColor(backgroundColor);
			g2.fillArc((int) (this.getWidth() / 2 - 0.1f * radius), (int) (this.getHeight() - 0.1f * radius),
					(int) (0.2f * radius), (int) (0.2f * radius), 0, 180);

			bufferstrategy.show();
			Toolkit.getDefaultToolkit().sync();
		}
	}

	public void setRasterWidth(int rasterWidth) {
		this.rasterWidth = rasterWidth;
		this.stroke = new BasicStroke(rasterWidth);
	}
}
