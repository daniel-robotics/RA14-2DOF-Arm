import java.io.*;
import lejos.nxt.*;
import lejos.robotics.*;
import lejos.util.Delay;

public class InstructionReader implements Runnable
{
	private String filename;
	private BufferedReader buf;
	private Instruction[] instructions;

	public InstructionReader(Instruction[] instructions, String filename)
	{
		this.instructions = instructions;
		this.filename = filename;			//Initialize the file reader
		File file = new File(filename);
		if(!file.exists())
			error();
		try{
			buf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		}catch(IOException e){
			error();
		}

		new Thread(this).start();
	}

	public void run()
	{
		int i = 0;
		String line = "";
		try
		{
			while((line = buf.readLine()) != null)
			{
				waitForExecution(instructions[i]);	//wait for instruction to be executed before replacing it

				if(line.isEmpty())																			//If line is empty, stop and raise the pen
				{
					instructions[i].setType(Instruction.RAISE);
				}
				else																						//Otherwise, move the arm to the new position
				{
					int split = line.indexOf(' ');
					instructions[i].setAngle1(Float.parseFloat(line.substring(0, split)));
					instructions[i].setAngle2(Float.parseFloat(line.substring(split+1, line.length()-1)));
					instructions[i].setType(Instruction.MOVE);

					if(instructions[getPreviousIndex(i)].getType()==Instruction.RAISE)						//If the previous line was blank, be sure to lower the pen again
					{
						i = getNextIndex(i);
						instructions[i].setType(Instruction.LOWER);
					}
				}

				i = getNextIndex(i);			// repeat for every instruction in the buffer, then start back at the beginning of the buffer
			}
		}
		catch(IOException e){
			error();
		}

		waitForExecution(instructions[i]);				//when file ends, make the next instruction the "EXIT" instruction
		instructions[i].setType(Instruction.EXIT);
	}

	private void error()
	{
		Sound.buzz();
		System.exit(1);
	}

	private int getNextIndex(int i)
	{
		if(i==instructions.length-1)
			return 0;
		else
			return i+1;
	}

	private int getPreviousIndex(int i)
	{
		if(i==0)
			return instructions.length-1;
		else
			return i-1;
	}

	private void waitForExecution(Instruction instruction)
	{
		while(instruction.getType() != Instruction.EMPTY)
			Delay.msDelay(10);
	}
}
