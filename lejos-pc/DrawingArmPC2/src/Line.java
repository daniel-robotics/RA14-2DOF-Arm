import java.awt.Graphics;

public class Line extends Shape
{
	public Line(double startX, double startY, double endX, double endY)
	{
		double xdist = endX-startX;
		double ydist = endY-startY;
		double d = Math.sqrt( (xdist*xdist) + (ydist*ydist) );

		initPoints((int)d+1);									//approximate how many points will be in the line
		for(int t=1; t<=d; t++)
		{
			double x = (1-t/d)*startX + t*endX/d;
			double y = (1-t/d)*startY + t*endY/d;
			addPoint(x,y);
		}
		trimPoints();
	}


	public String getType()
	{
		return "Line";
	}

	public String toString()
	{
		return "Line:  "+getStartX()+","+getStartY()+","+getEndX()+","+getEndY();
	}

	public void draw(Graphics g, double x0, double y0, boolean flipped)
	{
		int x1=(int)getStartX()+(int)x0;
		int x2=(int)getEndX()+(int)x0;
		int y1=(int)getStartY();
		int y2=(int)getEndY();
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
		g.drawLine(x1,y1,x2,y2);
	}

	public void drawAsPoints(Graphics g, double x0, double y0, boolean flipped)
	{
		for(int i=0; i<getPoints(); i++)
			g.drawLine((int)(getX(i)+x0), (int)(getY(i)+y0), (int)(getX(i)+x0), (int)(getY(i)+y0));
	}
}
