package LiveGraph;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.text.StyledEditorKit.ForegroundAction;
import javax.xml.crypto.Data;

import org.w3c.dom.css.RGBColor;

public class LiveXYViewer extends Canvas {
	private Dataset datasetX;
	private Dataset datasetY;
	private Color color;
	private Color backgroundColor;
	private int crossSize;
	private int crossThickness;
	private int max;
	private int offset;
	private boolean useRaster;
	private Color rasterColor;
	private BufferStrategy bufferstrategy;
	private int rasterWidth;
	private int rasterLinesCount;
	private BasicStroke rasterStroke;
	private BasicStroke crossStroke;
	private boolean drawLinkLine;
	private Color pathColor;
	private boolean fadePathColor;
	private int pathLength;

	public boolean isUseRaster() {
		return useRaster;
	}

	public void setUseRaster(boolean useRaster) {
		this.useRaster = useRaster;
	}

	public Color getRasterColor() {
		return rasterColor;
	}

	public void setRastertColor(Color rasterColor) {
		this.rasterColor = rasterColor;
	}

	public LiveXYViewer(Color backgroundColor, Color foregroundColor, int crossSize,
			int crossThickness, int max, int offset, Color rasterColor, boolean useRaster, int rasterWidth,
			int rasterLinesCount, boolean drawLinkLine, int pathLength, Color pathColor) {
		this.backgroundColor = backgroundColor;
		this.color = foregroundColor;
		this.crossThickness = crossThickness;
		crossStroke = new BasicStroke(crossThickness);
		this.crossSize = crossSize;
		this.max = max;
		this.offset = offset;
		this.rasterColor = rasterColor;
		this.useRaster = useRaster;
		this.rasterWidth = rasterWidth;
		this.rasterLinesCount = rasterLinesCount;
		this.drawLinkLine = drawLinkLine;
		this.pathLength = pathLength;
		this.pathColor = pathColor;
		this.fadePathColor = fadePathColor;
		rasterStroke = new BasicStroke(rasterWidth);
		bufferstrategy = null;
	}

	public void update() {
		if (this.isVisible() && bufferstrategy == null) {
			this.createBufferStrategy(2);
			bufferstrategy = this.getBufferStrategy();
		} else {
			Graphics2D g2 = (Graphics2D) bufferstrategy.getDrawGraphics();
			// Graph loeschen
			g2.setColor(backgroundColor);
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());

			// Raster zeichnen
			if (useRaster) {
				g2.setStroke(rasterStroke);
				g2.setColor(rasterColor);

				for (int i = 1; i < rasterLinesCount - 1; i++) {
					g2.drawLine(i * this.getWidth() / (rasterLinesCount - 1), 0,
							i * this.getWidth() / (rasterLinesCount - 1), this.getHeight());
					g2.drawLine(0, i * this.getHeight() / (rasterLinesCount - 1), this.getWidth(),
							i * this.getWidth() / (rasterLinesCount - 1));
				}
			}

			// Variablen anlegen
			int x1 = 0;
			int x2 = 0;
			int y1 = 0;
			int y2 = 0;

			// Pfad zeichnen
			g2.setColor(pathColor);
			for (int i = 1; i <= pathLength; i++) {
				x1 = (int) datasetX.getBuffer()[datasetX.getBuffer().length - i] + offset;
				x2 = (int) datasetX.getBuffer()[datasetX.getBuffer().length - i - 1] + offset;
				y1 = (int) datasetY.getBuffer()[datasetY.getBuffer().length - i] + offset;
				y2 = (int) datasetY.getBuffer()[datasetY.getBuffer().length - i - 1] + offset;
				g2.drawLine(this.getWidth() - x1 * this.getWidth() / max,
						this.getHeight() - y1 * this.getHeight() / max, this.getWidth() - x2 * this.getWidth() / max,
						this.getHeight() - y2 * this.getHeight() / max);
			}

			// Offset berechnen
			int x = (int) datasetX.getBuffer()[datasetX.getBuffer().length - 1];
			int y = (int) datasetY.getBuffer()[datasetY.getBuffer().length - 1];
			x += offset;
			y += offset;

			// Verbindungslinie zeichnen
			g2.setStroke(crossStroke);
			g2.setColor(color);
			if (drawLinkLine) {
				g2.drawLine(this.getWidth() / 2, this.getHeight() / 2, this.getWidth() - x * this.getWidth() / max,
						this.getHeight() - y * this.getHeight() / max);
			}
			// Kreuz zeichnen
			g2.drawLine(this.getWidth() - x * this.getWidth() / max - crossSize / 2, this.getHeight() - y,
					this.getWidth() - x * this.getWidth() / max + crossSize / 2, this.getHeight() - y);
			g2.drawLine(this.getWidth() - x, this.getHeight() - y * this.getHeight() / max - crossSize / 2,
					this.getWidth() - x, this.getHeight() - y * this.getHeight() / max + crossSize / 2);

			bufferstrategy.show();
			Toolkit.getDefaultToolkit().sync();
		}
	}

	public void setRasterThickness(int crossThickness) {
		this.crossThickness = crossThickness;
		this.crossStroke = new BasicStroke(crossThickness);
	}

	public void setRasterWidth(int rasterWidth) {
		this.rasterWidth = rasterWidth;
		this.rasterStroke = new BasicStroke(rasterWidth);
	}

	public void setDatasetX(Dataset datasetX) {
		this.datasetX = datasetX;
	}

	public void setDatasetY(Dataset datasetY) {
		this.datasetY = datasetY;
	}
}
