import java.awt.Graphics;

public class Circle extends Shape
{
	private double startX, startY, endX, endY, midX, midY, r;

	public Circle(double startX, double startY, double endX, double endY)	//two points on directly opposite sides of the circle.
	{
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		midX = (startX+endX)/2;									// center x-coord of circle
		midY = (startY+endY)/2;									// center y-coord of circle
		r = Math.sqrt( (midX-startX)*(midX-startX) + (midY-startY)*(midY-startY) );	// radius r of circle
		double c = 2*Math.PI*r;									// circumference c of circle

		double t = Math.atan((startY-midY)/(startX-midX)); 		// start at the angle from the center of the circle to the starting point
		if(startX-midX<0) t+=Math.PI;
		double dT = 2*Math.PI/c;								// change in theta corresponding to a distance of one pixel along the circumference of the circle
		int numPoints = (int)(2*Math.PI/dT);					// number of points that will be generated
		initPoints(numPoints+1);
		for(int i=0; i<numPoints; i++)							// add points around the circumference of the circle
		{
			addPoint( midX+r*Math.cos(t), midY+r*Math.sin(t) );
			t+=dT;
		}
		trimPoints();
	}

	public String getType()
	{
		return "Circle";
	}

	public String toString()
	{
		return "Circle:"+startX+","+startY+","+endX+","+endY;
	}

	public void draw(Graphics g, double x0, double y0, boolean flipped)
	{
		g.drawOval((int)(midX+x0-r), (int)(midY+y0-r), (int)(2*r), (int)(2*r));
	}

	public void drawAsPoints(Graphics g, double x0, double y0, boolean flipped)
	{
		for(int i=0; i<getPoints(); i++)
			g.drawLine((int)(getX(i)+x0), (int)(getY(i)+y0), (int)(getX(i)+x0), (int)(getY(i)+y0));
	}
}
