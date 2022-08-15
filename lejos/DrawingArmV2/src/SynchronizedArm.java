import lejos.nxt.*;
import lejos.robotics.*;
import lejos.util.Delay;

public class SynchronizedArm
{
	public static final int MAX_SPEED = 500;							//degrees per second
	public static final float J1_MAX =  45, J2_MAX = 180, J3_MAX = 30;	//maximum angle limits
	public static final float J1_MIN = -50, J2_MIN =  85, J3_MIN = -60;	//minimum angle limits
	public static final float J1_INIT= -45, J2_INIT= 180, J3_INIT= 30;	//initial joint angles

	private boolean sync = false;
	private NXTRegulatedMotorAddon joint1, joint2, joint3;
	private float gear1, gear2, gear3;
	private float dep2on1, dep3on1, dep3on2;
	private float actual1 = J1_INIT, actual2 = J2_INIT, actual3 = J3_INIT;	//Holds the "actual" angles of the arm, relative to the coordinate system shown on the PC

	public SynchronizedArm(	NXTRegulatedMotorAddon joint1, float gearRatio1,
							NXTRegulatedMotorAddon joint2, float gearRatio2, float dep2on1,
							NXTRegulatedMotorAddon joint3, float gearRatio3, float dep3on1, float dep3on2)
	{
		this.joint1 = joint1;	gear1 = gearRatio1;
		this.joint2 = joint2;	gear2 = gearRatio2;		this.dep2on1 = dep2on1;
		this.joint3 = joint3;	gear3 = gearRatio3;		this.dep3on1 = dep3on1;		this.dep3on2 = dep3on2;
		resetTachoCounts();
	}

	public void resetTachoCounts()
	{
		joint1.resetTachoCount();
		joint2.resetTachoCount();
		joint3.resetTachoCount();
	}

	private static void beep()
	{
		Sound.beep();
		Delay.msDelay(250);
	}

	public void waitForCompletion()
	{
		joint1.waitComplete();
		joint2.waitComplete();
		joint3.waitComplete();
	}

	private int low1, high1, low2, high2, low3, high3;
	public void waitUntilTacho(int tachoCount1, int tachoCount2, int tachoCount3)
	{
		low1 = tachoCount1-1;
		low2 = tachoCount2-1;
		low3 = tachoCount3-1;
		high1 = tachoCount1+1;
		high2 = tachoCount2+1;
		high3 = tachoCount3+1;
		while(!inRange(joint1.getTachoCount(), low1, high1) || !inRange(joint2.getTachoCount(), low2, high2) || !inRange(joint3.getTachoCount(), low3, high3))
		{}
	}

	private boolean inRange(int value, int lower, int upper)
	{
		return value>lower && value<upper;
	}

	public void setStopModes(boolean stopmodej1, boolean stopmodej2, boolean stopmodej3)
	{
		joint1.setStopMode(stopmodej1);
		joint2.setStopMode(stopmodej2);
		joint3.setStopMode(stopmodej3);
	}

	public void setSynchronization(boolean sync)
	{
		this.sync = sync;
	}

	public void moveJ3ToAngle(float newAngle)
	{
		moveToAngles(actual1, actual2, newAngle);
	}

	public void moveToAngles(float newAngle1, float newAngle2)
	{
		moveToAngles(newAngle1, newAngle2, actual3);
	}

