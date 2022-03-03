// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants;
import frc.robot.subsystems.LimeLight;
import frc.robot.subsystems.ShootSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

/** An example command that uses an example subsystem. */
public class AutoAim extends CommandBase {
    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
    private double          tx_angle;
    private ShootSubsystem  m_ShootSubsystem;

    /**
     * Creates a new LimeLightCommand.
     *
     * @param subsystem The subsystem used by this command.
     */
    public AutoAim(ShootSubsystem m_ShootSubsystem) {
        this.m_ShootSubsystem = m_ShootSubsystem;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(m_ShootSubsystem);
    }

    // 被whenPressed/whenReleased排程時使用
    // Called when the command is initially scheduled.
    @Override
    public void initialize()
    {

    }

    // 在排程中被呼叫時使用
    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute()
    {
        // 弳度量
        tx_angle = LimeLight.getTx();
        SmartDashboard.putNumber("GivedTx", tx_angle);
        // if (tx_angle > 5)           m_ShootSubsystem.turn(-1);
        // else if (tx_angle > 1)      m_ShootSubsystem.turn(-0.75);
        // else if (tx_angle > 0.2)    m_ShootSubsystem.turn(-0.5);
        // else if (tx_angle < -5)     m_ShootSubsystem.turn(1);
        // else if (tx_angle < -1)     m_ShootSubsystem.turn(0.75);
        // else if (tx_angle < -0.2)   m_ShootSubsystem.turn(0.5);
        // else                        m_ShootSubsystem.turn(0);

        if((tx_angle < 0.1f) && (tx_angle > -0.1f) && (tx_angle != 0.0f))
            m_ShootSubsystem.turnStop();
        else
            m_ShootSubsystem.turn(-tx_angle * Constants.kLimeLightAdjust);
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {}

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }
}
