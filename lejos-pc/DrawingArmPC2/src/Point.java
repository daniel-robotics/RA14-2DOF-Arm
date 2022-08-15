import java.awt.Graphics;

public class Point extends Shape
{
	public Point(double x, double y) //represents one (or multiple) individual points, preferrably connected.
	{
		addPoint(x,y);
	}

	public String getType()
	{
		return "Point";
	}

	public String toString()
	{
		return "Point: "+getStartX()+","+getStartY();
	}

	public void draw(Graphics g, double x0, double y0, boolean flipped)
	{
		if(flipped)
			for(int i=0; i<getPoints(); i++)
			{
				int x1=(int)getX(i)+(int)x0;
				int y1=(int)y0-(int)getY(i);
				g.drawLine(x1,y1,x1,y1);
			}
		else
			for(int i=0; i<getPoints(); i++)
			{
				int x1=(int)getX(i)+(int)x0;
				int y1=(int)y0+(int)getY(i);
				g.drawLine(x1,y1,x1,y1);
			}
	}

	public void drawAsPoints(Graphics g, double x0, double y0, boolean flipped)
	{
		draw(g, x0, y0, flipped);
	}

}
