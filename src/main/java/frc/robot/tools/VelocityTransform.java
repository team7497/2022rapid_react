package frc.robot.tools;

import frc.robot.Constants.PercentageFix;

/**
 * A tool created for transforming velocity into voltage.
 */
public class VelocityTransform {
    private double target_velocity, difference_velocity;
    private double min_voltage, target_fix, difference_fix;
    private double output_voltage;

    /**
     * Set mode of transform.
     * @param mode percentagefix mode
     */
    public VelocityTransform(PercentageFix mode) {
        min_voltage = mode.base;
        target_fix = mode.target;
        difference_fix = mode.difference;
    }

    /**
     * Set percentage of target velocity and current velocity.
     * @param target_percentage target percentage of velocity
     * @param current_percentage current percentage of velocity
     */
    public void SetVelocity(double target_percentage, double current_percentage) {
        target_velocity = target_percentage;
        difference_velocity = target_percentage - current_percentage;
    }

    /**
     * Set RPS of encoder and the percentage of velocity.
     * @param target_percentage percentage of target velocity
     * @param current_RPS RPS of encoder
     * @param max_RPS max RPS of motor
     */
    public void setRPS(double target_percentage, double current_RPS, double max_RPS) {
        SetVelocity(target_percentage, current_RPS / max_RPS);
    }

    /**
     * Get output_voltage.
     * @return percentage of voltage
     */
    public double GetOutput() {
        output_voltage = min_voltage + target_velocity * target_fix + difference_velocity * difference_fix;
        return PecentageProtect(output_voltage);
    }

    /**
     * Get output_voltage by giving full info.
     * @param target_percentage target percentage of velocity
     * @param current_percentage current percentage of velocity
     * @return percentage of voltage
     */
    public double GetOutput(double target_percentage, double current_percentage) {
        target_velocity = target_percentage;
        difference_velocity = target_percentage - current_percentage;
        return GetOutput();
    }

    /**
     * Get output_voltage by giving full info.
     * @param target_percentage target percentage of velocity
     * @param current_RPS RPS of encoder
     * @param max_RPS max RPS of motor
     * @return percentage of voltage
     */
    public double GetOutput(double target_percentage, double current_RPS, double max_RPS) {
        return GetOutput(target_percentage, current_RPS / max_RPS);
    }

    public static double RPStoPulsePer100ms(double RPS, int EncoderResolution) {
        return RPS * 10 * EncoderResolution;
    }

    private double PecentageProtect(double input) {
        if (input > 1) return 1;
        else if (input < -1) return -1;

        return input;
    }
}
