# GonzoDriveTrain

Below I'll outline the steps necessary to make the drive train come to life (I think, I can't exactly test it here!)

## Step 1: Give our drive train a `teleopDrive` method

> [Link to commit to see the changes required to achieve Step 1](https://github.com/LHSPantherbots/GonzoDriveTrain/commit/15e8c1f29f320b89bd1b2add85c9ebc07bcddb2f).

If you reference the [2021-Robot-Code](https://github.com/LHSPantherbots/2021-Robot-Code) repo, you'll see that in [RobotContainer.java](https://github.com/LHSPantherbots/2021-Robot-Code/blob/c036fe45b935c2e474f176289e6d119afdd75294/src/main/java/frc/robot/RobotContainer.java#L60-L65) the following lines of code are executed to set up the DriveTrain to by default receive drive commands from the gamepad:
```java
driveTrain.setDefaultCommand(
  new RunCommand(() -> driveTrain
     // A split-stick arcade command, with forward/backward controlled by the left
    // hand, and turning controlled by the right.
  .teleopDrive(-Gamepad0.getRawAxis(1),
               Gamepad0.getRawAxis(4)), driveTrain));
```
This is all well and good, but when we were working in the classroom we agreed that perhaps trying to make this part of the code a little easier-to-understand is always a good thing. So we started with the following code (and if you look in my RobotContainer.java at the initial commit, this is what it looks like:
```java
// Make drivetrain respond to DriverGamepad inputs by default
Runnable driveFromGamepad = () -> driveTrain.teleopDrive();
RunCommand driveCommand = new RunCommand(driveFromGamepad, driveTrain);
driveTrain.setDefaultCommand(driveCommand);
```
What we're first doing here is setting up the [runnable](https://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html) `driveFromGamepad` so we define in a distinct manner what "driving from the gamepad" means. Then, we set up our [RunCommand](https://first.wpi.edu/FRC/roborio/release/docs/java/edu/wpi/first/wpilibj2/command/RunCommand.html) `driveCommand` and are telling it, "this command will process the runnable function `driveFromGamepad` to call the `teleopDrive` method on the drive train, and in order to do this, we require the `driveTrain` subsystem. Now that we have our `driveCommand` defined, telling the driveTrain class it's default command becomes a lot easier as you can see.

However, for the observer with the keen eye (and all of y'all smarties on the programming subteam), you see the three lines of code I listed above and ask yourself, "but where do we actually provide the DriverGamepad [Joystick](https://first.wpi.edu/FRC/roborio/release/docs/java/edu/wpi/first/wpilibj/Joystick.html) instance that we created?" You're totally right! We need to do that! In the [2021-Robot-Code](https://github.com/LHSPantherbots/2021-Robot-Code) right now we are explicitely telling which raw axises to use and provided it to DriveTrain.teleopDrive with the following method signature: `teleopDrive(double move, double turn)`. Simple enough, you tell it how much to move, you tell it how much to turn . However, I'm picky and believe in clean code. 

While it's nice to have a teleopDrive method that takes in doubles (and we will have one!) we don't want it doing all that work in the [`RobotContainer.java`](src/main/java/frc/robot/RobotContainer.java) class, because it adds clutter and that class is responsible for, at the high level, linking all our subsystems together. What we're going to do is create a `teleopDrive` method for our DriveTrain class that accepts a `Joystick` instance, and then inside that method we will figure out which raw axises to use and call the underlying `teleopDrive` method that accepts floats. One of the cool things about java is that you can have methods (a.k.a. functions) that all have the same name, but have different method signatures. *("Method Signature" is a fancy word for "what arguments/parameters does this function take and what does it return")*. One other thing you'll see me do in the Step 1 commit is move the logic to set up the runnable, the runcommand, and the linking of the default command into it's own private method in the RobotContainer class to clean up the `RobotContainer()` constructor 😀.

### Summary of Step 1:
As I linked to the top of this section, [this commit](https://github.com/LHSPantherbots/GonzoDriveTrain/commit/15e8c1f29f320b89bd1b2add85c9ebc07bcddb2f) shows the changes I made to achieve Step 1. 
* I created a private method in RobotContainer.java called `setupDrivetrain` in which we create `driveFromGamepad`, `driveCommand`, and then specify the DriveTrain's default command as the `driveCommand`.
* Inside DriveTrain.java, I created two teleopDrive methods, one that took a Joystick instance, and one that took two doubles, move and turn. As you'll notice by comment on line 53, we don't have a differential drivetrain instance yet! Let's do that

### Bonus question:
Right now, inside `teleopDrive(Joystick joystick)`, we just automatically use axises 3 and 4 to accomplish Daniel's challenge of driving with just one of the fobs on the joystick. If we wanted to allow to specify different axises to use when linking the joystick, how would we you do it? Talk amongst yourselves and let me know what ideas you come up with! I have some ideas of my own, but I'll only share those once y'all have given me some ideas.


## Step 2: Create our DifferentialDrive.

> **BEFORE YOU CAN BEGIN STEP 2, YOU NEED TO DO THE FOLLOWING**:
> https://www.revrobotics.com/content/sw/max/sdk/REVRobotics.json
> 1. Press `F1` key to open the [command palette for VS Code](https://code.visualstudio.com/docs/getstarted/userinterface#_command-palette)
> 2. First, build your robot code by looking for the command ">WPILib: BUild Robot Code" and executing it.
> 3. Look for the command ">WPILib: Manage Vendor libraries" by typing it out. Once you have it selected as the option, press Enter to select it
> 4. Select the option "Install new libraries (online)".
> 5. It will ask you to enter a vendor file URL. You need to enter: https://www.revrobotics.com/content/sw/max/sdk/REVRobotics.json as the vendor URL.

> [Link to commit to see the changes required to achieve Step 2](https://github.com/LHSPantherbots/GonzoDriveTrain/commit/a4bff97e33723b2ffc9e27448a00c47367ea136c?branch=a4bff97e33723b2ffc9e27448a00c47367ea136c&diff=unified).

So now we have our drive train set to respond to the inputs from the DriverGamepad by default. Our next challenge is to actually set up the DifferentialDrive instance and link it up to the motor controllers like Daniel showed us on Tuesday :-). The steps for this aren't too difficult, and you shouldn't feel bad for referencing the [DriveTrain.java](https://github.com/LHSPantherbots/2021-Robot-Code/blob/master/src/main/java/frc/robot/subsystems/DriveTrain.java) file in the [2021-Robot-Code](https://github.com/LHSPantherbots/2021-Robot-Code) repo because this part of the process is relatively simple in that there's only so many ways to mess it up. But the steps are as follows:
1. Define the motor controllers, which on this year's Robot are Spark Max controllers, so you'll be creating new instances of the `CANSparkMax` class for each of the controllers. This can be accomplished by creating the following class properties:
    ```java
    // Set up motor controllers
    CANSparkMax leftLeader = new CANSparkMax(7, MotorType.kBrushless);
    CANSparkMax leftFollower = new CANSparkMax(8, MotorType.kBrushless);
    CANSparkMax rightLeader = new CANSparkMax(5, MotorType.kBrushless);
    CANSparkMax rightFollower = new CANSparkMax(6, MotorType.kBrushless);
    ```
    
    **Question**: What are the numbers 7, 8, 5, and 6? According to the documentation, they represent the device ID, but how do we know what those numbers are? **Y'all should talk with Daniel or Mr. Robertson (or, anyone for that matter) to learn about how the way we wire the robot determines the IDs for the Spark controllers**.

2. Set up the [encoders](https://en.wikipedia.org/wiki/Rotary_encoder) (if you're unsure what encoders are, talk to Mr. Robertson!). As far as I can best explain, our encoders tell us how many times the motors have spinned the drive shaft, and you can use this to do basic geometry to track how far the robot has moved. This part is pretty basic, but the way we do it differs a little bit from the 2021-Robot-Code.

```java
// Sets up encoders
CANEncoder leftEncoder = leftLeader.getEncoder();
CANEncoder rightEncoder = rightLeader.getEncoder();
```

3. Set up the [DifferentialDrive](https://first.wpi.edu/FRC/roborio/release/docs/java/edu/wpi/first/wpilibj/drive/DifferentialDrive.html). For this, you specify the left leader as the left motor controller, and the right leader as the right motor controller.

```java
// Sets up differental drive
DifferentialDrive drive = new DifferentialDrive(leftLeader, rightLeader);
```

4. Set everything up in the `DriveTrain` Constructor! For this part, we make sure:
   1.  to reset the motor controllers to their factory defaults (we don't want the motor controller retaining settings from previous cycles, we like things to start from scratch).
   2.  to set the the open loop ramp rate for the leader controllers using [CANSparkMax.setOpenLoopRampRate](https://www.revrobotics.com/content/sw/max/sw-docs/java/com/revrobotics/CANSparkMax.html#setOpenLoopRampRate(double)) (I'm not familiar with what this is for, I would ask Daniel/Mr. Robertson about this.)
   3.  to set the current limit rates for the motor controllers using [CANSparkMax.setSmartCurrentLimit](https://www.revrobotics.com/content/sw/max/sw-docs/java/com/revrobotics/CANSparkMax.html#setSmartCurrentLimit(int)) which makes sure to limit the amount of current the motor controller will send to the motor. Since these are brushless motors, too much current can cause damage, ask the mentors about how this works or why it's bad.
   4.  to make the `leftFollower` follow the `leftLeader`, and vice versa 😀
   5.  to reset the encoders to be at position 0, because when we are creating the new instance of the DriveTrain upon first initialization of the Robot, it's safe to assume that the robot has not moved at all, and we don't want the encoders to think it has.

Right now, this is all happening in the constructor, which is fine, but constructors for a Java class shouldn't contain all the logic of the application, and when they become cluttered, it can become unclear what actually goes on. We'll talk more about constructors later. In the meantime, let's get to coding! As always, at the begining of the section and below in the summary you can see my code, but you should attempt this before looking at how I did it.

### Summary of Step 2:
As I linked to the top of this section, [this commit](https://github.com/LHSPantherbots/GonzoDriveTrain/commit/a4bff97e33723b2ffc9e27448a00c47367ea136c?branch=a4bff97e33723b2ffc9e27448a00c47367ea136c&diff=unified) shows the changes I made to achieve Step 2.
* I created a private method in DriveTrain.java called `setupDifferentialDriveSystem` in which we create carry out the necessary steps above from part 4 of Step 2 to set our system to the default state. As a result of this, the constructor for DriveTrain is nice and concise, it's just:
    ```java
    /** Creates a new DriveTrain. */
    public DriveTrain() {
        // Prepare our motor controllers and encoders for operation
        this.setupDifferentialDriveSystem();
    }
    ```
    Isn't that nice?
* To help make our code safe from [magic numbers](http://web.mit.edu/6.031/www/fa20/classes/03-code-review/#avoid_magic_numbers), I created the constants `OPEN_LOOP_RAMP_RATE` and `MAX_ALLOWED_CURRENT_LIMIT` to make things consistent. Becuase we use these constants, we can be certain that all the controllers receive the same values, which is a good practice to help make code **safe from bugs**.

## Step 3: Test the code!
This *should* be all you need to produce a working drive train! Once you reach this point, have Daniel show you how to build your code and test it on the Robot!

Good luck, and reach me at (936) 208-8329 if you have any questions. Text works best, but if it's a holistic question, we can call :)

- Gonzo