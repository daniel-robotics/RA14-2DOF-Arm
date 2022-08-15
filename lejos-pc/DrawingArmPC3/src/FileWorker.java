import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

public class FileWorker
{
	public static final double MAX_DISTANCE = 2.2;
	public static final int CANCELLED = 0;
	public static final int SAVED = 0;
	private static final DecimalFormat form	= DrawingArmPC3.form;

	private static String filename = "";
	private static PrintWriter writer = null;
	private static Scanner scan = null;

	public static String getLastFilename()
	{
		return filename;
	}

	public static int save(ArrayList<Shape> shapes)
	{
		filename = JOptionPane.showInputDialog("Enter a filename for this drawing.",filename);

		if(filename!=null)
		{
			if(!filename.endsWith(".txt"))
				filename+=".txt";

			writer = null;
			try
			{
				writer = new PrintWriter(filename);
				for(Shape s : shapes)				//List each shape in file.
				{
					writer.println(s.toString());
				}
			}
			catch(IOException e){ e.printStackTrace(); }
			finally{
				writer.flush();
				writer.close();
			}

			return SAVED;
		}
		else
			return CANCELLED;
	}

	public static ArrayList<Shape> load()
	{
		filename = JOptionPane.showInputDialog("Enter a filename to load.",filename);
		ArrayList<Shape> shapes = new ArrayList<Shape>(100);

		if(filename!=null)
		{
			if(!filename.endsWith(".txt"))
				filename+=".txt";

			System.out.println("Loading file: "+filename);
			try{
				scan = new Scanner(new File(filename));
				while(scan.hasNextLine())
				{
					shapes.add(parseShape(scan.nextLine()));
				}
			}catch(IOException e){ e.printStackTrace(); }
			finally{
				if(scan!=null) scan.close();
				shapes.trimToSize();
			}

			return shapes;
		}
		else
			return null;

	}

	private static Shape parseShape(String line)
	{
		Shape shape=null;
		if(line.startsWith("Line"))
		{
			line = line.substring(7,line.length());
			Scanner scan = new Scanner(line);
			scan.useDelimiter(",");
			double x1=scan.nextDouble();
			double y1=scan.nextDouble();
			double x2=scan.nextDouble();
			double y2=scan.nextDouble();
			shape = new Line(x1,y1,x2,y2);
		}
		else if(line.startsWith("Point"))
		{
			line = line.substring(7,line.length());
			Scanner scan = new Scanner(line);
			scan.useDelimiter(",");
			double x1=scan.nextDouble();
			double y1=scan.nextDouble();
			shape = new Point(x1,y1);
		}
		else if(line.startsWith("Rect"))
		{
			line = line.substring(7,line.length());
			Scanner scan = new Scanner(line);
			scan.useDelimiter(",");
			double x1=scan.nextDouble();
			double y1=scan.nextDouble();
			double x2=scan.nextDouble();
			double y2=scan.nextDouble();
			shape = new Rectangle(x1,y1,x2,y2);
		}
		else if(line.startsWith("Circle"))
		{
			line = line.substring(7,line.length());
			Scanner scan = new Scanner(line);
			scan.useDelimiter(",");
			double x1=scan.nextDouble();
			double y1=scan.nextDouble();
			double x2=scan.nextDouble();
			double y2=scan.nextDouble();
			shape = new Circle(x1,y1,x2,y2);
		}
		System.out.println(shape.toString());
		return shape;
	}

	public static File writeDataFile(ArrayList<Shape> shapes, String filename)		//Prepares a (temporary) data file which will be sent to the NXT.
	{
		//Walk through the entire process of drawing the picture point by point, writing each angle pair to the data file

		File file = new File(filename);		//create the new temporary file (with a .dat extension instead of the .txt)
		try
		{
			writer = new PrintWriter(file);
			Point lastPoint = null;
			for(Shape s : shapes)				//For each shape...
			{
				Point[] points = s.toPointArray();	//retrieve a sequential list of points that compose that shape

													//determine if this shape is "connected" to the previous shape (the starting point of this shape is too far away from the end of the last shape)
				if(lastPoint!=null && distanceBetween(lastPoint, points[0]) > MAX_DISTANCE)
					liftPen();							//and if so, lift the pen.

				for(Point p: points)				//move through every point in the shape (the robot will automatically lower the pen after moving to the first point)
				{
					moveToPoint(p);
					lastPoint = p;
				}
				writer.flush();						//write out the data for that shape
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			if(writer!=null)
			{
				writer.flush();
				writer.close();
			}
		}

		return file;
	}

	private static void moveToPoint(Point p)
	{
		AngleCalculator.calculateAngles(p.getStartX(), p.getStartY());
		moveToPoint(AngleCalculator.getT1(), AngleCalculator.getT2());
	}

	private static void moveToPoint(double t1, double t2)
	{
		writer.println(form.format(t1)+" "+form.format(t2));
	}

	private static double distanceBetween(Point p1, Point p2)
	{
		double dx = p2.getStartX()-p1.getStartX();
		double dy = p2.getStartY()-p1.getStartY();
		return Math.sqrt( (dx*dx) + (dy*dy) );
	}

	private static void liftPen()
	{
		writer.println("");
	}
}
