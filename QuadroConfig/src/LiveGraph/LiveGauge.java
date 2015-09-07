package LiveGraph;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class LiveGauge extends Canvas{
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
	
	public LiveGauge( Color backgroundColor, Color foregroundColor, Color rasterColor, boolean useRaster, int rasterWidth, int rasterCount, int max, String unit) {
		this.backgroundColor = backgroundColor;
		this.color = foregroundColor;
		this.rasterColor = rasterColor;
		this.useRaster = useRaster;
		this.rasterWidth = rasterWidth;
		this.rasterCount = rasterCount;
		stroke = new BasicStroke( rasterWidth );	
		this.max = max;
		this.unit = unit;
	}
	
	public void update( int altitude) {
		
		if( this.isVisible() && bufferstrategy == null ) {
			this.createBufferStrategy(2);
			bufferstrategy = this.getBufferStrategy();
		} else {
		
			Graphics2D g2 = (Graphics2D) bufferstrategy.getDrawGraphics();
			// Graph loeschen
			g2.setColor( backgroundColor );
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
						
			// Beschriftung zeichnen
			g2.setColor( rasterColor );
			
			int alpha = (int)(Math.atan( this.getWidth() / this.getHeight() ) * 2 * 360 / Math.PI);
			g2.drawArc(0, this.getHeight()/ 2, this.getWidth(), this.getHeight()/ 2, 0, alpha);
			g2.drawArc(this.getWidth()/ 4, 0, this.getWidth() / 2, this.getHeight()/ 4, 0, alpha);
			g2.drawLine(0, this.getHeight()/ 2, this.getWidth()/ 4, 0);
			g2.drawLine(this.getWidth(), this.getHeight()/ 2, this.getWidth() * 3 / 4, 0);
			
			// Skala zeichnen
			if( useRaster ) {
				g2.setStroke( stroke );
				for(int i = 0; i < rasterCount; i++) {
					// g2.drawLine(0, i * (this.getHeight() / (rasterCount-1)), 3, i * (this.getHeight() / (rasterCount-1)) );
					// g2.drawLine(this.getWidth() - 3, i * (this.getHeight() / (rasterCount-1)), this.getWidth(), i * (this.getHeight() / (rasterCount-1)) );
				}
			}
			
			// Nadel zeichnen	
			g2.setColor( color );
			
			
			bufferstrategy.show();
			Toolkit.getDefaultToolkit().sync();
		}
	}


	public void setRasterWidth(int rasterWidth) {
		this.rasterWidth = rasterWidth;
		this.stroke = new BasicStroke( rasterWidth );
	}
}
