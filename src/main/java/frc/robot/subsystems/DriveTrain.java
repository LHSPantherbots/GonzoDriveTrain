// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DriveTrain extends SubsystemBase {

  // Set up the motor controllers
  CANSparkMax leftLeader = new CANSparkMax(7, MotorType.kBrushless);
  CANSparkMax leftFollower = new CANSparkMax(8, MotorType.kBrushless);
  CANSparkMax rightLeader = new CANSparkMax(5, MotorType.kBrushless);
  CANSparkMax rightFollower = new CANSparkMax(6, MotorType.kBrushless);

  // Set up the encoders by extracting them from the leader controllers.
  CANEncoder leftEncoder = leftLeader.getEncoder();
  CANEncoder rightEncoder = rightLeader.getEncoder();

  // Sets up differental drive, using the leader controllers as the left and right
  // motor controllers
  DifferentialDrive drive = new DifferentialDrive(leftLeader, rightLeader);

  // The default open loop ramp rate for the drive train
  private final double OPEN_LOOP_RAMP_RATE = 0.7;

  // The current limit in amps for the motor controllers to prevent motor damage
  private final int MAX_ALLOWED_CURRENT_LIMIT = 60;

  /** Creates a new DriveTrain. */
  public DriveTrain() {

    // Prepare our motor controllers and encoders for operation
    this.setupDifferentialDriveSystem();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  /**
   * This method will drive the drive train from the current values of the right
   * thumb stick in the provided joystick instance. Once the move and turn values
   * are extracted, the {@link #teleopDrive(double, double)} method will be
   * called.
   * 
   * @param joystick the joystick for which to read the values from to teleop
   *                 drive the robot.
   */
  public void teleopDrive(Joystick joystick) {
    // No magic numbers :) Now you know exactly what the axis' are
    // TODO don't know if these axis values are correct!
    int rightHorizontalAxis = 3;
    int rightVerticalAxis = 4;

    double move = -joystick.getRawAxis(rightVerticalAxis);
    double turn = joystick.getRawAxis(rightHorizontalAxis);

    // now that we have the move and turn values extracted from the
    // provided joystick class, call the teleopDrive method
    teleopDrive(move, turn);
  }

  /**
   * Drive the robot for the specified amount of move and turn
   * 
   * @param move the amount to move
   * @param turn the amount to turn
   */
  public void teleopDrive(double move, double turn) {
    // if I were to leave this uncommented, the code would not compile
    // because we haven't defined our DifferentialDrive yet, we'll do that
    // in the next step :-)
    // drive.arcadeDrive(move, turn);
  }

  /**
   * Reset the position of both encoders to 0
   */
  public void resetEncoders() {
    leftEncoder.setPosition(0);
    rightEncoder.setPosition(0);
  }

  /**
   * Set's up the motor controllers and encoders for the drive train by setting
   * everything to the default states as well as making sure max allow-able
   * current limits are set for the motor controllers.
   * 
   * This method is private because only the constructor calls it.
   */
  private void setupDifferentialDriveSystem() {
    // Resets motor controllers to default conditions
    leftLeader.restoreFactoryDefaults();
    leftFollower.restoreFactoryDefaults();
    rightLeader.restoreFactoryDefaults();
    rightFollower.restoreFactoryDefaults();

    leftLeader.setOpenLoopRampRate(OPEN_LOOP_RAMP_RATE);
    rightLeader.setOpenLoopRampRate(OPEN_LOOP_RAMP_RATE);

    leftLeader.setSmartCurrentLimit(MAX_ALLOWED_CURRENT_LIMIT);
    leftFollower.setSmartCurrentLimit(MAX_ALLOWED_CURRENT_LIMIT);
    rightLeader.setSmartCurrentLimit(MAX_ALLOWED_CURRENT_LIMIT);
    rightFollower.setSmartCurrentLimit(MAX_ALLOWED_CURRENT_LIMIT);

    // Sets up follower motors
    leftFollower.follow(leftLeader);
    rightFollower.follow(rightLeader);

    // Sets up endcoders
    leftEncoder = leftLeader.getEncoder();
    rightEncoder = rightLeader.getEncoder();

    resetEncoders();
  }
}
