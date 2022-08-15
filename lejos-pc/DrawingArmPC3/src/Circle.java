import java.util.ArrayList;
import java.awt.Graphics;

public class Circle extends Shape	// A "basic" shape (has no subshapes) which represents a circle made of points
{
	private double[] xcoords;
	private double[] ycoords;
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
		xcoords = new double[numPoints];
		ycoords = new double[numPoints];
		for(int i=0; i<numPoints; i++)							// add points around the circumference of the circle
		{
			xcoords[i] = midX+r*Math.cos(t);
			ycoords[i] = midY+r*Math.sin(t);
			t+=dT;
		}
	}


	public double getStartX()
	{
		return startX;
	}

	public double getStartY()
	{
		return startY;
	}

	public double getEndX()
	{
		return endX;
	}

	public double getEndY()
	{
		return endY;
	}

	public int getPoints()
	{
		return xcoords.length;
	}

	public String toString()
	{
		return "Circle:"+startX+","+startY+","+endX+","+endY;
	}

	public Point[] toPointArray()
	{
		Point[] points = new Point[getPoints()];
		for(int i=0; i<getPoints(); i++)
			points[i] = new Point(xcoords[i], ycoords[i]);
		return points;
	}

	public void draw(Graphics g, double x0, double y0)
	{
		g.drawOval((int)(midX+x0-r), (int)(midY+y0-r), (int)(2*r), (int)(2*r));
	}

	public void drawAsPoints(Graphics g, double x0, double y0)
	{
		for(int i=0; i<getPoints(); i++)
			g.drawLine((int)(xcoords[i]+x0), (int)(ycoords[i]+y0), (int)(xcoords[i]+x0), (int)(ycoords[i]+y0));
	}
}
