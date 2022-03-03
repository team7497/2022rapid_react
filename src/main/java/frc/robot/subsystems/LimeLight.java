package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class LimeLight extends SubsystemBase{
    private static double tv, tx, ty, ta, ts, tl, tshort, tlong, thor, tvert;
    private static double[] camtran, tc;
    private static double distance;
    private static boolean isEnable;

    public static enum InformationType {
        vaild_target("tv"),
        horizontal_angle("tx"),
        vertical_angle("ty"),
        area("ta"),
        skew_angle("ts"),
        latency_time("tl"),
        short_length("tshort"),
        long_length("tlong"),
        rough_horizontal_length("thor"),
        rough_vertical_length("tvert"),
        transform_solution("camtran"),
        average_HSVcolor("tc");

        public final String string;
        
        InformationType(String string) { this.string = string; }
    }

    public static enum LightMode {
        Current(0),
        Off(1),
        Blink(2),
        On(3);

        public final int value;
        
        LightMode(int value) { this.value = value; }
    }

    /**
     * Enable Limelight subsystem to receive data from camera.
     */
    public void Enable() {
        isEnable = true;
    }

    /**
     * Disable Limelight subsystem to receive date from camera
     */
    public void Disable() {
        isEnable = false;
    }

    public void setLightMode(LightMode mode) {
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(mode.value);
    }

    @Override
    public void periodic()
    {
        tv      = getDouble(InformationType.vaild_target);
        tx      = getDouble(InformationType.horizontal_angle);
        ty      = getDouble(InformationType.vertical_angle);
        ta      = getDouble(InformationType.area);
        ts      = getDouble(InformationType.skew_angle);
        tl      = getDouble(InformationType.latency_time);
        tshort  = getDouble(InformationType.short_length);
        tlong   = getDouble(InformationType.long_length);
        thor    = getDouble(InformationType.rough_horizontal_length);
        tvert   = getDouble(InformationType.rough_vertical_length);
        
        camtran = getDoubleArray(InformationType.transform_solution);
        tc      = getDoubleArray(InformationType.average_HSVcolor);
        
        SmartDashboard.putNumber("LimelightTx", tx);
    }

    public static double getDistance()
    {
        distance = (Constants.target_height - Constants.camera_height) / Math.tan(Constants.camera_angle + ty);
        return distance;
    }

    /** 
     * get double value
     * 
     * accepted type:
     * tv, tx, ty, ta, ts, tl,
     * tshort, tlong, thor, tvert
     */
    private static double getDouble(InformationType type)
    {
        return NetworkTableInstance.getDefault().getTable("limelight").getEntry(type.string).getDouble(0);
    }

    /**
     * get doulbe array
     * 
     * accepted type:
     * camtran(6 elements)
     * tc(3 elements)
     */
    private static double[] getDoubleArray(InformationType type)
    {
        double[] a = {};
        return NetworkTableInstance.getDefault().getTable("limelight").getEntry(type.string).getDoubleArray(a);
    }

    public static double getTa() {
        return ta;
    }
    public static double getTx() {
        return tx;
    }
    public static double[] getTc() {
        return tc;
    }
    public static double getThor() {
        return thor;
    }
    public static double[] getCamtran() {
        return camtran;
    }
    public static double getTl() {
        return tl;
    }
    public static double getTlong() {
        return tlong;
    }
    public static double getTs() {
        return ts;
    }
    public static double getTshort() {
        return tshort;
    }
    public static double getTv() {
        return tv;
    }
    public static double getTvert() {
        return tvert;
    }
    public static double getTy() {
        return ty;
    }
}
