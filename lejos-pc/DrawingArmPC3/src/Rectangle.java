import java.util.ArrayList;
import java.awt.Graphics;

public class Rectangle extends Shape	// A "complex" shape representing a rectangle composed of four lines
{
	private double startX, startY, endX, endY;

	public Rectangle(double startX, double startY, double endX, double endY)	// should be two opposite corners of the rectangle.
	{
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;

		//A rectangle contains four lines.
		initSubshapes(4);
		addSubshape(new Line(startX, startY, endX, startY));
		addSubshape(new Line(endX, startY, endX, endY));
		addSubshape(new Line(endX, endY, startX, endY));
		addSubshape(new Line(startX, endY, startX, startY));
	}

	public double getStartX()
	{
		return startX;
	}

	public double getStartY()
	{
		return startY;
	}

	public double getEndX()	//since a drawing arm will return to the beginning of the rectangle, getEndX() and getEndY() should refer to the same as the starting position
	{
		return startX;
	}

	public double getEndY()
	{
		return startY;
	}

	public int getPoints()
	{
		return getSubshape(0).getPoints()+getSubshape(1).getPoints()+getSubshape(2).getPoints()+getSubshape(3).getPoints();
	}

	public String toString()
	{
		return "Rect:  "+startX+","+startY+","+endX+","+endY;
	}

	public Point[] toPointArray()	//add the points from all subshapes to a total point array
	{
		Point[] points = new Point[getPoints()];
		int i=0;
		for(int s=0; s<getSubshapes(); s++)
		{
			Point[] subpoints = getSubshape(s).toPointArray();
			for(Point p : subpoints)
			{
				points[i] = p;
				i++;
			}
		}
		return points;
	}

	public void draw(Graphics g, double x0, double y0)
	{
		int x1=(int)startX+(int)x0;
		int x2=(int)endX+(int)x0;
		int y1=(int)startY+(int)y0;
		int y2=(int)endY+(int)y0;

		if(x1<x2)
		{
			if(y1<y2)
				g.drawRect(x1, y1, x2-x1, y2-y1);
			else
				g.drawRect(x1, y2, x2-x1, y1-y2);
		}
		else
		{
			if(y1<y2)
				g.drawRect(x2, y1, x1-x2, y2-y1);
			else
				g.drawRect(x2, y2, x1-x2, y1-y2);
		}
	}

	public void drawAsPoints(Graphics g, double x0, double y0)
	{
		drawSubshapesAsPoints(g, x0, y0);
	}
}
