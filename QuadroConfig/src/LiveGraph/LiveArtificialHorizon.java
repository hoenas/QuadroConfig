package LiveGraph;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class LiveArtificialHorizon extends Canvas{
	private java.util.List<Dataset> graphlist = new ArrayList<Dataset>();
	private Color color;
	private Color backgroundColor;
	private boolean useRaster;
	private Color rasterColor;
	private BufferStrategy bufferstrategy;
	private int rasterWidth;
	private BasicStroke stroke;
	
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
	
	public LiveArtificialHorizon( Color backgroundColor, Color foregroundColor, Color rasterColor, boolean useRaster, int rasterWidth) {
		this.backgroundColor = backgroundColor;
		this.color = foregroundColor;
		this.rasterColor = rasterColor;
		this.useRaster = useRaster;
		this.rasterWidth = rasterWidth;
		stroke = new BasicStroke( rasterWidth );	
	}
	
	public void update( int angle) {
		
		if( this.isVisible() && bufferstrategy == null ) {
			this.createBufferStrategy(2);
			bufferstrategy = this.getBufferStrategy();
		}
		
		Graphics2D g2 = (Graphics2D) bufferstrategy.getDrawGraphics();
		// Graph loeschen
		g2.setColor( backgroundColor );
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// kuenstlichen Horizont zeichnen		
		g2.setColor( color );		
		g2.fillArc(0, 0, this.getWidth(), this.getHeight(), angle + 180, 180);
		
		// Umrandung zeichnen
		if( useRaster ) {
			g2.setStroke( stroke );
			g2.setColor( rasterColor );
			g2.drawArc(0, 0, this.getWidth(), this.getHeight(), 0, 360);
			g2.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);
			for(int i = 0; i < 30; i++) {
				g2.drawArc(10, 10, this.getWidth() - 20, this.getHeight() - 20, 12*i - 1, 2);	
			}
		}
		
		bufferstrategy.show();
		Toolkit.getDefaultToolkit().sync();
	}


	public void setRasterWidth(int rasterWidth) {
		this.rasterWidth = rasterWidth;
		this.stroke = new BasicStroke( rasterWidth );
	}
}
