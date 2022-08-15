/**
 * @(#)NXTRegulatedMotorAddon.java
 *
 * DrawingArmV2 application
 *
 * An extension of the NXTRegulatedMotor class, which allows you to choose to brake or float the motor upon
 * reaching its target angle.
 */

import lejos.nxt.*;
import lejos.robotics.*;

public class NXTRegulatedMotorAddon extends NXTRegulatedMotor
{
	public static final boolean STOP_MODE_FLOAT = false;
	public static final boolean STOP_MODE_BRAKE = true;

	private boolean stopMode = STOP_MODE_BRAKE;

	public NXTRegulatedMotorAddon(TachoMotorPort port)
	{
		super(port);
	}


	public void setStopMode(boolean stopMode)
	{
		this.stopMode = stopMode;
	}

	// Same as normal, but takes into account stopMode when movement is complete

    public void rotate(int angle)
    {
        rotate(angle, false);
    }

    public void rotate(int angle, boolean immediateReturn)
    {
		rotateTo(super.getTachoCount() + angle, immediateReturn);
    }

    public void rotateTo(int limitAngle)
    {
        rotateTo(limitAngle, false);
    }

    public void rotateTo(int limitAngle, boolean immediateReturn)
    {
        super.reg.newMove(speed, acceleration, limitAngle, this.stopMode, !immediateReturn);
    }

    // Same as normal, but lets you specify a stopMode for this particular motion

    public void rotateTo(int limitAngle, boolean stopMode, boolean immediateReturn)
    {
        super.reg.newMove(speed, acceleration, limitAngle, stopMode, !immediateReturn);
    }
}
