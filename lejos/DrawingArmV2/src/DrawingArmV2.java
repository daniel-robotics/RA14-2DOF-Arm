/**
 * @(#)DrawingArmV2.java
 *
 * DrawingArmV2 application
 *
 * @author
 * @version 1.00 2017/5/18
 */

import java.io.*;
import lejos.nxt.*;
import lejos.robotics.*;
import lejos.util.Delay;

public class DrawingArmV2
{
	private static NXTRegulatedMotorAddon joint1 = new NXTRegulatedMotorAddon(MotorPort.C);
		private static final float gearRatio1 = 0.42857f;

	private static NXTRegulatedMotorAddon joint2 = new NXTRegulatedMotorAddon(MotorPort.A);
		private static final float gearRatio2 = 0.05714f,	dep2on1 = 1f;

	private static NXTRegulatedMotorAddon joint3 = new NXTRegulatedMotorAddon(MotorPort.B);
		private static final float gearRatio3 = 0.60000f,	dep3on1 = 1f, dep3on2 = -0.6f;

    public static final String	INSTRUCTION_FILE = "draw.dat";
    public static final int 	MAX_LINE_LENGTH	 = 20;

	private static FileReader reader;


    public static void main(String[] args)
    {
    	SynchronizedArm arm = new SynchronizedArm(	joint1, gearRatio1,						//Initialize the motors
													joint2, gearRatio2, dep2on1,
													joint3, gearRatio3, dep3on1, dep3on2);

		arm.setStopModes(	NXTRegulatedMotorAddon.STOP_MODE_BRAKE,
							NXTRegulatedMotorAddon.STOP_MODE_BRAKE,
							NXTRegulatedMotorAddon.STOP_MODE_BRAKE);

		File file = new File(INSTRUCTION_FILE);
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
		arm.setStopModes(	NXTRegulatedMotorAddon.STOP_MODE_FLOAT,		//When the end of the file is reached, set the motors to BRAKE mode, lift pen, and return to initial positions.
							NXTRegulatedMotorAddon.STOP_MODE_FLOAT,
							NXTRegulatedMotorAddon.STOP_MODE_BRAKE);
		arm.moveJ3ToAngle(SynchronizedArm.J3_INIT);
		arm.waitForCompletion();
		arm.moveToAngles(SynchronizedArm.J1_INIT, SynchronizedArm.J2_INIT);
		arm.waitForCompletion();
		Sound.beepSequenceUp();
		System.exit(0);
    }

	private static void beep()
	{
		Sound.beep();
		Delay.msDelay(250);
	}

	private static void error()
	{
		Sound.buzz();
		System.exit(1);
	}

}
