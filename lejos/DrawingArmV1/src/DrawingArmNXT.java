/**
 * @(#)DrawingArmNXT.java
 *
 * DrawingArmNXT application
 *
 * @author
 * @version 1.00 2016/4/14
 */

import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Delay;
import lejos.nxt.Button;
import lejos.nxt.ButtonListener;

public class DrawingArmNXT implements ButtonListener
{

	public static final int PEN_UP_SIGNAL		= -10000;
	public static final int PEN_DOWN_SIGNAL		= -20000;
	public static final int FINISHED_SIGNAL		= -30000;
	public static final int RESET_SIGNAL		= -40000;
	public static final char REPLY_CHAR			= 'k';
	public static final int BUFFER_SIZE			= 100;

	private static DataInputStream dis;
	private static DataOutputStream dos;

    public static void main(String[] args)
    {
		new DrawingArmNXT();
    }

	public DrawingArmNXT()
	{
		try
    	{
    		Button.ESCAPE.addButtonListener(this);
    		Motor.A.setSpeed(150);
			Motor.A.resetTachoCount();
			Motor.B.resetTachoCount();
			Motor.C.resetTachoCount();

    		NXTConnection connection = USB.waitForConnection();
    		dos = connection.openDataOutputStream();
    		dis = connection.openDataInputStream();

    		Sound.twoBeeps();
    		Button.waitForAnyPress();
    		dos.writeChar(REPLY_CHAR);
    		dos.flush();
    		Sound.beep();

			boolean finished = false;
			int[] buf = new int[BUFFER_SIZE];
			while(!finished)
			{
				for(int i=0; i<buf.length; i++)	// Receive BUFFER_SIZE number of commands from the PC, store them in an array, stopping if the FINISHED command is received.
				{
					buf[i] = dis.readInt();
					if(Runtime.getRuntime().freeMemory()<5000)
						Sound.buzz();
					if(buf[i] == FINISHED_SIGNAL)
						break;
				}

				Sound.beepSequenceUp();

				for(int i=0; i<buf.length; i++) // Interpret each command in sequence, halting once the FINISHED command is reached.
				{
	    			if(buf[i]==PEN_UP_SIGNAL)
	    			{
	    				liftPen();
	    			}
	    			else if(buf[i]==PEN_DOWN_SIGNAL)
	    			{
	    				lowerPen();
	    			}
	    			else if(buf[i]==RESET_SIGNAL)
	    			{
	    				reset();
	    			}
	    			else if(buf[i]==FINISHED_SIGNAL)
	    			{
	    				finished=true;
	    				break;
	    			}
	    			else
	    			{
	    				moveToPoint(buf[i], buf[i+1]);		// If the command represents an angle, the next command represents the other angle in the angle pair.
	    				i++;
	    			}
				}
			}


			dos.writeChar(REPLY_CHAR);
    		Sound.beepSequenceUp();
    		System.exit(0);
    	}
    	catch(IOException e){}
	}

    public static void liftPen() throws IOException
    {
    	Motor.A.rotateTo(-70);
    }

    public static void lowerPen() throws IOException
    {
    	Motor.A.rotateTo(0);
    }

    public static void reset() throws IOException
    {
    	Motor.B.resetTachoCount();
    	Motor.C.resetTachoCount();
    }

    public static void moveToPoint(int t1, int t2) throws IOException
    {
    	Motor.B.rotateTo(t1, true);
    	Motor.C.rotateTo(t2, true);
    	while(Motor.B.isMoving() || Motor.C.isMoving()){}
    }

	public void buttonPressed(Button b)
    {
		if(b==Button.ESCAPE)
    	{
    		System.exit(0);
    	}

    }

    public void buttonReleased(Button b){}
}
