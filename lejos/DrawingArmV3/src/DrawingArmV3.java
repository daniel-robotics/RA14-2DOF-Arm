/**
 * @(#)DrawingArmV3.java
 *
 * DrawingArmV2 application
 *
 * @author
 * @version 1.00 2017/5/30
 */

import java.io.File;
import java.io.IOException;
import lejos.nxt.Sound;
import lejos.nxt.LCD;
import lejos.util.Delay;
import lejos.util.TextMenu;


public class DrawingArmV3
{
	public static final boolean SYNCHRONIZE		 = true;
	public static final boolean BRAKE_MODE		 = false;
//    public static final String	INSTRUCTION_FILE = "draw.dat";
    public static final int 	MAX_LINE_LENGTH	 = 20;
    public static final int		MAX_INST_FILES	 = 7;

	public static SynchronizedArm arm = new SynchronizedArm(SYNCHRONIZE, BRAKE_MODE);
	public static FileReader reader;

    public static void main(String[] args)
    {
		String filename = initGUI();
		beep();
		execute(filename);
		end();
    }

    public static String initGUI()
    {
    	int i = 0, instructionFiles = 0;
    	String filename;
    	File[] files = File.listFiles();
    	String[] filenames = new String[MAX_INST_FILES];
    	for(i = 0; i<files.length && instructionFiles<MAX_INST_FILES; i++) 	// Check every file on the system, or until we have discovered MAX_INST_FILES instruction files.
    	{
    		if(files[i]!=null)
    		{
				filename = files[i].getName();
	     		if(filename.substring(filename.length()-4).equals(".dat"))				// If a filename ends with .dat, it is an instruction file, so add it to the list of filenames
	     		{
	     			filenames[instructionFiles] = filename;
	    			instructionFiles++;
	     		}
    		}
    	}

		String[] menuItems = new String[instructionFiles];						// Make a new string array, with only the non-null strings from the "filenames" array, minus the file extension
		for(i=0; i<instructionFiles; i++)
			menuItems[i] = filenames[i].substring(0, filenames[i].length()-4);

		TextMenu menu = new TextMenu(menuItems, 1, "Make a selection:");		// Display the menu, wait for the user to select one of the instruction files
		if((i=menu.select())<0)
			System.exit(0);
		menu.quit();
		LCD.clear();
		System.out.println(" Reading file:");
		System.out.println("   "+filenames[i]);
		return filenames[i];
    }

    private static void execute(String filename)	// Read the instruction file line-by-line, moving to each angle pair
    {
		File file = new File(filename);
		if(!file.exists())
			error();
		try{
			reader = new FileReader(file, MAX_LINE_LENGTH);
			int split;
			float angle1, angle2;
			String line="", prevLine="";

			while((line = reader.readLine()) != null)
			{
				if(line.equals(""))										//If line is empty, stop and raise the pen
				{
					arm.moveJ3ToAngle(SynchronizedArm.J3_MAX);
				}
				else													//Otherwise, move the arm to the new position
				{
					split = line.indexOf(' ');
					angle1 = Float.parseFloat(line.substring(0, split));
					angle2 = Float.parseFloat(line.substring(split+1, line.length()-1));
					arm.moveToAngles(angle1, angle2);
					if(prevLine.equals("")){								//If the previous line was blank, be sure to lower the pen again
						arm.moveJ3ToAngle(SynchronizedArm.J3_MIN);
					}
				}

				prevLine = line;
			}
		}catch(IOException e){
			error();
		}
		reader.close();
    }

    private static void end()		// Conclude the program by resetting the arm positions
    {
		arm.setStopModes(	NXTRegulatedMotorAddon.STOP_MODE_BRAKE,		//set the motors to BRAKE mode, lift pen, and return to initial positions.
							NXTRegulatedMotorAddon.STOP_MODE_BRAKE,
							NXTRegulatedMotorAddon.STOP_MODE_BRAKE);
		arm.moveJ3ToAngle(SynchronizedArm.J3_INIT);
		arm.moveToAngles(SynchronizedArm.J1_INIT, SynchronizedArm.J2_INIT);
		arm.waitForCompletion();
		Sound.beepSequenceUp();
		System.exit(0);
    }

	private static void beep()
	{
		Sound.beep();
		Delay.msDelay(2000);
	}

	private static void error()
	{
		Sound.buzz();
		System.exit(1);
	}

}
