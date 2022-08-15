import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.io.*;
import java.text.DecimalFormat;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTInfo;

public class NXTInterface
{
	public static final boolean DELETE_OUTPUT	= true;
	public static final int		PROTOCOL		= NXTCommFactory.BLUETOOTH;
	public static final String	LOCAL_EXTENSION	= ".dat";
	private static final DecimalFormat form		= DrawingArmPC2.form;

	private static PrintWriter writer;
	private static NXTCommand nxt;

	public static void sendToNXT(ArrayList<Drawing> drawings, String filename)
	{
		String localName = filename.substring(0, filename.length()-4)+LOCAL_EXTENSION;	// local filename (the instruction file to be sent to the NXT) is the same as the drawing filename, minus the .txt, plus .dat

		System.out.println("Connecting to NXT...");
		String nxtName = JOptionPane.showInputDialog(null, "Enter NXT name: ");
		boolean success = connect(nxtName);
		if(!success){
			int response = JOptionPane.showConfirmDialog(null, "Could not connect to NXT: "+nxtName+"\nPrepare file anyway?");
			if(response==JOptionPane.YES_OPTION)
			{
				File localFile = writeOut(drawings, localName);
				double sizeKB = (float)localFile.length()/1024;
				System.out.println("Drawing "+localName+" will take up "+form.format(sizeKB)+"kB on the NXT.");
				JOptionPane.showMessageDialog(null, "Drawing "+localName+" will take up "+form.format(sizeKB)+"kB on the NXT.");
			}
			return;
		}

		System.out.println("Preparing instructions file for NXT");
		File localFile = writeOut(drawings, localName);
		double sizeKB = (float)localFile.length()/1024;
		double freeKB = getFreeKB();
		System.out.println("Drawing "+localName+" will take up "+form.format(sizeKB)+"kB / "+form.format(freeKB)+"kB available on the NXT.");
		int response = JOptionPane.showConfirmDialog(null, "Drawing "+localName+" will take up\n"+form.format(sizeKB)+"kB / "+form.format(freeKB)+"kB\navailable on the NXT. Continue?");
		if(response!=JOptionPane.YES_OPTION)
			return;
		if(sizeKB>freeKB-1){
			JOptionPane.showMessageDialog(null, "File too large. Aborting.");
			return;
		}

		System.out.println("Sending file to NXT");
		success = sendFile(localFile);
		if(!success){
			JOptionPane.showMessageDialog(null, "Could not send file to NXT");
			return;
		}
		JOptionPane.showMessageDialog(null, "File successfully sent to NXT");
		if(DELETE_OUTPUT)
			localFile.delete();
	}



	private static boolean connect(String nxtName)
	{
		boolean success = true;
		try{
			NXTComm nxtComm = NXTCommFactory.createNXTComm(PROTOCOL);
			NXTInfo nxtInfo = nxtComm.search(nxtName)[0];
			nxtComm.open(nxtInfo);
			nxt = new NXTCommand(nxtComm);
		}catch(Exception e){
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	private static File writeOut(ArrayList<Drawing> drawings, String localName)
	{
		//Walk through the entire process of drawing the picture point by point, writing each angle pair to a local file

		File localFile = new File(localName);
		try{
			writer = new PrintWriter(localFile);
			for(Drawing d : drawings)				//For each drawing...
			{
				moveToPoint(d.getShape(0), 0);		//set the pen down at the first point in the drawing
				lowerPen();
				for(int s=1; s<d.getShapes(); s++)	//for each subsequent shape in the drawing, move through every point in the shape
				{
					Shape shape = d.getShape(s);
					for(int p=0; p<shape.getPoints(); p++)
						moveToPoint(shape, p);
					writer.flush();
				}
				liftPen();
			}
			writer.flush();
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}

		return localFile;
	}

	private static void liftPen()
	{
		writer.println("");
	}

	private static void lowerPen()
	{

	}

	private static void moveToPoint(Shape s, int p)
	{
		moveToPoint(s.getT1(p), s.getT2(p));
	}

	private static void moveToPoint(double t1, double t2)
	{
		writer.println(form.format(t1)+" "+form.format(t2));
	}

	private static double getFreeKB()
	{
		double freeFlash = -1;
		try{
			freeFlash = (double)(nxt.getDeviceInfo().freeFlash)/1024;
		}catch(IOException e){
			e.printStackTrace();
		}
		return freeFlash;
	}

	private static boolean sendFile(File localFile)
	{
		boolean success = true;
		try{
//			nxt.delete(LOCAL_FILENAME);
			nxt.uploadFile(localFile, localFile.getName());
			nxt.disconnect();
		}catch(Exception e){
			e.printStackTrace();
			success=false;
		}
		return success;
	}


}
