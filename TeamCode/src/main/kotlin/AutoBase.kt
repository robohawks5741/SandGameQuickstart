import androidx.xr.runtime.math.clamp
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.PoseVelocity2d
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.MecanumDrive
import org.firstinspires.ftc.teamcode.MecanumDrive.DriveLocalizer
import org.firstinspires.ftc.teamcode.PinpointLocalizer
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/*
* This file contains an example of an iterative (Non-Linear) "OpMode".
* An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
* The names of OpModes appear on the menu of the FTC Driver Station.
* When a selection is made from the menu, the corresponding OpMode
* class is instantiated on the Robot Controller and executed.
*
* This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
* It includes all the skeletal structure that all iterative OpModes contain.
*
* Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
* Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
*/
@Autonomous(name = "AutoBase")

class AutoBase : OpMode() {
    // Declare OpMode members.
    private val runtime = ElapsedTime()
    lateinit var drive: MecanumDrive
    lateinit var pinPoint: PinpointLocalizer
    var sequencer = 0

    /*
     * Code to run ONCE when the driver hits INIT
     */
    override fun init() {
        telemetry.addData("Status", "Initialized")

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        //initialize Mecanum Drive with the hardwareMap and an initial 2D Pose @ (0,0) with heading of 0.0 Degrees
        drive = MecanumDrive(hardwareMap, Pose2d(0.0, 0.0, 0.0))
        //Initialize the Pinpoint Localizer with the hardwareMap, encoder inches per tick, and an
        //initial 2D Pose @ (0,0) with a heading of 0.0 degrees
        pinPoint = PinpointLocalizer(hardwareMap, 0.00144943115234375, Pose2d(0.0, 0.0, 0.0))

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized")
        var rotation = pinPoint.pose.heading
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
     */
    override fun init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits START
     */
    override fun start() {
        runtime.reset()
    }

    /*
     * Code to run REPEATEDLY after the driver hits START but before they hit STOP
     */
    override fun loop() {
        // Setup a variable for each drive wheel to save power level for telemetry
        var xPower :Double = 0.0
        var yPower :Double = 0.0
        var turnPower :Double = 0.0
        val robotYaw = pinPoint.pose.heading
        var robotX = pinPoint.pose.position.x
        var robotY = pinPoint.pose.position.y
        
        var xFinal = 0.0
        var yFinal = 0.0
        var yawFinal = 0.0

        val gyroYaw = robotYaw.toDouble()

        //autoDriver -> router or sequencer then router
        //router uses global variables for target coords
        //sequencer calls vision when necessary

        autoDriver()

        // +X = forward
        // +Y = left
        //TODO: Switch controller inputs to automatics
        val x = -xFinal
        val y = -yFinal

        // angle of the stick
        val inputTheta = atan2(y, x)
        // evaluated theta
        val driveTheta = inputTheta - gyroYaw // + PI
        // magnitude of inputVector clamped to [0, 1]
        val inputPower = sqrt(x * x + y * y).coerceIn(0.0, 1.0)
        val driveRelativeX = cos(driveTheta) * inputPower
        val driveRelativeY = sin(driveTheta) * inputPower
        val pv = PoseVelocity2d(
            Vector2d(driveRelativeX, driveRelativeY),
            -yawFinal
        )
        drive.setDrivePowers(pv)

        // Choose to drive using either Tank Mode, or POV Mode
        // Comment out the method that's not used.  The default below is POV.

        // Show the elapsed game time.
        telemetry.addData("Status", "Run Time: $runtime")
    }


    //used by sequencer to see an object and calculate coordinates that sequencer will deliver to router
    private fun vision() : IntArray {
        return intArrayOf(1,1,1)
    }
    //holds the sequence of instructions for the router and moves through them when called.
    private fun sequencer() {
        var outputTransform = intArrayOf(0,0,0)

        if (sequencer == 0) {
            outputTransform = vision()
        }
    }
    //calculates the best route to target position and passes operation inputs
    private fun router() {
        
    }
    //takes route from router and calculates drive inputs and operation inputs
    private fun autoDriver() {

    }
    /*
     * Code to run ONCE after the driver hits STOP
     */
    override fun stop() {
    }
}
