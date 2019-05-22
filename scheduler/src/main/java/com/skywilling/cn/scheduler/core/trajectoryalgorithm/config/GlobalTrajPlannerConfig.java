package com.skywilling.cn.scheduler.core.trajectoryalgorithm.config;

public class GlobalTrajPlannerConfig {
    public static String trajFilePath = "D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\lanes\\";
    public static double weight_data = 0.47;
    public static double weight_smooth = 0.14;
    public static double tolerance = 0.17;
    public static double start_length = 5.0;
    public static double end_length = 5.0;
    public static double lane_speed_limit = 3.0;
    public static double cross_speed_limit = 1.5;
    public static double thresh_start_length = 8.0;
    public static double thresh_end_length = 8.0;

    public static void setTrajFilePath(String parkName){
        trajFilePath = trajFilePath + parkName + '/';
    }
}
