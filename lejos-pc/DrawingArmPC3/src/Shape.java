import java.util.ArrayList;
import java.awt.Graphics;

public abstract class Shape
{
	private ArrayList<Shape> subshapes = new ArrayList<Shape>(1);

	public void initSubshapes(int numSubshapes)
	{
		subshapes.ensureCapacity(numSubshapes);
	}

	public void addSubshape(Shape s)
	{
		subshapes.add(s);
	}

	public Shape getSubshape(int n)
	{
		if(n>=0 && n<getSubshapes())
			return subshapes.get(n);
		else
			return null;
	}

	public int getSubshapes()
	{
		return subshapes.size();
	}

	public Shape getLastSubshape()
	{
		if(subshapes.size()>0)
			return subshapes.get(subshapes.size()-1);
		else
			return null;
	}

	public void removeSubshape(int n)
	{
		if(n>=0 && n<getSubshapes())
			subshapes.remove(n);
	}

	public void removeLastSubshape()
	{
		if(subshapes.size()>0)
			subshapes.remove(subshapes.size()-1);
	}

	public abstract int		getPoints();
	public abstract double	getStartX();
	public abstract double	getStartY();
	public abstract double	getEndX();
	public abstract double	getEndY();

	public abstract String	toString();
	public abstract Point[] toPointArray();

	public abstract void	draw(Graphics g, double x0, double y0);			//x0 and y0 are a translation factor
	public abstract void	drawAsPoints(Graphics g, double x0, double y0);

	public void drawSubshapes(Graphics g, double x0, double y0)
	{
		for(Shape s : subshapes)
			s.draw(g, x0, y0);
	}

	public void drawSubshapesAsPoints(Graphics g, double x0, double y0)
	{
		for(Shape s : subshapes)
			s.drawAsPoints(g, x0, y0);
	}
}
