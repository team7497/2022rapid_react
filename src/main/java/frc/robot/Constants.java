// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.tools.Gains;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    /**
     * Encoder每圈回傳的脈衝數
     * Resolution = PulsePerRevolution = CountPerRevolution / 4
     */
    public final static int EncoderResolution = 4096;

    /**
     * 馬達最大轉速 單位為RPS
     * RPS = RPM / 60
     */
    public static enum MaxRevolutionPerSecond{
        TalonFX(2000 / 60),
        NEO(5400 / 60);

        public final double value;
        MaxRevolutionPerSecond(double value) { this.value = value; }
    }

    public static enum PortID{
        left_front(2, false),
        left_back(4, false),
        right_front(3, true),
        right_back(5, true),
        
        shoot(0, false),
        shoot_turn(11, false),

        elevate_climb(6, true),
        elevate_rotate(7, false),

        index_indexing(8, true),
        index_trigger(9, true),

        intake(10, false),
        //以上為CAN port

        intake_solenoid(0, false);
        //以上為PCM port 

        public final int value;
        public final boolean reversed; //reversed

        PortID(int value, boolean reversed) {
            this.value = value;
            this.reversed = reversed;
        }
    }

    /**
     * 修正百分比輸出
     * 使用：最低電壓(base) + 目標狀態 * PercentageFix.target + 目標與當前狀態差 * PercentageFix.difference
     */
    public static enum PercentageFix{
        drive(0.3, 0.6, 0.25),
        shoot(0.1, 0.6, 0.15);

        public final double base;
        public final double target;
        public final double difference;
        PercentageFix(double base, double target, double difference) {
            this.base = base;
            this.target = target;
            this.difference = difference;
        }
    }

    public static final int kSlotIdx = 0;

    public static final int kPIDLoopIdx = 0;

    public static final int kTimeoutMs = 30;

    public final static Gains kGains_Shoot = new Gains(0.1, 0.001, 5, 1023.0/20660.0, 300, 1.00);

    public static final double  camera_height = 99.5, // in cm
                                camera_angle = 60 * Math.PI / 180; // in radian
    
    public static final double target_height    = 265.5;
    public static final double kMotorRampRate   = 0.25f;
    public static final double kLimeLightAdjust = 0.035f;
    public static final double kNavXAdjust      = 0.035f;
}