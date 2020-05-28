package com.skywilling.cn.scheduler.core.trajectoryalgorithm.config;

import org.springframework.stereotype.Service;

@Service
public class GlobalTrajPlannerConfig {
    public static String linux_traj_file_path = "/doc/Map/lanes/";
    public static String windows_traj_file_path = "\\doc\\Map\\lanes\\";
    public static String trajFilePath = "";
    static{
        String root_path = System.getProperty("user.dir");
        if(root_path == "/"){
            root_path = "";
        }
        linux_traj_file_path = root_path+linux_traj_file_path;
        windows_traj_file_path = root_path+windows_traj_file_path;
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
        System.out.println("----------------------------------");
        System.out.println(trajFilePath);
        System.out.println("---------------------------------");
    }
}
