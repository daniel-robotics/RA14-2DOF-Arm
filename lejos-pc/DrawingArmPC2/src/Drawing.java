import java.util.ArrayList;
import java.awt.Graphics;

public class Drawing //Represents a drawing made of a continuous series of shapes such as lines, points, curves, etc.
{

	private ArrayList<Shape> shapes = new ArrayList<Shape>();

	public Drawing(double startX, double startY)	//Begins the drawing with a Point shape
	{
		addPoint(startX, startY);
	}

	public Drawing(Shape s)							//Begins the drawing with a shape object
	{
		shapes.add(s);
	}

	public void addShape(Shape s)
	{
		shapes.add(s);
	}

	public void addPoint(double x, double y)		//Adds a Point shape to the drawing, does not necessarily need to be connected to the other shapes
	{
		shapes.add(new Point(x, y));
	}

	public void addLine(double endX, double endY)	//Adds a Line shape to the drawing, beginning at the previous shape's endpoint
	{
		if(shapes.size()==0)	//but if there hasn't been a previous shape to start at, just put a point there instead.
		{
			addPoint(endX,endY);
			return;
		}
		double startX = getLastShape().getEndX();
		double startY = getLastShape().getEndY();
		shapes.add(new Line(startX, startY, endX, endY));
	}

	public void addRect(double endX, double endY)	//Adds a Rectangle shape to the drawing, beginning at the previous shape's endpoint
	{
		if(shapes.size()==0)	//but if there hasn't been a previous shape to start at, just put a point there instead.
		{
			addPoint(endX,endY);
			return;
		}
		double startX = getLastShape().getEndX();
		double startY = getLastShape().getEndY();
		shapes.add(new Rectangle(startX, startY, endX, endY));
	}

	public void addCircle(double endX, double endY)	//Adds a Circle shape to the drawing, beginning at the previous shape's endpoint
	{
		if(shapes.size()==0)	//but if there hasn't been a previous shape to start at, just put a point there instead.
		{
			addPoint(endX,endY);
			return;
		}
		double startX = getLastShape().getEndX();
		double startY = getLastShape().getEndY();
		shapes.add(new Circle(startX, startY, endX, endY));
	}

	public void removeShape(int n)
	{
		if(n>=shapes.size())
			return;
		shapes.remove(n);
	}

	public void removeLastShape()
	{
		if(shapes.size()>0)
			shapes.remove(shapes.size()-1);
	}

	public int getShapes()
	{
		return shapes.size();
	}

	public Shape getShape(int n)
	{
		return shapes.get(n);
	}

	public Shape getLastShape()
	{
		if(shapes.size()>0)
			return shapes.get(shapes.size()-1);
		else
			return null;
	}

	public int getPoints() //returns the total number of points in all shapes
	{
		int points=0;
		for(Shape shape : shapes)
			points+=shape.getPoints();
		return points;
	}

	public void draw(Graphics g, double x0, double y0, boolean flipped)
	{
		for(Shape shape : shapes)
			shape.draw(g, x0, y0, flipped);
	}

	public void drawAsPoints(Graphics g, double x0, double y0, boolean flipped)
	{
		for(Shape shape : shapes)
			shape.drawAsPoints(g, x0, y0, flipped);
	}
}
