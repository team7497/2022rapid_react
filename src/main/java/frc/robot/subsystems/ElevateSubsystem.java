// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.PortID;


public class ElevateSubsystem extends SubsystemBase
{
  /** Creates a new ExampleSubsystem. */
  private TalonFX climb;
  private TalonFX rotate;

  public ElevateSubsystem()
  {
    climb   = new TalonFX(PortID.elevate_climb.value);
    rotate  = new TalonFX(PortID.elevate_rotate.value);

    MotorInit();
  }

  private void MotorInit()
  {
    climb.configFactoryDefault();
    rotate.configFactoryDefault();

    climb.setInverted(PortID.elevate_climb.reversed);
    rotate.setInverted(PortID.elevate_rotate.reversed);

    climb.setNeutralMode(NeutralMode.Brake);
    rotate.setNeutralMode(NeutralMode.Brake);

    climb.setSelectedSensorPosition(0);
  }

  public void climb(double speed)
  {
    climb.set(ControlMode.PercentOutput, speed);
  }

  public void climbStop()
  {
    climb(0.0f);
  }

  public void rotate(double speed)
  {
    rotate.set(ControlMode.PercentOutput, speed);
  }

  public void rotateStop()
  {
    rotate(0.0f);
  }

  public void resetClimbEncoder()
  {
    climb.setSelectedSensorPosition(0);
  }

  public void setClimbMode(NeutralMode mode)
  {
    try
    {
      climb.setNeutralMode(mode);
    }
    catch(Exception e)
    {
      System.out.printf("Fail to set NeutralMode of climb motor");
    }
  }

  @Override
  public void periodic()
  {
    double climb_position = climb.getSelectedSensorPosition();
    SmartDashboard.putNumber("Climb_Position", climb_position);
  }
}