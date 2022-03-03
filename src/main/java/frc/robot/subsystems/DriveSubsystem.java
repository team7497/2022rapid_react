// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.PortID;

public class DriveSubsystem extends SubsystemBase {
  private CANSparkMax left_front  = new CANSparkMax(PortID.left_front.value, MotorType.kBrushless);
  private CANSparkMax left_back   = new CANSparkMax(PortID.left_back.value, MotorType.kBrushless);
  private CANSparkMax right_front = new CANSparkMax(PortID.right_front.value, MotorType.kBrushless);
  private CANSparkMax right_back  = new CANSparkMax(PortID.right_back.value, MotorType.kBrushless);
  public double left_RPS, right_RPS;

  public DriveSubsystem()
  {
    MotorInit();
    // ControllerInit();
  }

  @Override
  public void periodic()
  {
    //CANEncoder直接給予RPM
    // left_RPS = left_encoder.getVelocity() / 60.0;
    // right_RPS = right_encoder.getVelocity() / 60.0;
  }

  public void setDriveMode(IdleMode mode) {
    left_front.setIdleMode(mode);
    left_back.setIdleMode(mode);
    right_front.setIdleMode(mode);
    right_back.setIdleMode(mode);
  }

  public void setVoltagePercentage(double left, double right)
  {
    left_front.set(left);
    left_back.set(left);
    right_front.set(right);
    right_back.set(right);
  }

  private void MotorInit()
  {
    left_front.restoreFactoryDefaults();
    left_back.restoreFactoryDefaults();
    right_front.restoreFactoryDefaults();
    right_back.restoreFactoryDefaults();

    left_front.setInverted(PortID.left_front.reversed);
    left_back.setInverted(PortID.left_back.reversed);
    right_front.setInverted(PortID.right_front.reversed);
    right_back.setInverted(PortID.right_back.reversed);

    left_front.setIdleMode(IdleMode.kBrake);
    left_back.setIdleMode(IdleMode.kBrake);
    right_front.setIdleMode(IdleMode.kBrake);
    right_back.setIdleMode(IdleMode.kBrake);
  }
}
