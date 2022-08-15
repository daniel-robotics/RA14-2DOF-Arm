import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.io.*;
import java.text.DecimalFormat;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTInfo;

public class NXTInterface
{
	public static final boolean DELETE_OUTPUT	= true;

	private static final DecimalFormat form	= DrawingArmPC3.form;
	private static PrintWriter writer;
	private static NXTCommand nxt;

	public static void sendToNXT(ArrayList<Shape> shapes)
	{
		// The data file name (the instruction file to be sent to the NXT) is the same as the drawing filename, minus the .txt, plus .dat
		String filename = FileWorker.getLastFilename().substring(0, FileWorker.getLastFilename().length()-4)+".dat";

		System.out.println("Connecting to NXT...");
		String nxtName = JOptionPane.showInputDialog(null, "Enter NXT name: ");
		boolean success = connect(nxtName);
		if(!success){
			int response = JOptionPane.showConfirmDialog(null, "Could not connect to NXT: "+nxtName+"\nPrepare file anyway?");
			if(response==JOptionPane.YES_OPTION)
			{
				File file = FileWorker.writeDataFile(shapes, filename);
				double sizeKB = (float)file.length()/1024;
				System.out.println("Drawing "+filename+" will take up "+form.format(sizeKB)+"kB on the NXT.");
				JOptionPane.showMessageDialog(null, "Drawing "+filename+" would take up "+form.format(sizeKB)+"kB on the NXT.");
				if(DELETE_OUTPUT)
					file.delete();
			}
			return;
		}

		System.out.println("Preparing instructions file for NXT");
		File file = FileWorker.writeDataFile(shapes, filename);
		double sizeKB = (float)file.length()/1024;
		double freeKB = getFreeKB();
		System.out.println("Drawing "+filename+" will take up "+form.format(sizeKB)+"kB / "+form.format(freeKB)+"kB available on the NXT.");
		int response = JOptionPane.showConfirmDialog(null, "Drawing "+filename+" will take up\n"+form.format(sizeKB)+"kB / "+form.format(freeKB)+"kB\navailable on the NXT. Continue?");
		if(response!=JOptionPane.YES_OPTION)
			return;
		if(sizeKB>freeKB-1){
			JOptionPane.showMessageDialog(null, "File too large. Aborting.");
			return;
		}

		System.out.println("Sending file to NXT");
		success = sendFile(file);
		if(!success){
			JOptionPane.showMessageDialog(null, "Could not send file to NXT");
			return;
		}
		JOptionPane.showMessageDialog(null, "File successfully sent to NXT");
		if(DELETE_OUTPUT)
			file.delete();
	}

	private static boolean connect(String nxtName)
	{
		System.out.println("Searching for "+nxtName+"...");
		NXTConnector connector = new NXTConnector();
		boolean success = connector.connectTo(nxtName, null, NXTCommFactory.ALL_PROTOCOLS, NXTComm.LCP);
		if(success)
			nxt = new NXTCommand(connector.getNXTComm());
		return success;
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

	private static boolean sendFile(File file)
	{
		boolean success = true;
		try{
			nxt.delete(file.getName());
			nxt.uploadFile(file, file.getName());
			nxt.disconnect();
		}catch(Exception e){
			e.printStackTrace();
			success=false;
		}
		return success;
	}

}
