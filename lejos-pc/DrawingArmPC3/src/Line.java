import java.util.ArrayList;
import java.awt.Graphics;

public class Line extends Shape		// A "basic" shape (has no subshapes) which represents a line made of points
{
	private double[] xcoords;
	private double[] ycoords;

	public Line(double startX, double startY, double endX, double endY)
	{
		double xdist = endX-startX;
		double ydist = endY-startY;
		double d = Math.sqrt( (xdist*xdist) + (ydist*ydist) );

		xcoords = new double[(int)d+1];		//initialize arrays with the number of points that will be on the line
		ycoords = new double[(int)d+1];
		for(int t=0; t<=d; t++)
		{
			xcoords[t] = (1-t/d)*startX + t*endX/d;	//go along the line parametrically calculating new points
			ycoords[t] = (1-t/d)*startY + t*endY/d;
		}
	}

	public double getStartX()
	{
		return xcoords[0];
	}

	public double getStartY()
	{
		return ycoords[0];
	}

	public double getEndX()
	{
		return xcoords[xcoords.length-1];
	}

	public double getEndY()
	{
		return ycoords[ycoords.length-1];
	}

	public int getPoints()
	{
		return xcoords.length;
	}

	public String toString()
	{
		return "Line:  "+getStartX()+","+getStartY()+","+getEndX()+","+getEndY();
	}

	public Point[] toPointArray()
	{
		Point[] points = new Point[getPoints()];
		for(int i=0; i<getPoints(); i++)
			points[i] = new Point(xcoords[i], ycoords[i]);
		return points;
	}

	private int x1,x2,y1,y2;
	public void draw(Graphics g, double x0, double y0)
	{
		x1=(int)getStartX()+(int)x0;
		x2=(int)getEndX()+(int)x0;
		y1=(int)getStartY()+(int)y0;
		y2=(int)getEndY()+(int)y0;
		g.drawLine(x1,y1,x2,y2);
	}

	public void drawAsPoints(Graphics g, double x0, double y0)
	{
		for(int i=0; i<getPoints(); i++)
			g.drawLine((int)(xcoords[i]+x0), (int)(ycoords[i]+y0), (int)(xcoords[i]+x0), (int)(ycoords[i]+y0));
	}
}
