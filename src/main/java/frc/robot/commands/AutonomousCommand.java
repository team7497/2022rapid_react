// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.GyroSubsystem;
import frc.robot.subsystems.IndexSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.LimeLight;
import frc.robot.subsystems.ShootSubsystem;
import frc.robot.subsystems.ShootSubsystem.ShootVelocityPercentage;

/** An example command that uses an example subsystem. */
public class AutonomousCommand extends CommandBase {
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
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})

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

    m_Limiter = new SlewRateLimiter(1.5);

    timer = new Timer();

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
    if (timer.get() <= 3.0f)                                        // 瞄 & falcon先轉
    {
      c_AutoAim.execute();
      m_ShootSubsystem.shoot(ShootVelocityPercentage.Normal);
      m_Limiter.reset(-0.5);
    }
    else if (timer.get() > 3.0f && timer.get() <= 5.0f)             // 射球
    {
      c_AutoAim.execute();
      m_ShootSubsystem.shoot(ShootVelocityPercentage.Normal);
      m_IndexSubsystem.trigger(0.5);
      m_IndexSubsystem.index(-0.5);
    }
    else if (timer.get() > 5.0f && timer.get() <= 6.5f)             // 轉向
    {
      turnDrive(0.5);
      moveDrive(m_Limiter.calculate(0));
    }
    else if (timer.get() > 6.5f && timer.get() <= 9.0f)             // 後退 & 吸球
    {
      moveDrive(0.5f);
      m_IntakeSubsystem.setSolenoidOut();
      m_IntakeSubsystem.intake(0.5f);
      m_IndexSubsystem.index(-0.5f);
    }
    else if (timer.get() > 9.0f && timer.get() <= 10.5)             // 轉向
    {
      turnDrive(0.5);
    }
    else if (timer.get() > 10.5f && timer.get() <= 13.5)            // 瞄 & falcon先轉
    {
      c_AutoAim.execute();
      m_ShootSubsystem.shoot(ShootVelocityPercentage.Normal);
    } 
    else if (timer.get() > 13.5f && timer.get() <= 15.0f)           // 射球
    {
      c_AutoAim.execute();
      m_ShootSubsystem.shoot(ShootVelocityPercentage.Normal);
      m_IndexSubsystem.index(-0.5f);
      m_IndexSubsystem.trigger(0.5f);
    }
    else if (timer.get() > 15.0f)                                   // 停止
    {
      m_IndexSubsystem.triggerStop();
      m_IndexSubsystem.indexStop();
      m_IntakeSubsystem.intakeStop();
      m_ShootSubsystem.shoot(ShootVelocityPercentage.Min);
      c_AutoAim.isFinished();
      c_DriveControl.ArcadeDrive(0.0, 0.0);
      
      finished = true;
      end(true);
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
