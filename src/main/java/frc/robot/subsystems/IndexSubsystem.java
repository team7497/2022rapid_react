// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.PortID;

public class IndexSubsystem extends SubsystemBase {
    /** Creates a new ExampleSubsystem. */
    private VictorSPX index, trigger;

    public IndexSubsystem() {
        index = new VictorSPX(PortID.index_indexing.value);
        trigger = new VictorSPX(PortID.index_trigger.value);

        MotorInit();
    }

    private void MotorInit() {
        index.configFactoryDefault();
        trigger.configFactoryDefault();

        index.setInverted(PortID.index_indexing.reversed);
        trigger.setInverted(PortID.index_trigger.reversed);

        index.setNeutralMode(NeutralMode.Brake);
        trigger.setNeutralMode(NeutralMode.Brake);
    }

    public void index(double speed) {
        index.set(ControlMode.PercentOutput, speed);
    }

    public void indexStop() {
        index.set(ControlMode.PercentOutput, 0.0f);
    }

    public void trigger(double speed) {
        trigger.set(ControlMode.PercentOutput, speed);
    }

    public void triggerStop() {
        trigger(0d);
    }
}