	private float angle1, angle2, angle3;
	private int tachoCount1, tachoCount2, tachoCount3;
	public void moveToAngles(float newAngle1, float newAngle2, float newAngle3)
	{
		actual1 = newAngle1;
		actual2 = newAngle2;
		actual3 = newAngle3;

		if(actual1>J1_MAX)		actual1 = J1_MAX;		// enforce safety limits
		else if(actual1<J1_MIN)	actual1 = J1_MIN;
		if(actual2>J2_MAX)		actual2 = J2_MAX;
		else if(actual2<J2_MIN)	actual2 = J2_MIN;
		if(actual3>J3_MAX)		actual3 = J3_MAX;
		else if(actual3<J3_MIN)	actual3 = J3_MIN;

		angle1 = actual1 - J1_INIT;						// Subtract the arm's initial position from the desired "actual" angles to get
		angle2 = actual2 - J2_INIT;						//   the corresponding angles relative to the starting position.
		angle3 = actual3 - J3_INIT;

		tachoCount1 = (int)(angle1/gear1);									//the position of joint 1 has no dependance on the rest of the joints
		tachoCount2 = (int)(angle2/gear2 + angle1/dep2on1);					//the position of joint 2 depends on joint 1
		tachoCount3 = (int)(angle3/gear3 + angle1/dep3on1 + angle2/dep3on2);//the position of joint 3 depends on joint 1 and 2

//		System.out.println("new1:"+newAngle1+" actual:"+angle1);
//		System.out.println("new2:"+newAngle2+" actual:"+angle2);
//		System.out.println("new3:"+newAngle3+" actual:"+angle3);
//		Delay.msDelay(5000);

		if(sync)	synchronizedMotion(tachoCount1, tachoCount2, tachoCount3);
		else		directMotion(tachoCount1, tachoCount2, tachoCount3);
	}

	private int prevTacho1, prevTacho2, prevTacho3, tc1a, tc2a, tc3a, max, speed1, speed2, speed3;
	public void synchronizedMotion(int tachoCount1, int tachoCount2, int tachoCount3)	//moves three arm joints to final positions tachoCount1,2,3
	{																					// while regulating speed so that each joint reaches its final position at approximately the same time
		tc1a = Math.abs(tachoCount1-joint1.getTachoCount());	//find the maximum of the three distances traveled
		tc2a = Math.abs(tachoCount2-joint2.getTachoCount());
		tc3a = Math.abs(tachoCount3-joint3.getTachoCount());
		max = Math.max(Math.max(tc1a,tc2a),tc3a);

		if(max == tc1a){
			if(tc1a==0) tc1a=1;
			speed1 = MAX_SPEED;					//if joint1 has to move the farthest, it should move the fastest
			speed2 = MAX_SPEED*tc2a/tc1a;		//then set joint2's speed so it travels its distance in the same amount of time as joint 1
			speed3 = MAX_SPEED*tc3a/tc1a;		//same for joint3
		}
		else if(max == tc2a){
			if(tc2a==0) tc2a=1;
			speed2 = MAX_SPEED;
			speed1 = MAX_SPEED*tc1a/tc2a;
			speed3 = MAX_SPEED*tc3a/tc2a;
		}
		else if(max	== tc3a){
			if(tc3a==0) tc3a=1;
			speed3 = MAX_SPEED;
			speed1 = MAX_SPEED*tc1a/tc3a;
			speed2 = MAX_SPEED*tc2a/tc3a;
		}

		waitUntilTacho(prevTacho1, prevTacho2, prevTacho3);
		joint1.setSpeed(speed1);				//finally, start each motor
		joint2.setSpeed(speed2);
		joint3.setSpeed(speed3);
		joint1.rotateTo(tachoCount1, true);
		joint2.rotateTo(tachoCount2, true);
		joint3.rotateTo(tachoCount3, true);

		prevTacho1 = tachoCount1;
		prevTacho2 = tachoCount2;
		prevTacho3 = tachoCount3;
//		waitForCompletion();
	}

	public void directMotion(int tachoCount1, int tachoCount2, int tachoCount3)	//moves three arm joints to final positions tachoCount1,2,3
	{
		waitUntilTacho(prevTacho1, prevTacho2, prevTacho3);
		joint1.setSpeed(MAX_SPEED);
		joint2.setSpeed(MAX_SPEED);
		joint3.setSpeed(MAX_SPEED);
		joint1.rotateTo(tachoCount1, true);
		joint2.rotateTo(tachoCount2, true);
		joint3.rotateTo(tachoCount3, true);

		prevTacho1 = tachoCount1;
		prevTacho2 = tachoCount2;
		prevTacho3 = tachoCount3;
//		waitForCompletion();
	}
}
