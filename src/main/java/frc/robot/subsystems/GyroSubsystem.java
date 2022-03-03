package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class GyroSubsystem extends SubsystemBase {
    public AHRS gyro;

    public GyroSubsystem() {
        gyro = new AHRS(SPI.Port.kMXP);
    }

    public double getVelocityX() {
        return gyro.getVelocityX();
    }
    
    public double getVelocityY() {
        return gyro.getVelocityY();
    }

    public double getVelocityZ() {
        return gyro.getVelocityZ();
    }

    public double getYaw() {
        return gyro.getYaw();
    }

    public void resetEncoder() {
        gyro.reset();
        System.out.println("ResetEncoder ensure");
    }
    @Override
    public void periodic() {
        double angle = getYaw();
        SmartDashboard.putNumber("angle", angle);
    }
}