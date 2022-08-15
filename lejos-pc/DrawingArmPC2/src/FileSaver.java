
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class FileSaver
{
	public static final int CANCELLED = 0;
	public static final int SAVED = 0;

	private static String filename = "";

	public static int save(ArrayList<Drawing> drawings, String previousFilename)
	{
		filename = JOptionPane.showInputDialog("Enter a filename for this drawing.",previousFilename);

		if(filename!=null)
		{
			if(!filename.endsWith(".txt"))
				filename+=".txt";

			PrintWriter writer = null;
			try
			{
				writer = new PrintWriter(filename);

				for(Drawing d : drawings)
				{
					writer.println("");						//Format: Put a space between each drawing
					for(int n=0; n<d.getShapes(); n++)		//Within each drawing, list each of the shapes in order. Points can later be reconstructed from the shapes.
						writer.println(d.getShape(n).toString());
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

	public static String getFilename()
	{
		return filename;
	}
}
