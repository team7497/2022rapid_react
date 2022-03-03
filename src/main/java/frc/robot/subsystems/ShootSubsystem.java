// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.MaxRevolutionPerSecond;
import frc.robot.Constants.PercentageFix;
import frc.robot.Constants.PortID;
import frc.robot.tools.VelocityTransform;

public class ShootSubsystem extends SubsystemBase {
  private TalonFX shoot;
  private VictorSPX shoot_turn;
  // 宣告Faults類別的暫存
  private Faults temp;
  private double shoot_RPS;
  private VelocityTransform transform;

  public static enum ShootVelocityPercentage {
    Max(1),
    Fast(0.8),
    Quick(0.7),
    Normal(0.6),
    Slow(0.5),
    Min(0);

    public double value;
    ShootVelocityPercentage(double value) { this.value = value; }
  }

  public ShootSubsystem() {
    MotorInit();
    EncoderInit();
    transform = new VelocityTransform(PercentageFix.shoot);
  }

  @Override
  public void periodic() {
    //使馬達控制器將Encoder的讀取值暫存至temp變數
    shoot.getFaults(temp);

    // 向馬達控制器獲取目前脈衝速率
    // 脈衝速率會從temp變數中讀取
    // 
    // Encoder的脈衝速率單位為 Pulse/ 100 ms
    // 除以EncoderResolution *10 獲得每秒旋轉的圈數(RPS)
    shoot_RPS   = shoot.getSelectedSensorVelocity()   / Constants.EncoderResolution * 10.0;
  }

  public double getSpeed() {
    return shoot_RPS;
  }

  public void shoot(ShootVelocityPercentage mode) {
    switch (mode){
      case Min:
        setVoltagePercentage(0);
        break;
      default:
        shoot(mode.value, mode.value);
    }
  }

  /**
   * 設定速度
   * 最大速度為 RPS * 輪的圓周長
   * 利用PercentageFix使當前速度逼近目標速度
   */
  private void shoot(double left, double right)
  {
    double left_speed_percentage    = transform.GetOutput(left, shoot_RPS, MaxRevolutionPerSecond.TalonFX.value);

    setVoltagePercentage(left_speed_percentage);
  }
  public void shootStop()
  {
    setVoltagePercentage(0.0);
  }

  public void turn(double speed)
  {
    speed *= 0.4;
    shoot_turn.set(ControlMode.PercentOutput, speed);
  }

  public void turnStop()
  {
    shoot_turn.set(ControlMode.PercentOutput, 0.0f);
  }

  public void setVelocityRPS(ShootVelocityPercentage mode) {
    setVelocityRPS(mode.value * MaxRevolutionPerSecond.TalonFX.value);
  }

  public void setVelocityRPS(double Target_RPS) {
    shoot.set(ControlMode.Velocity, VelocityTransform.RPStoPulsePer100ms(Target_RPS, Constants.EncoderResolution));
  }

  /**
   * 設定電壓百分比
   * 最大電壓為1.0
   */
  public void setVoltagePercentage(double speed) {
    shoot.set(ControlMode.PercentOutput, speed);
  }

  private void EncoderInit() {
    temp = new Faults();
  }

  private void MotorInit() {
    shoot = TalonFXInitModule(PortID.shoot);
    shoot_turn = VictorSPXInitModule(PortID.shoot_turn);
  }

  private TalonFX TalonFXInitModule(PortID id) {
    TalonFX talonfx = new TalonFX(id.value);
    talonfx.configFactoryDefault();
    talonfx.configNeutralDeadband(0.001);
    talonfx.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, Constants.kPIDLoopIdx, Constants.kTimeoutMs);
    talonfx.setInverted(id.reversed);
    talonfx.setNeutralMode(NeutralMode.Coast);
    talonfx.configOpenloopRamp(1);
    /* Config the peak and nominal outputs */
		talonfx.configNominalOutputForward(0, Constants.kTimeoutMs);
		talonfx.configNominalOutputReverse(0, Constants.kTimeoutMs);
		talonfx.configPeakOutputForward(1, Constants.kTimeoutMs);
		talonfx.configPeakOutputReverse(-1, Constants.kTimeoutMs);

		/* Config the Velocity closed loop gains in slot0 */
		talonfx.config_kF(Constants.kPIDLoopIdx, Constants.kGains_Shoot.kF, Constants.kTimeoutMs);
		talonfx.config_kP(Constants.kPIDLoopIdx, Constants.kGains_Shoot.kP, Constants.kTimeoutMs);
		talonfx.config_kI(Constants.kPIDLoopIdx, Constants.kGains_Shoot.kI, Constants.kTimeoutMs);
    talonfx.config_kD(Constants.kPIDLoopIdx, Constants.kGains_Shoot.kD, Constants.kTimeoutMs);
    return talonfx;
  }

  private VictorSPX VictorSPXInitModule(PortID id) {
    VictorSPX victorspx = new VictorSPX(id.value);
    victorspx.configFactoryDefault();
    victorspx.setInverted(id.reversed);
    victorspx.setNeutralMode(NeutralMode.Brake);
    return victorspx;
  }
}
