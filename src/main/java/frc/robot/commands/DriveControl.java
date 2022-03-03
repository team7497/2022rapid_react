package frc.robot.commands;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.MaxRevolutionPerSecond;
import frc.robot.Constants.PercentageFix;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.tools.VelocityTransform;

public class DriveControl extends CommandBase
{
    private DriveSubsystem m_DriveSubsystem;
    private VelocityTransform transform;

    public DriveControl(DriveSubsystem m_DriveSubsystem)
    {
        this.m_DriveSubsystem = m_DriveSubsystem;
        addRequirements(m_DriveSubsystem);
    }

    @Override
    public void initialize()
    {
        transform = new VelocityTransform(PercentageFix.drive);
    }

    @Override
    public void execute()
    {
        
    }

    public void ArcadeDrive(double speed, double twist)
    {
        speed *= 0.6;
        twist *= 0.25;
    
        m_DriveSubsystem.setVoltagePercentage(speed + twist, speed - twist);
    }

    private void setVelocityPercentage(double left, double right)
    {
        double left_speed_percentage    = transform.GetOutput(left, m_DriveSubsystem.left_RPS, MaxRevolutionPerSecond.NEO.value);
        double right_speed_percentage    = transform.GetOutput(right, m_DriveSubsystem.right_RPS, MaxRevolutionPerSecond.NEO.value);
    
        m_DriveSubsystem.setVoltagePercentage(left_speed_percentage, right_speed_percentage);
    }
}
