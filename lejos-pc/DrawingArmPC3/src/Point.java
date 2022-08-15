import java.awt.Graphics;

public class Point extends Shape	// A "basic" shape (has no subshapes) which represents an individual point.
{
	private double x, y;

	public Point(double x, double y)
	{
		this.x=x;
		this.y=y;
	}

	public int getPoints()
	{
		return 1;
	}

	public double getStartX()
	{
		return x;
	}

	public double getStartY()
	{
		return y;
	}

	public double getEndX()
	{
		return x;
	}

	public double getEndY()
	{
		return y;
	}

	public String toString()
	{
		return "Point: "+getStartX()+","+getStartY();
	}

	public Point[] toPointArray()
	{
		return new Point[]{this};
	}

	private int x1, y1;
	public void draw(Graphics g, double x0, double y0)
	{
		x1=(int)x0+(int)x;
		y1=(int)y0+(int)y;
		g.drawLine(x1,y1,x1,y1);
	}

	public void drawAsPoints(Graphics g, double x0, double y0)
	{
		draw(g, x0, y0);
	}

}
