import java.util.ArrayList;
import java.awt.Graphics;

public abstract class Shape
{
	protected ArrayList<Double> xcoords = new ArrayList<Double>();
	protected ArrayList<Double> ycoords = new ArrayList<Double>();
	protected ArrayList<Double> t1angles = new ArrayList<Double>();
	protected ArrayList<Double> t2angles = new ArrayList<Double>();

	public void initPoints(int numPoints)
	{
		xcoords.ensureCapacity(numPoints);
		ycoords.ensureCapacity(numPoints);
		t1angles.ensureCapacity(numPoints);
		t2angles.ensureCapacity(numPoints);
	}

	public void addPoint(double x, double y)
	{
		AngleCalculator.calculateAngles(x,y);
		xcoords.add(x);
		ycoords.add(y);
		t1angles.add( AngleCalculator.getT1() );
		t2angles.add( AngleCalculator.getT2() );
	}

	public void trimPoints()
	{
		xcoords.trimToSize();
		ycoords.trimToSize();
		t1angles.trimToSize();
		t2angles.trimToSize();
	}

	public double getX(int n)
	{
		return xcoords.get(n);
	}

	public double getY(int n)
	{
		return ycoords.get(n);
	}

	public double getT1(int n)
	{
		return t1angles.get(n);
	}

	public double getT2(int n)
	{
		return t2angles.get(n);
	}

	public double getStartX()
	{
		return xcoords.get(0);
	}

	public double getStartY()
	{
		return ycoords.get(0);
	}

	public double getStartT1()
	{
		return t1angles.get(0);
	}

	public double getStartT2()
	{
		return t2angles.get(0);
	}

	public double getEndX()
	{
		return xcoords.get(xcoords.size()-1);
	}

	public double getEndY()
	{
		return ycoords.get(ycoords.size()-1);
	}

	public double getEndT1()
	{
		return t1angles.get(t1angles.size()-1);
	}

	public double getEndT2()
	{
		return t2angles.get(t2angles.size()-1);
	}

	public int getPoints()
	{
		return xcoords.size();
	}

	public void removePoint(int n)
	{
		if(n>=xcoords.size())
			return;
		xcoords.remove(n);
		ycoords.remove(n);
		t1angles.remove(n);
		t2angles.remove(n);
	}

	public abstract String getType();

	public abstract String toString();

	public abstract void draw(Graphics g, double x0, double y0, boolean flipped);
	public abstract void drawAsPoints(Graphics g, double x0, double y0, boolean flipped);
}
