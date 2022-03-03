// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Axis;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.AutoAim;
import frc.robot.commands.AutoTurn;
import frc.robot.commands.DriveControl;
import frc.robot.commands.AutonomousCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ElevateSubsystem;
import frc.robot.subsystems.GyroSubsystem;
import frc.robot.subsystems.IndexSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.LimeLight;
import frc.robot.subsystems.ShootSubsystem;
import frc.robot.subsystems.ShootSubsystem.ShootVelocityPercentage;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ShootSubsystem    m_ShootSubsystem    = new ShootSubsystem();
  private final DriveSubsystem    m_DriveSubsystem    = new DriveSubsystem();
  private final ElevateSubsystem  m_ElevateSubsystem  = new ElevateSubsystem();
  private final IndexSubsystem    m_IndexSubsystem    = new IndexSubsystem();
  private final IntakeSubsystem   m_IntakeSubsystem   = new IntakeSubsystem();
  private final GyroSubsystem     m_GyroSubsystem     = new GyroSubsystem();
  private final LimeLight         m_LimeLight         = new LimeLight();

  private final DriveControl      c_DriveControl      = new DriveControl(m_DriveSubsystem);
  private final AutoAim           c_AutoAim           = new AutoAim(m_ShootSubsystem);
  private final AutoTurn          c_AutoTurn          = new AutoTurn(m_GyroSubsystem, c_DriveControl);

  private final SlewRateLimiter DriveLimiter = new SlewRateLimiter(1.5);
  private final Timer m_Timer = new Timer();

  private final XboxController xbox_drive = new XboxController(0);
  private final XboxController xbox_shoot = new XboxController(1);

  boolean IndexIsOn = false;

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();
    m_IntakeSubsystem.compressor();

    m_Timer.reset();
    m_Timer.start();
  }

  public ShootSubsystem getShootSubsystem() {
    return m_ShootSubsystem;
  }

  public DriveSubsystem getDriveSubsystem() {
    return m_DriveSubsystem;
  }

  public ElevateSubsystem getElevateSubsystem() {
    return m_ElevateSubsystem;
  }

  public IndexSubsystem getIndexSubsystem() {
    return m_IndexSubsystem;
  }

  public IntakeSubsystem getIntakeSubsystem() {
    return m_IntakeSubsystem;
  }

  public GyroSubsystem getGyroSubsystem() {
    return m_GyroSubsystem;
  }

  public LimeLight getLimeLight() {
    return m_LimeLight;
  }

  /**
   * 用whenPressed()/whenReleased()等函數 綁定狀態到後面執行的程式
   * 只需要執行一次就可以綁定成功
   * 
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    // **************
    // * xbox_drive *
    // **************

    new JoystickButton(xbox_drive, Button.kA.value).whileHeld(() -> {
      m_IntakeSubsystem.intake(0.5f);
      m_IndexSubsystem.index(0.5f);
    });
    new JoystickButton(xbox_drive, Button.kA.value).whenReleased(() -> {
      m_IntakeSubsystem.intakeStop();
      m_IndexSubsystem.indexStop();
    });
    new JoystickButton(xbox_drive, Button.kY.value).whileHeld(() -> {
      m_IntakeSubsystem.intake(-0.5f);
      m_IndexSubsystem.index(-0.5f);
    });
    new JoystickButton(xbox_drive, Button.kY.value).whenReleased(() -> {
      m_IntakeSubsystem.intakeStop();
      m_IndexSubsystem.indexStop();
    });
    new JoystickButton(xbox_drive, Button.kB.value).whenPressed(() -> m_IntakeSubsystem.setSolenoidOut());
    new JoystickButton(xbox_drive, Button.kX.value).whenPressed(() -> m_IntakeSubsystem.setSolenoidIn());

    new JoystickButton(xbox_drive, Button.kStart.value).whileHeld(c_AutoAim);

    new JoystickButton(xbox_drive, Button.kLeftBumper.value).whenPressed(() -> m_ElevateSubsystem.climb(-0.5f));
    new JoystickButton(xbox_drive, Button.kLeftBumper.value).whenReleased(() -> m_ElevateSubsystem.climbStop());
    new JoystickButton(xbox_drive, Button.kRightBumper.value).whenPressed(() -> m_ElevateSubsystem.climb(0.5f));
    new JoystickButton(xbox_drive, Button.kRightBumper.value).whenReleased(() -> m_ElevateSubsystem.climbStop());

    new POVButton(xbox_drive, 90).whenPressed(() -> m_ElevateSubsystem.rotate(-0.5f));
    new POVButton(xbox_drive, 90).whenReleased(() -> m_ElevateSubsystem.rotateStop());
    new POVButton(xbox_drive, 270).whenPressed(() -> m_ElevateSubsystem.rotate(0.5f));
    new POVButton(xbox_drive, 270).whenReleased(() -> m_ElevateSubsystem.rotateStop());
    
    // **************
    // * xbox_shoot *
    // **************

    // index

    new JoystickButton(xbox_shoot, XboxController.Button.kY.value).whileHeld(() -> m_IndexSubsystem.index(0.5f));
    new JoystickButton(xbox_shoot, XboxController.Button.kY.value).whenReleased(() -> m_IndexSubsystem.indexStop());

    new JoystickButton(xbox_shoot, XboxController.Button.kA.value).whenPressed(() -> m_IndexSubsystem.index(-0.5f));
    new JoystickButton(xbox_shoot, XboxController.Button.kA.value).whenReleased(() -> m_IndexSubsystem.indexStop());

    new JoystickButton(xbox_shoot, XboxController.Button.kB.value).whenPressed(() -> m_IndexSubsystem.trigger(0.5f));
    new JoystickButton(xbox_shoot, XboxController.Button.kB.value).whenReleased(() -> m_IndexSubsystem.triggerStop());
    
    new JoystickButton(xbox_shoot, XboxController.Button.kX.value).whenPressed(() -> m_IndexSubsystem.trigger(-0.5f));
    new JoystickButton(xbox_shoot, XboxController.Button.kX.value).whenReleased(() -> m_IndexSubsystem.triggerStop());

    //shoot
    new POVButton(xbox_shoot, 90).whenPressed(() -> m_ShootSubsystem.shoot(ShootVelocityPercentage.Slow));    // 0.5
    new POVButton(xbox_shoot, 90).whenReleased(() -> m_ShootSubsystem.shoot(ShootVelocityPercentage.Min));
    new POVButton(xbox_shoot, 0).whenPressed(() -> m_ShootSubsystem.shoot(ShootVelocityPercentage.Normal));   // 0.6
    new POVButton(xbox_shoot, 0).whenReleased(() -> m_ShootSubsystem.shoot(ShootVelocityPercentage.Min));
    new POVButton(xbox_shoot, 270).whenPressed(() -> m_ShootSubsystem.shoot(ShootVelocityPercentage.Quick));  // 0.7
    new POVButton(xbox_shoot, 270).whenReleased(() -> m_ShootSubsystem.shoot(ShootVelocityPercentage.Min));
    new POVButton(xbox_shoot, 180).whenPressed(() -> m_ShootSubsystem.shoot(ShootVelocityPercentage.Fast));   // 0.8
    new POVButton(xbox_shoot, 180).whenReleased(() -> m_ShootSubsystem.shoot(ShootVelocityPercentage.Min));

    new JoystickButton(xbox_shoot, Button.kStart.value).whileHeld(c_AutoAim);
    new JoystickButton(xbox_shoot, Button.kBack.value).whenPressed(() -> m_IntakeSubsystem.setSolenoidIn());
  }

  /**
   * 針對無法使用whenPressed()/whenReleased()的Axis 或需要複雜控制的Button
   * (事實上configureButtonBindings()裡面的條件都能改寫至此)
   * 需要在TeleopPeriodic()裡不斷執行
   */
  public void runXboxControllerDetection()
  {
    double drive_speed  = AxisDetection(xbox_drive, Axis.kRightTrigger.value) - AxisDetection(xbox_drive, Axis.kLeftTrigger.value);
    double drive_turn   = AxisDetection(xbox_drive, Axis.kRightX.value) + AxisDetection(xbox_drive, Axis.kLeftX.value);

    SmartDashboard.putNumber("origin_drive_speed", drive_speed);

    drive_speed = DriveLimiter.calculate(drive_speed);
    
    if (Math.abs(drive_speed) > .1 || Math.abs(drive_turn) > .1)
    {
      c_AutoTurn.setStatus(false);
      c_DriveControl.ArcadeDrive(drive_speed, drive_turn);
    }
    else if(m_Timer.get() < 0.25)
    {
      drive_speed = DriveLimiter.calculate(0);
      c_DriveControl.ArcadeDrive(drive_speed, 0);
      c_AutoTurn.reset();
    }
    else if(c_AutoTurn.getStatus())
    {
      c_AutoTurn.execute();
    }
    else if (!c_AutoTurn.getStatus())
    {
      c_AutoTurn.setStatus(true);
      m_Timer.reset();
    }

    SmartDashboard.putNumber("drive_speed", drive_speed);

    double shoot_turn = -(AxisDetection(xbox_shoot, Axis.kRightTrigger.value) - AxisDetection(xbox_shoot, Axis.kLeftTrigger.value)) / 2;
    if (!xbox_shoot.getLeftStickButton())
    {
      m_ShootSubsystem.turn(shoot_turn);        
    }
  }

  private double AxisDetection(XboxController xbox, int axis) {
    double temp = xbox.getRawAxis(axis);
    if (Math.abs(temp) > 0.1)
      return temp;
    return 0;
  }

  /**X
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    AutonomousCommand c_AutonomousCommand = new AutonomousCommand(m_DriveSubsystem, m_GyroSubsystem, m_IntakeSubsystem, m_IndexSubsystem, m_ShootSubsystem, m_LimeLight, c_AutoAim);

    return c_AutonomousCommand;
  }
}
