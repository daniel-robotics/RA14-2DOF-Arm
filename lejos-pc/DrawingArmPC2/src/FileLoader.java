
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class FileLoader
{
	private static String filename = "";

	public static ArrayList<Drawing> load(String previousFilename)
	{
		filename = JOptionPane.showInputDialog("Enter a filename to load.",previousFilename);
		ArrayList<Drawing> drawings = new ArrayList<Drawing>();

		if(filename!=null)
		{
			if(!filename.endsWith(".txt"))
				filename+=".txt";

			Scanner file = null;
			try{
				file = new Scanner(new File(filename));
				while(file.hasNextLine())
				{
					String line = file.nextLine();
					if(line.equals(""))
					{
						if(file.hasNextLine())	//If there's a blank line, but not at the end of the file, add a new drawing.
							drawings.add(new Drawing(parseShape(file.nextLine())));
					}
					else						// Otherwise, keep adding shapes to the current drawing.
					{
						drawings.get(drawings.size()-1).addShape(parseShape(line));
					}
				}


			}catch(IOException e){ e.printStackTrace(); }
			finally{
				if(file!=null) file.close();
			}

			return drawings;
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

	public static String getFilename()
	{
		return filename;
	}
}
