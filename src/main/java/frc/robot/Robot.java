// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.sql.Time;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.VideoSource;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.commands.AutoAim;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ElevateSubsystem;
import frc.robot.subsystems.GyroSubsystem;
import frc.robot.subsystems.LimeLight;
import frc.robot.subsystems.ShootSubsystem;
import frc.robot.subsystems.LimeLight.LightMode;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private ShootSubsystem    m_ShootSubsystem;
  private ElevateSubsystem  m_ElevateSubsystem;
  private GyroSubsystem     m_GyroSubsystem;
  private DriveSubsystem    m_DriveSubsystem;
  private LimeLight         m_LimeLight;
  private Command           m_autonomousCommand;
  private AutoAim           m_AutoAim;
  private Timer             m_timer;

  private VideoSource       raspberryPi_Camera;

  private RobotContainer    m_robotContainer;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();

    m_ShootSubsystem    = m_robotContainer.getShootSubsystem();
    m_ElevateSubsystem  = m_robotContainer.getElevateSubsystem();
    m_DriveSubsystem    = m_robotContainer.getDriveSubsystem();
    m_GyroSubsystem     = m_robotContainer.getGyroSubsystem();
    m_LimeLight         = m_robotContainer.getLimeLight();
    
    m_timer = new Timer();
    CameraServer.startAutomaticCapture(0);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
    m_timer.reset();
    m_timer.start();
    try{
      m_ElevateSubsystem.setClimbMode(NeutralMode.Coast);
    }catch(Exception e){
      System.out.printf("Fail to set climb mode of ElevateSubsystem.");
    }
    m_LimeLight.setLightMode(LightMode.Off);
  }

  @Override
  public void disabledPeriodic() {
    if (m_timer.get() >= 0.5)
      m_DriveSubsystem.setDriveMode(IdleMode.kCoast);
  }

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    m_LimeLight.setLightMode(LightMode.On);
    m_DriveSubsystem.setDriveMode(IdleMode.kBrake);
    
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    m_autonomousCommand.initialize();
    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
      if (m_autonomousCommand.isScheduled())
        System.out.printf("AutonomousCommand is scheduled");
      else
        System.out.printf("fail to schedule");
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    m_DriveSubsystem.setDriveMode(IdleMode.kBrake);
    m_LimeLight.setLightMode(LightMode.On);
    m_GyroSubsystem.resetEncoder();
    
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    m_ElevateSubsystem.resetClimbEncoder();
    m_ElevateSubsystem.setClimbMode(NeutralMode.Brake);

  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("shoot_motor speed", m_ShootSubsystem.getSpeed());
    if (LimeLight.getTv() == 1)
      SmartDashboard.putNumber("target_distance", LimeLight.getDistance());
    else
      SmartDashboard.putNumber("target_distance", -1);
    try{
      m_robotContainer.runXboxControllerDetection();
    }catch(Exception e){
      System.out.printf("Fail to run detection.");
    }
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
