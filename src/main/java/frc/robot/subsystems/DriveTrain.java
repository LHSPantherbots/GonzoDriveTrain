// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DriveTrain extends SubsystemBase {
  /** Creates a new DriveTrain. */
  public DriveTrain() {
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
}
