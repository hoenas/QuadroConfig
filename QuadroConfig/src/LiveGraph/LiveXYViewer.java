package LiveGraph;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class LiveXYViewer extends Canvas{
	private java.util.List<Dataset> graphlist = new ArrayList<Dataset>();
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
	
	public LiveXYViewer( Color backgroundColor, Color foregroundColor,int crossSize, int crossThickness, int max, int offset, Color rasterColor, boolean useRaster, int rasterWidth, int rasterLinesCount, boolean drawLinkLine) {
		this.backgroundColor = backgroundColor;
		this.color = foregroundColor;
		this.crossThickness = crossThickness;
		crossStroke = new BasicStroke( crossThickness );
		this.crossSize = crossSize;
		this.max = max;
		this.offset = offset;
		this.rasterColor = rasterColor;
		this.useRaster = useRaster;
		this.rasterWidth = rasterWidth;
		this.rasterLinesCount = rasterLinesCount;
		this.drawLinkLine = drawLinkLine;
		rasterStroke = new BasicStroke( rasterWidth );	
		bufferstrategy = null;
	}
	
	public void addGraph( Dataset dataset ) {
		graphlist.add(dataset);
	}
	
	public void update( int x, int y) {
		if( this.isVisible() && bufferstrategy == null ) {
			this.createBufferStrategy(2);
			bufferstrategy = this.getBufferStrategy();
		} else {
			Graphics2D g2 = (Graphics2D) bufferstrategy.getDrawGraphics();
			// Graph loeschen
			g2.setColor( backgroundColor );
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			// Raster zeichnen
			if( useRaster ) {
				g2.setStroke( rasterStroke );
				g2.setColor( rasterColor );
				
				for(int i = 1; i < rasterLinesCount - 1; i++) {
					g2.drawLine( i * this.getWidth() / (rasterLinesCount - 1), 0, i * this.getWidth() / (rasterLinesCount - 1), this.getHeight() );
					g2.drawLine( 0, i * this.getHeight() / (rasterLinesCount - 1), this.getWidth(), i * this.getWidth() / (rasterLinesCount - 1) );
				}
			}
						
			
			// Offset berechnen
			x += offset;
			y += offset;
			
			// Verbindungslinie zeichnen
			g2.setStroke( crossStroke );
			g2.setColor( color );
			if(drawLinkLine) {
				g2.drawLine(this.getWidth() / 2, this.getHeight() / 2, this.getWidth() - x * this.getWidth() / max, this.getHeight() -  y * this.getHeight() / max);
			}
			// Kreuz zeichnen
			g2.drawLine(this.getWidth() - x * this.getWidth() / max - crossSize / 2, this.getHeight() -  y, this.getWidth() - x * this.getWidth() / max + crossSize / 2, this.getHeight() - y );
			g2.drawLine(this.getWidth() - x, this.getHeight() -  y * this.getHeight() / max - crossSize / 2, this.getWidth() - x, this.getHeight() -  y * this.getHeight() / max + crossSize / 2);
			
			bufferstrategy.show();
			Toolkit.getDefaultToolkit().sync();
		}
	}

	public void setRasterThickness(int crossThickness) {
		this.crossThickness = crossThickness;
		this.crossStroke = new BasicStroke( crossThickness );
	}
	
	public void setRasterWidth(int rasterWidth) {
		this.rasterWidth = rasterWidth;
		this.rasterStroke = new BasicStroke( rasterWidth );
	}
}
