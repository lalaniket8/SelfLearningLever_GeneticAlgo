package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.List;

import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.framework.SimulationBody;
import org.dyn4j.framework.SimulationFrame;
import org.dyn4j.framework.input.BooleanStateKeyboardInputHandler;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.World;
import org.dyn4j.world.listener.StepListenerAdapter;

public class GASimulator extends SimulationFrame {

	private static final long serialVersionUID = 1L;
	private final BooleanStateKeyboardInputHandler up;
	private final BooleanStateKeyboardInputHandler down;
	private final BooleanStateKeyboardInputHandler left;
	private final BooleanStateKeyboardInputHandler right;

	private SimulationBody ball;
	private SimulationBody bucketTop;
	private SimulationBody bucketBottom;
	private SimulationBody bucketLeft;
	private SimulationBody bucketRight;
	private SimulationBody stand;
	RevoluteJoint<SimulationBody> joint;
	private long step = 0;
	private boolean stepTimerFlag = false;

	private SimulationBody holder;

	private Object BOUNDS = new Object();
	private Object BALL = new Object();
	private Object HOLDER = new Object();

	private double torque = 0;
	private boolean simulationFlag = false; // start simulation when true
	// Flags for simulation Panel
	public boolean ballFallen = false;

	public GASimulator() {
		super("GA Simulator", 27.0, 500, 500);// 27
		this.setOffsetY(-243);
		// this.disableMousePicking();
		this.disablePan();
		this.disableZoom();
		this.up = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_UP);
		this.down = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_DOWN);
		this.left = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_LEFT);
		this.right = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_RIGHT);

		this.up.install();
		this.down.install();
		this.left.install();
		this.right.install();
	}

	@Override
	protected void initializeWorld() {
		this.world.setGravity(World.EARTH_GRAVITY);
		// Bottom-Side
		bucketBottom = new SimulationBody();
		bucketBottom.addFixture(Geometry.createRectangle(18.0, 0.5));
		bucketBottom.setMass(MassType.INFINITE);
		bucketBottom.setColor(Color.GRAY);
		bucketBottom.setUserData(BOUNDS);
		world.addBody(bucketBottom);

		// Top-Side
		bucketTop = new SimulationBody();
		bucketTop.addFixture(Geometry.createRectangle(18.0, 0.5));
		bucketTop.translate(new Vector2(0.0, 18.0));
		bucketTop.setMass(MassType.INFINITE);
		bucketTop.setColor(Color.GRAY);
		bucketBottom.setUserData(BOUNDS);
		world.addBody(bucketTop);

		// Left-Side
		bucketLeft = new SimulationBody();
		bucketLeft.addFixture(Geometry.createRectangle(0.5, 18.0));
		bucketLeft.translate(new Vector2(-9, 9));
		bucketLeft.setMass(MassType.INFINITE);
		bucketLeft.setColor(Color.GRAY);
		bucketBottom.setUserData(BOUNDS);
		world.addBody(bucketLeft);

		// Right-Side
		bucketRight = new SimulationBody();
		bucketRight.addFixture(Geometry.createRectangle(0.5, 18.0));
		bucketRight.translate(new Vector2(9, 9));
		bucketRight.setMass(MassType.INFINITE);
		bucketRight.setColor(Color.GRAY);
		bucketBottom.setUserData(BOUNDS);
		world.addBody(bucketRight);

		stand = new SimulationBody();
		stand.addFixture(Geometry.createIsoscelesTriangle(2, 2.6));
		stand.setColor(Color.BLUE);
		stand.translate(new Vector2(0, 1.1));
		stand.setMass(MassType.INFINITE);
		bucketBottom.setUserData(BOUNDS);
		world.addBody(stand);

		holder = new SimulationBody();
		holder.addFixture(Geometry.createRectangle(5, 0.25));
		holder.setColor(Color.BLUE);
		holder.translate(new Vector2(0, 3));
		holder.setMass(MassType.NORMAL);
		world.addBody(holder);
		bucketBottom.setUserData(HOLDER);
		joint = new RevoluteJoint<SimulationBody>(holder, stand, new Vector2(0.00, 3.0));
		world.addJoint(joint);

		ball = new SimulationBody();
		ball.addFixture(Geometry.createCircle(0.3), 3, 0.2, 0);
		ball.setColor(Color.RED);
		ball.translate(new Vector2(1, 3.55));
		ball.setMass(MassType.NORMAL);
		bucketBottom.setUserData(BALL);
		world.addBody(ball);

		world.addStepListener(new StepListenerAdapter<SimulationBody>() {
			@Override
			public void begin(TimeStep step, PhysicsWorld<SimulationBody, ?> world) {
				super.begin(step, world);

				if (!ballFallen) {
					List<ContactConstraint<SimulationBody>> contacts = world.getContacts(ball);
					for (ContactConstraint<SimulationBody> cc : contacts) {
						if (cc.getOtherBody(ball) == bucketBottom || cc.getOtherBody(ball) == bucketLeft
								|| cc.getOtherBody(ball) == bucketRight || cc.getOtherBody(ball) == bucketTop
								|| cc.getOtherBody(ball) == stand) {
							ballFallen = true;
							// System.out.println("Ball Fallen");
						}

					}
				}
			}
		});

	}

	protected void render(Graphics2D g, double elapsedTime) {
		super.render(g, elapsedTime);

		if (stepTimerFlag) {
			step++;
		}

		if (simulationFlag) {
			// System.out.println("applying torque:" + torque);
			holder.applyTorque(torque);
			simulationFlag = false;
		}

	}

	public void simulate(double torque) {
		this.torque = torque;
		this.simulationFlag = true;
	}

	public boolean isBallFallen() {
		return ballFallen;
	}

	public void setBallFallen(boolean b) {
		ballFallen = b;
	}

	public void startStepTimer() {
		step = 0;
		stepTimerFlag = true;
	}

	public long stopStepTimer() {
		stepTimerFlag = false;
		return step;
	}

	public void reset() {
		// System.out.println("RESETTING!");
		this.pause();
		world.removeBody(holder);
		world.removeJoint(joint);
		holder = new SimulationBody();
		holder.addFixture(Geometry.createRectangle(5, 0.25));
		holder.setColor(Color.BLUE);
		holder.translate(new Vector2(0, 3));
		holder.setMass(MassType.NORMAL);
		world.addBody(holder);
		bucketBottom.setUserData(HOLDER);
		joint = new RevoluteJoint<SimulationBody>(holder, stand, new Vector2(0.00, 3.0));
		world.addJoint(joint);

		world.removeBody(ball);
		ball = new SimulationBody();
		ball.addFixture(Geometry.createCircle(0.3), 3, 0.2, 0);
		ball.setColor(Color.RED);
		ball.translate(new Vector2(1, 3.55));
		ball.setMass(MassType.NORMAL);
		bucketBottom.setUserData(BALL);
		world.addBody(ball);

		setBallFallen(false);
		startStepTimer();
		this.resume();
	}
}
