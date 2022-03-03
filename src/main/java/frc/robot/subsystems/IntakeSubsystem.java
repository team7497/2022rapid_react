// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.PortID;

public class IntakeSubsystem extends SubsystemBase {
    private VictorSPX intake;
    private Compressor airCompressor;
    private Solenoid switchSolenoid;
    private boolean isCompressorOn;
        
    public IntakeSubsystem() {
        airCompressor = new Compressor(PneumaticsModuleType.CTREPCM);
        switchSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, PortID.intake_solenoid.value);
        intake = new VictorSPX(PortID.intake.value);
        MotorInit();
    }

    private void MotorInit() {
        intake.configFactoryDefault();
        intake.setInverted(PortID.intake.reversed);
        intake.setNeutralMode(NeutralMode.Brake);
    }

    public void intake(double speed) {
        intake.set(ControlMode.PercentOutput, speed);
    }

    public void intakeStop() {
        intake(0d);
    }

    public void compressor() {
        isCompressorOn = true;
        airCompressor.enableDigital();
    }

    public void compressorStop() {
        isCompressorOn = false;
        airCompressor.disable();
    }

    public void toggleCompressor() {
        if (isCompressorOn)
            compressorStop();
        else
            compressor();
    }

    public void setSolenoidOut()
    {
        switchSolenoid.set(true);
    }

    public void setSolenoidIn()
    {
        switchSolenoid.set(false);
    }
}
