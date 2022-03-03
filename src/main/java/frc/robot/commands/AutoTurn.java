package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.GyroSubsystem;

public class AutoTurn extends CommandBase {
    private GyroSubsystem m_GyroSubsystem;
    private DriveControl c_DriveControl;
    private double angle;
    private boolean status;

    public AutoTurn(GyroSubsystem m_GyroSubsystem, DriveControl c_DriveControl) {
        this.m_GyroSubsystem = m_GyroSubsystem;
        this.c_DriveControl = c_DriveControl;

        addRequirements(m_GyroSubsystem);
    }

    public void reset() {
        m_GyroSubsystem.resetEncoder();
    }

    @Override
    public void initialize() {
        m_GyroSubsystem.resetEncoder();
    }

    @Override
    public void execute() {
        if (status) {
            angle = m_GyroSubsystem.getYaw();
            // if(angle > 0)
            // {
            //     if(angle > 30)          c_DriveControl.ArcadeDrive(0, -.8);
            //     else if(angle > 15)     c_DriveControl.ArcadeDrive(0, -.75);
            //     else if(angle > 5)      c_DriveControl.ArcadeDrive(0, -.7);
            //     else if (angle > 2)     c_DriveControl.ArcadeDrive(0, -.3);
            //     else                    c_DriveControl.ArcadeDrive(0, 0);
            // }
            // else
            // {
            //     if(angle < -30)         c_DriveControl.ArcadeDrive(0, .8);
            //     else if(angle < -15)    c_DriveControl.ArcadeDrive(0, .75);
            //     else if(angle < -5)     c_DriveControl.ArcadeDrive(0, .7);
            //     else if (angle < -2)    c_DriveControl.ArcadeDrive(0, .3);
            //     else                    c_DriveControl.ArcadeDrive(0, 0);
            // }

            if((angle < 1) && (angle > -1))
                c_DriveControl.ArcadeDrive(0, 0);
            else
                c_DriveControl.ArcadeDrive(0, - angle * Constants.kNavXAdjust);
        }
    }

    public void toggleStatus() {
        setStatus(!getStatus());
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }
}
