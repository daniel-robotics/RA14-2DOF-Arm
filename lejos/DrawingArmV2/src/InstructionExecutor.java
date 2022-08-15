import lejos.util.Delay;
import lejos.nxt.Sound;

public class InstructionExecutor implements Runnable
{
	private Instruction[] instructions;
	private SynchronizedArm arm;

	public InstructionExecutor(Instruction[] instructions, SynchronizedArm arm)
	{
		this.instructions = instructions;
		this.arm = arm;

		new Thread(this).start();
	}

	public void run()
	{
		int i = 0;
		while(true)
		{
			while(instructions[i].getType() == Instruction.EMPTY)	//wait for instruction to be read from file
				Delay.msDelay(10);

			execute(instructions[i]);		//execute instruction

			i++;							// repeat for every instruction in the buffer, then start back at the beginning of the buffer
			if(i==instructions.length)
				i=0;
		}
	}

	public void execute(Instruction instruction)
	{System.out.println(instruction.toString());//
		if(instruction.getType()==Instruction.MOVE)
			arm.moveToAngles(instruction.getAngle1(), instruction.getAngle2());

		else if(instruction.getType()==Instruction.RAISE)
			arm.moveJ3ToAngle(SynchronizedArm.J3_MAX);

		else if(instruction.getType()==Instruction.LOWER)
			arm.moveJ3ToAngle(SynchronizedArm.J3_MIN);

		else if(instruction.getType()==Instruction.EXIT)
		{
			arm.setStopModes(	NXTRegulatedMotorAddon.STOP_MODE_BRAKE,
								NXTRegulatedMotorAddon.STOP_MODE_BRAKE,
								NXTRegulatedMotorAddon.STOP_MODE_BRAKE);
			arm.moveJ3ToAngle(SynchronizedArm.J3_INIT);
			arm.moveToAngles(SynchronizedArm.J1_INIT, SynchronizedArm.J2_INIT);
			Sound.beepSequenceUp();
			System.exit(0);
		}

		instruction.setType(Instruction.EMPTY);
	}
}
