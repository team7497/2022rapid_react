// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.GyroSubsystem;
import frc.robot.subsystems.IndexSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.LimeLight;
import frc.robot.subsystems.ShootSubsystem;
import frc.robot.subsystems.ShootSubsystem.ShootVelocityPercentage;

/** An example command that uses an example subsystem. */
public class AutonomousCommand extends CommandBase {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})

  private DriveSubsystem  m_DriveSubsystem;
  private GyroSubsystem   m_GyroSubsystem;
  private IntakeSubsystem m_IntakeSubsystem;
  private IndexSubsystem  m_IndexSubsystem;
  private ShootSubsystem  m_ShootSubsystem;
  private LimeLight       m_LimeLight;

  private AutoAim       c_AutoAim;
  private AutoTurn      c_AutoTurn;
  private DriveControl  c_DriveControl;

  private SlewRateLimiter m_Limiter;

  private boolean finished = false;
  private Timer timer;

  private enum PathType{
    straight_intake,  // shoot, backward, turn 180 degrees, forward, intake, turn 180 degrees, forward, shoot
    basic,  // shoot, backward
    turn_intake;  // shoot, turn 170 degrees, forward, intake, turn -135 degrees, forward, shoot
  }

  private PathType type;
  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public AutonomousCommand(DriveSubsystem m_DriveSubsystem, GyroSubsystem m_GyroSubsystem, IntakeSubsystem m_IntakeSubsystem, IndexSubsystem m_IndexSubsystem, ShootSubsystem m_ShootSubsystem, LimeLight m_LimeLight, AutoAim c_AutoAim) {
    this.m_DriveSubsystem   = m_DriveSubsystem;
    this.m_GyroSubsystem    = m_GyroSubsystem;
    this.m_IntakeSubsystem  = m_IntakeSubsystem;
    this.m_IndexSubsystem   = m_IndexSubsystem;
    this.m_ShootSubsystem   = m_ShootSubsystem;
    this.m_LimeLight        = m_LimeLight;

    c_DriveControl  = new DriveControl(m_DriveSubsystem);
    c_AutoAim       = new AutoAim(m_ShootSubsystem);
    c_AutoTurn      = new AutoTurn(m_GyroSubsystem, c_DriveControl);

    m_Limiter = new SlewRateLimiter(2);

    timer = new Timer();

    type = PathType.basic;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_DriveSubsystem, m_GyroSubsystem, m_IntakeSubsystem, m_LimeLight, m_ShootSubsystem, m_IndexSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    timer.reset();
    timer.start();
    finished = false;
  }

  // 在排程中被呼叫時使用
  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute()
  {
    switch (type) {
      case straight_intake:
        PathStraightIntake();
        break;
      case turn_intake:
        PathTurnIntake();
        break;
      case basic:
        PathBasic();
        break;
    }

    // 停止
    if (timer.get() > 15.0f)
    {
      m_IndexSubsystem.triggerStop();
      m_IndexSubsystem.indexStop();
      m_IntakeSubsystem.intakeStop();
      m_ShootSubsystem.shoot_voltage(0.0f);
      c_AutoAim.isFinished();
      turnDrive(0.0f);
      moveDrive(0.0f);
      
      finished = true;
      end(true);
    }
  }

  private void PathTurnIntake(){
    // 瞄 & falcon先轉
    if (timer.get() <= 3.0f)
    {
      Aim();
      shoot_voltage(0.6f);
    }
    // 射球
    if (timer.get() > 3.0f && timer.get() <= 5.0f)
    {
      Aim();
      shoot_voltage(0.6f);
      trigger(0.5);
      index(-0.5);
    }
    // 轉向
    if (timer.get() > 5.0f && timer.get() <= 6.5f)
    {
      double original_angle = m_GyroSubsystem.getYaw();
      double final_angle = original_angle + 170;
      c_AutoAim.isFinished();

      while (true)
      {
        turnDrive(0.3);
        if (m_GyroSubsystem.getYaw() > final_angle)
          break;
      }
    }
    // 後退 & 吸球
    if (timer.get() > 6.5f && timer.get() <= 9.0f)
    {
      moveDrive(0.1f);
      intake(-0.5f);
      index(-0.5f);

      if (timer.get() > 8.0f)
      {
        setSolenoidOut();
      }
    }
    // 轉向
    if (timer.get() > 9.0f && timer.get() <= 10.5f)
    {
      setSolenoidIn();
    }
    // 瞄 & falcon先轉
    if (timer.get() > 10.5f && timer.get() <= 13.5f)
    {
      Aim();
      shoot_voltage(0.6f);
    }
    // 射球
    if (timer.get() > 13.5f && timer.get() <= 15.0f)
    {
      Aim();
      shoot_voltage(0.6f);
      index(-0.5f);
      trigger(0.5f);
    }
  }
  
  private void PathStraightIntake(){}

  private void PathBasic(){
    if (timer.get() < .5) {
      Aim();
      shoot_voltage(.6);
    }
    if (timer.get() > .3 && timer.get() < .5) {
      trigger(.5);
      index(-.5);
    }
    else if (timer.get() > .5 && timer.get() < 1.3) {
      moveDrive(.5);
      m_Limiter.reset(.5);
    }
    else if (timer.get() > 1.3 && timer.get() < 1.5) {
      moveDrive(m_Limiter.calculate(0));
    }
    else if (timer.get() > 1.5 && timer.get() < 2.5) {
      c_AutoTurn.Turn(180);
    }
    else if (timer.get() > 2.5 && timer.get() < 3.3) {
      moveDrive(.45);
      m_Limiter.reset(.45);
    }
    else if (timer.get() > 3.3 && timer.get() < 3.5) {
      moveDrive(m_Limiter.calculate(0));
    }
    else if (timer.get() > 3.5 && timer.get() < 4.5) {
      c_AutoTurn.Turn(180);
    }
    else if (timer.get() > 4.5 && timer.get() < 5.3) {
      moveDrive(.45);
      m_Limiter.reset(.45);
    }
    else if (timer.get() > 5.3 && timer.get() < 5.5) {
      moveDrive(m_Limiter.calculate(0));
    }
    if (timer.get() < 6 && timer.get() > 5.5) {
      Aim();
      shoot_voltage(.6);
    }
    if (timer.get() > 6 && timer.get() < 5.8) {
      trigger(.5);
      index(-.5);
    }
  }

  private void moveDrive(double value)
  {
    c_DriveControl.ArcadeDrive(value, 0);
  }

  private void turnDrive(double value)
  {
    c_DriveControl.ArcadeDrive(0, value);
  }

  private void Aim()
  {
    double tx_angle = LimeLight.getTx();
    if((tx_angle < 0.1f) && (tx_angle > -0.1f) && (tx_angle != 0.0f))
      m_ShootSubsystem.turnStop();
    else
      m_ShootSubsystem.turn(-tx_angle * Constants.kLimeLightAdjust);
  }

  private void index(double speed)
  {
    m_IndexSubsystem.index(speed);
  }

  private void trigger(double speed)
  {
    m_IndexSubsystem.trigger(speed);
  }

  private void intake(double speed)
  {
    m_IntakeSubsystem.intake(speed);
  }

  private void shoot_voltage(double speed)
  {
    m_ShootSubsystem.shoot.set(ControlMode.PercentOutput, speed);
  }

  private void setSolenoidOut()
  {
    m_IntakeSubsystem.setSolenoidOut();
  }

  private void setSolenoidIn()
  {
    m_IntakeSubsystem.setSolenoidIn();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_IndexSubsystem.triggerStop();
    m_IndexSubsystem.indexStop();
    m_IntakeSubsystem.intakeStop();
    m_ShootSubsystem.shoot(ShootVelocityPercentage.Min);
    c_AutoAim.isFinished();
    c_DriveControl.ArcadeDrive(0.0, 0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
