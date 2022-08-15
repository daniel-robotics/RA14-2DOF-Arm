import java.awt.Graphics;

public class Rectangle extends Shape
{
	private double startX, startY, endX, endY;

	public Rectangle(double startX, double startY, double endX, double endY)	//two opposite corners of the rectangle.
	{
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;

		//A rectangle contains four lines.
		initPoints((int)(2*(endX-startX)+2*(endY-startY))+4); //approximate how many points will be in the rectangle
		addLinePoints(startX, startY, endX, startY);
		addLinePoints(endX, startY, endX, endY);
		addLinePoints(endX, endY, startX, endY);
		addLinePoints(startX, endY, startX, startY);
		trimPoints();
	}

	private void addLinePoints(double startX, double startY, double endX, double endY)
	{
		double xdist = endX-startX;
		double ydist = endY-startY;
		double d = Math.sqrt( (xdist*xdist) + (ydist*ydist) );

		for(int t=1; t<=d; t++)
		{
			double x = (1-t/d)*startX + t*endX/d;
			double y = (1-t/d)*startY + t*endY/d;
			addPoint(x,y);
		}
	}

	public String getType()
	{
		return "Rectangle";
	}

	public String toString()
	{
		return "Rect:  "+startX+","+startY+","+endX+","+endY;
	}

	public void draw(Graphics g, double x0, double y0, boolean flipped)
	{
		int x1=(int)startX+(int)x0;
		int x2=(int)endX+(int)x0;
		int y1=(int)startY;
		int y2=(int)endY;
		if(flipped)
		{
			y1=(int)y0-y1;
			y2=(int)y0-y2;
		}
		else
		{
			y1+=(int)y0;
			y2+=(int)y0;
		}

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

	public void drawAsPoints(Graphics g, double x0, double y0, boolean flipped)
	{
		for(int i=0; i<getPoints(); i++)
			g.drawLine((int)(getX(i)+x0), (int)(getY(i)+y0), (int)(getX(i)+x0), (int)(getY(i)+y0));
	}
}
