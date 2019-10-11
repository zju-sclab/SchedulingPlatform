package com.skywilling.cn.scheduler.core.trajectoryalgorithm.config;

import org.springframework.stereotype.Service;

@Service
public class GlobalTrajPlannerConfig {
    //public static String linux_traj_file_path = "/home/sin/catkin_ws/rc_car/src/smartcar/planning/global_planning/";
    //public static String windows_traj_file_path = "D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\lanes\\";
    //方炜豪的笔记本对应的csv路径
    //    public static String linux_traj_file_path = "/home/po/Desktop/SchedulingPlatform/scheduler/src/main/resources/lanes/";
    //    public static String windows_traj_file_path = "D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\lanes\\";
    //path for simulation
    public static String linux_traj_file_path = "/home/simulation/Desktop/SchedulingPlatform-netty/scheduler/src/main/resources/lanes/";
    public static String windows_traj_file_path = "D:\\work\\Projects\\linxxx\\SchedulingPlatform\\doc\\Map\\lanes\\";

    public static String trajFilePath = "";
    static{
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith("win")){
           trajFilePath = windows_traj_file_path;
        }else{
            trajFilePath = linux_traj_file_path;
        }
    }
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
        trajFilePath = trajFilePath + parkName + System.getProperty("file.separator");
    }
}
