package com.skywilling.cn.scheduler.core.trajectoryalgorithm;

import com.skywilling.cn.livemap.service.StationService;
import com.skywilling.cn.scheduler.common.EulerAngle;
import com.skywilling.cn.scheduler.common.utils;
import com.skywilling.cn.scheduler.core.trajectoryalgorithm.config.GlobalTrajPlannerConfig;
import com.skywilling.cn.scheduler.model.*;
import com.skywilling.cn.scheduler.service.TrjPlanService;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.support.incrementer.SybaseAnywhereMaxValueIncrementer;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

@Data
class NearestPose{
    private int lane_id;
    private int point_index;
    private double distance;
    private Pose point;
    private Lane nearest_lane;

    public NearestPose(){
        point = new Pose();
    }
}


@Data
public class GlobalTrajPlanner implements TrjPlanService {
    private String trajFilePath;
    private double weight_data;
    private double weight_smooth;
    private double tolerance;
    private double start_length; // 短距离,用于决定起步直线距离
    private double end_length;
    private double lane_speed_limit;
    private double cross_speed_limit;
    private List<Lane> Lane_vec;
    private List<Cross> Cross_vec;
    private double thresh_start_length;  // thresh长距离,用于截取lane_front
    private double thresh_end_length;

    private void parseConfig(){
        GlobalTrajPlannerConfig.setTrajFilePath("path_file_Yunlecar");
        trajFilePath = GlobalTrajPlannerConfig.trajFilePath;
        weight_data = GlobalTrajPlannerConfig.weight_data;
        weight_smooth = GlobalTrajPlannerConfig.weight_smooth;
        tolerance = GlobalTrajPlannerConfig.tolerance;
        start_length = GlobalTrajPlannerConfig.start_length;
        end_length = GlobalTrajPlannerConfig.end_length;
        lane_speed_limit = GlobalTrajPlannerConfig.lane_speed_limit;
        cross_speed_limit = GlobalTrajPlannerConfig.cross_speed_limit;
        thresh_start_length = GlobalTrajPlannerConfig.thresh_start_length;
        thresh_end_length = GlobalTrajPlannerConfig.thresh_end_length;
    }

    private void constract_Lane_Cross_vec(){
        List<String> file_list = getAllFiles();
        readAllFiles(file_list);
    }

    private List<String> getAllFiles(){
        List<String> files = new ArrayList<>();
        File file = new File(trajFilePath);
        File[] tempList = file.listFiles();

        assert tempList != null;
        for (File value : tempList) {
            if (value.isFile()){
                files.add(value.toString());
            }
        }
        return files;
    }

    private void readAllFiles(List<String> files){
        for(String file: files){  // 因为下面把错误处理写在了函数上,throws
            try{
                readFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void readFile(String file) throws IOException {  // throws使得在以下都不用写try catch IO错误相关的处理//注意必须在上一级catch掉
        String[] splist = file.split("/");  // String[] 定长,不可变   List<String> 不定长
        String filename = splist[splist.length-1].split("\\.")[0];  // split["."]得不到结果,需要使用转义
        File csv = new File(file);
        csv.setReadable(true);
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(filename.startsWith("lane")){
            Integer id = Integer.parseInt(filename.split("_")[1]);
            Lane lane = new Lane();
            int lane_id = Integer.parseInt(br.readLine());
            lane.setId(lane_id);
            double length = Double.parseDouble(br.readLine());
            lane.setLength(length);
            Boolean reverse = Integer.parseInt(br.readLine()) == 1;
            lane.setReverse(reverse);
            List<Integer> pid = new ArrayList<>();
            List<Integer> nid = new ArrayList<>();
            String[] pre_id = br.readLine().split(",");
            for(String x: pre_id){
                pid.add(Integer.parseInt(x));
            }
            lane.setPre_id(pid);
            String[] next_id = br.readLine().split(",");
            for(String x:next_id){
                nid.add(Integer.parseInt(x));
            }
            lane.setNext_id(nid);
            String line = "";
            while((line = br.readLine())!=null){
                String[] cor = line.split(",");
                Pose points = new Pose();
                points.setPosition(cor[0],cor[1],cor[2]);
                points.setOrientation(cor[3],cor[4],cor[5],cor[6]);
                lane.addPoints(points);
            }
            Lane_vec.add(lane);
        }else if(filename.startsWith("cross")){
            Integer id = Integer.parseInt(filename.split("_")[1]);
            Cross cross = new Cross();
            int cross_id = Integer.parseInt(br.readLine());
            cross.setId(cross_id);
            double length = Double.parseDouble(br.readLine());
            cross.setLength(length);
            Boolean reverse = Integer.parseInt(br.readLine()) == 1;
            cross.setReverse(reverse);
            List<Integer> pid = new ArrayList<>();
            List<Integer> nid = new ArrayList<>();
            String[] pre_id = br.readLine().split(",");
            for(String x: pre_id){
                pid.add(Integer.parseInt(x));
            }
            cross.setPre_id(pid);
            String[] next_id = br.readLine().split(",");
            for(String x:next_id){
                nid.add(Integer.parseInt(x));
            }
            cross.setNext_id(nid);
            String line = "";
            while((line = br.readLine())!=null){
                String[] cor = line.split(",");
                Pose points = new Pose();
                points.setPosition(cor[0],cor[1],cor[2]);
                points.setOrientation(cor[3],cor[4],cor[5],cor[6]);
                cross.addPoints(points);
            }
            Cross_vec.add(cross);

            try{
                br.close();
                br = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Integer> get_line_nums(String line){return null;}

    private void startPose_cb(StaticStation station, List<Lane> temp_Lane_vec, List<Cross> temp_Cross_vec){
        NearestPose nearestPose = find_nearest_pose(station.getPoint().getPosition(), Lane_vec);
        if(!validate(nearestPose, temp_Cross_vec)){
            // TODO::错误处理::所选点在转弯处
            System.out.println("Start station is set at Cross, return.");
        }
        EulerAngle start_station_angle = utils.Quaternion2Euler(station.getPoint().getOrientation());
        EulerAngle nearest_lane_angle = utils.Quaternion2Euler(nearestPose.getPoint().getOrientation());
        Cross c_node = new Cross();
        Cross c_node_final = new Cross();
        Lane l_node = new Lane();
        Lane l_node_final = new Lane();
        Pose temp_start_pose = new Pose();
        temp_start_pose.setPosition(nearestPose.getPoint().getPosition());
        c_node.addPoints(temp_start_pose);
        c_node_final.addPoints(temp_start_pose);
        Lane t_lane = (Lane)nearestPose.getNearest_lane().clone();
        if(Math.abs(start_station_angle.getYaw() - nearest_lane_angle.getYaw()) < Math.PI / 2.0){
            // same orientation
            int traj_size = t_lane.getPointsNum();

            c_node_final.setId(temp_Cross_vec.size());
            c_node_final.setLength(0);
            c_node_final.setReverse(false);
            c_node_final.addPre_id(temp_Lane_vec.size());
            temp_Cross_vec.add(c_node_final);

            l_node_final.setId(temp_Lane_vec.size());
            l_node_final.setLength(utils.distance2points(t_lane.getFirstPoint().getPosition(), nearestPose.getPoint().getPosition()));
            l_node_final.setReverse(false);
            l_node_final.setPre_id(utils.depcopy(t_lane.getPre_id()));
            l_node_final.addNext_id(c_node_final.getId());

            for(int i=0;i< nearestPose.getPoint_index();i++){
                l_node_final.addPoints(t_lane.getPoint(i));
            }
            temp_Lane_vec.add(l_node_final);

            l_node.setId(temp_Lane_vec.size());
            l_node.setLength(utils.distance2points(t_lane.getLastPoint().getPosition(), nearestPose.getPoint().getPosition()));
            l_node.setReverse(false);
            l_node.addPre_id(temp_Cross_vec.size());
            l_node.setNext_id(utils.depcopy(t_lane.getNext_id()));
            for(int i=nearestPose.getPoint_index();i<t_lane.getPointsNum();i++){
                l_node.addPoints(t_lane.getPoint(i));
            }

            for(int cross_id: t_lane.getPre_id()){
//                System.out.println("cross "+cross_id+" originan pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" originan next: "+temp_Cross_vec.get(cross_id).getNext_id());
                for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getPre_id().iterator(); iter.hasNext(); ) {
                    Integer it = iter.next();
                    if(it == nearestPose.getLane_id()){
//                        System.out.println("delete "+it +" from cross "+cross_id +" preid");
                        iter.remove();
                        temp_Cross_vec.get(cross_id).addPre_id(l_node_final.getId());
                        break;
                    }
                }
                for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getNext_id().iterator(); iter.hasNext(); ) {
                    Integer it = iter.next();
                    if(it == nearestPose.getLane_id()){
//                        System.out.println("delete "+it +" from cross "+cross_id +" nextid");
                        iter.remove();
//                        System.out.println("add " + l_node_final.getId() + " to cross" + cross_id + " nextid");
                        temp_Cross_vec.get(cross_id).addNext_id(l_node_final.getId());
                        break;
                    }
                }
//                System.out.println("cross "+cross_id+" after pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" after next: "+temp_Cross_vec.get(cross_id).getNext_id());
            }
            for(int cross_id: t_lane.getNext_id()){
//                System.out.println("cross "+cross_id+" originan pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" originan next: "+temp_Cross_vec.get(cross_id).getNext_id());
                for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getPre_id().iterator(); iter.hasNext(); ) {
                    Integer it = iter.next();
                    if(it == nearestPose.getLane_id()){
                        iter.remove();
                        temp_Cross_vec.get(cross_id).addPre_id(l_node.getId());
                        break;
                    }
                }
                for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getNext_id().iterator(); iter.hasNext(); ) {
                    Integer it = iter.next();
                    if(it == nearestPose.getLane_id()){
                        iter.remove();
                        temp_Cross_vec.get(cross_id).addNext_id(l_node.getId());
                        break;
                    }
                }
//                System.out.println("cross "+cross_id+" after pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" after next: "+temp_Cross_vec.get(cross_id).getNext_id());

            }

            c_node.setId(temp_Cross_vec.size());
            c_node.setLength(0);
            c_node.setReverse(false);
            c_node.addNext_id(l_node.getId());

            temp_Cross_vec.add(c_node);
            temp_Lane_vec.add(l_node);
        }else{
            // different orientation
            int trj_size = nearestPose.getNearest_lane().getPointsNum();
            c_node_final.setId(temp_Cross_vec.size());
            c_node_final.setLength(0);
            c_node_final.setReverse(false);
            c_node_final.addPre_id(temp_Lane_vec.size());
            temp_Cross_vec.add(c_node_final);

            l_node_final.setId(temp_Lane_vec.size());
            l_node_final.setLength(utils.distance2points(t_lane.getLastPoint().getPosition(), nearestPose.getPoint().getPosition()));
            l_node_final.setReverse(false);
            l_node_final.setPre_id(utils.depcopy(t_lane.getNext_id()));
            l_node_final.addNext_id(c_node_final.getId());
            for(int i=trj_size-1;i>nearestPose.getPoint_index();i--){
                l_node_final.addPoints(t_lane.getPoint(i));
            }
            temp_Lane_vec.add(l_node_final);

            l_node.setId(temp_Lane_vec.size());
            l_node.setLength(utils.distance2points(t_lane.getFirstPoint().getPosition(), nearestPose.getPoint().getPosition()));
            l_node.setReverse(false);
            l_node.addPre_id(temp_Cross_vec.size());
            l_node.setNext_id(utils.depcopy(t_lane.getPre_id()));
            for(int i=nearestPose.getPoint_index();i>=0;i--){
                l_node.addPoints(t_lane.getPoint(i));
            }

            for(int cross_id: t_lane.getPre_id()){
//                System.out.println("cross "+cross_id+" originan pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" originan next: "+temp_Cross_vec.get(cross_id).getNext_id());
                for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getPre_id().iterator(); iter.hasNext(); ) {
                    Integer it = iter.next();
                    if(it == nearestPose.getLane_id()){
                        iter.remove();
                        temp_Cross_vec.get(cross_id).addPre_id(l_node.getId());
                        break;
                    }
                }
                for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getNext_id().iterator(); iter.hasNext(); ) {
                    Integer it = iter.next();
                    if(it == nearestPose.getLane_id()){
                        iter.remove();
                        temp_Cross_vec.get(cross_id).addNext_id(l_node.getId());
                        break;
                    }
                }
//                System.out.println("cross "+cross_id+" after pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" after next: "+temp_Cross_vec.get(cross_id).getNext_id());
            }
            for(int cross_id: t_lane.getNext_id()){
//                System.out.println("cross "+cross_id+" originan pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" originan next: "+temp_Cross_vec.get(cross_id).getNext_id());
                for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getPre_id().iterator(); iter.hasNext(); ) {
                    Integer it = iter.next();
                    if(it == nearestPose.getLane_id()){
                        iter.remove();
                        temp_Cross_vec.get(cross_id).addPre_id(l_node.getId());
                        break;
                    }
                }
                for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getNext_id().iterator(); iter.hasNext(); ) {
                    Integer it = iter.next();
                    if(it == nearestPose.getLane_id()){
                        iter.remove();
                        temp_Cross_vec.get(cross_id).addNext_id(l_node.getId());
                        break;
                    }
                }
//                System.out.println("cross "+cross_id+" after pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" after next: "+temp_Cross_vec.get(cross_id).getNext_id());

            }

            c_node.setId(temp_Cross_vec.size());
            c_node.setLength(0);
            c_node.setReverse(false);
            c_node.addNext_id(l_node.getId());

            temp_Cross_vec.add(c_node);
            temp_Lane_vec.add(l_node);

        }

        temp_Lane_vec.get(nearestPose.getLane_id()).getNext_id().clear();
        temp_Lane_vec.get(nearestPose.getLane_id()).getPre_id().clear();
        temp_Lane_vec.get(nearestPose.getLane_id()).getPoints().clear();
    }

    private void endPose_cb(StaticStation station, List<Lane> temp_Lane_vec, List<Cross> temp_Cross_vec){
        NearestPose nearestPose = find_nearest_pose(station.getPoint().getPosition(), Lane_vec);
        if(!validate(nearestPose, temp_Cross_vec)){
            // TODO::错误处理::所选点在转弯处
            System.out.println("Target station is set at Cross, return.");
        }

        Cross c_node = new Cross();
        Lane l_node1 = new Lane();
        Lane l_node2 = new Lane();
        Lane t_lane = (Lane)nearestPose.getNearest_lane().clone();

        l_node1.setId(temp_Lane_vec.size());
        l_node1.setLength(utils.distance2points(t_lane.getFirstPoint().getPosition(), nearestPose.getPoint().getPosition()));
        l_node1.setReverse(false);
        l_node1.setPre_id(utils.depcopy(t_lane.getPre_id()));
        l_node1.addNext_id(temp_Cross_vec.size());
        for(int i=0;i<nearestPose.getPoint_index();i++){
            l_node1.addPoints(t_lane.getPoint(i));
        }

        for(int cross_id: t_lane.getPre_id()){
//                System.out.println("cross "+cross_id+" originan pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" originan next: "+temp_Cross_vec.get(cross_id).getNext_id());
            for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getNext_id().iterator(); iter.hasNext(); ) {
                Integer it = iter.next();
                if(it == nearestPose.getLane_id()){
//                        System.out.println("delete "+it +" from cross "+cross_id +" preid");
                    iter.remove();
                    temp_Cross_vec.get(cross_id).addPre_id(l_node1.getId());
                    break;
                }
            }
            for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getPre_id().iterator(); iter.hasNext(); ) {
                Integer it = iter.next();
                if(it == nearestPose.getLane_id()){
//                        System.out.println("delete "+it +" from cross "+cross_id +" nextid");
                    iter.remove();
                    temp_Cross_vec.get(cross_id).addPre_id(l_node1.getId());
                    break;
                }
            }
//                System.out.println("cross "+cross_id+" after pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" after next: "+temp_Cross_vec.get(cross_id).getNext_id());
        }
        temp_Lane_vec.add(l_node1);

        int traj_size = t_lane.getPointsNum();
        l_node2.setId(temp_Lane_vec.size());
        l_node2.setLength(utils.distance2points(t_lane.getLastPoint().getPosition(), nearestPose.getPoint().getPosition()));
        l_node2.setReverse(false);
        l_node2.setPre_id(utils.depcopy(t_lane.getNext_id()));
        l_node2.addNext_id(temp_Cross_vec.size());
        for(int i=t_lane.getPointsNum()-1;i>=nearestPose.getPoint_index();i--){
            l_node2.addPoints(t_lane.getPoint(i));
        }
        for(int cross_id: t_lane.getNext_id()){
//                System.out.println("cross "+cross_id+" originan pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" originan next: "+temp_Cross_vec.get(cross_id).getNext_id());
            for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getPre_id().iterator(); iter.hasNext(); ) {
                Integer it = iter.next();
                if(it == nearestPose.getLane_id()){
                    iter.remove();
                    temp_Cross_vec.get(cross_id).addPre_id(l_node2.getId());
                    break;
                }
            }
            for (Iterator<Integer> iter = temp_Cross_vec.get(cross_id).getNext_id().iterator(); iter.hasNext(); ) {
                Integer it = iter.next();
                if(it == nearestPose.getLane_id()){
                    iter.remove();
                    temp_Cross_vec.get(cross_id).addPre_id(l_node2.getId());
                    break;
                }
            }
//                System.out.println("cross "+cross_id+" after pre: "+temp_Cross_vec.get(cross_id).getPre_id());
//                System.out.println("cross "+cross_id+" after next: "+temp_Cross_vec.get(cross_id).getNext_id());

        }
        temp_Lane_vec.add(l_node2);

        c_node.setId(temp_Cross_vec.size());
        c_node.setLength(0);
        c_node.setReverse(false);
        c_node.addPre_id(l_node1.getId());
        c_node.addPre_id(l_node2.getId());
        temp_Cross_vec.add(c_node);

        temp_Cross_vec.get(nearestPose.getLane_id()).getPre_id().clear();
        temp_Cross_vec.get(nearestPose.getLane_id()).getNext_id().clear();
        temp_Cross_vec.get(nearestPose.getLane_id()).getPoints().clear();
    }

    private NearestPose find_nearest_pose(Position p, List<Lane> lane_list){
        NearestPose nearestPose = new NearestPose();
        double min_distance = Double.MAX_VALUE;
        for(Lane lane : lane_list){
            int cnt = 0;
            for(Pose point: lane.getPoints()){
                double d = utils.distance2points(p, point.getPosition());
                if(d < min_distance){
                    min_distance = d;
                    nearestPose.setLane_id(lane.getId());
                    nearestPose.setPoint_index(cnt);
                    nearestPose.setDistance(min_distance);
                    nearestPose.setPoint(point);
                    nearestPose.setNearest_lane(lane);
                }
                cnt++;
            }
        }
        return nearestPose;
    }

    private List<Integer> Dij(StaticStation start, StaticStation target, List<Lane> temp_Lane_vec, List<Cross> temp_Cross_vec){
        List<Integer> result = new ArrayList<>();
        int c_size = temp_Cross_vec.size();
        int l_size = temp_Lane_vec.size();
        Integer[][] graph = new Integer[c_size][c_size];
        for(int i=0;i<c_size;i++){
            Arrays.fill(graph[i], -1);
        }

        for(int i = 0;i<l_size;i++){
            for(Integer a: temp_Lane_vec.get(i).getPre_id()){
                for(Integer b: temp_Lane_vec.get(i).getNext_id()){
                    graph[a][b] = temp_Lane_vec.get(i).getId();
                    if(temp_Lane_vec.get(i).getReverse()){
                        graph[b][a] = graph[a][b];
                    }
//                    System.out.println(a+" -> " + b + " : "+temp_Lane_vec.get(i).getId());
                }
            }
        }

//        for(Lane lane: temp_Lane_vec){
//            System.out.println("lane_id: "+lane.getId()+" pre: "+lane.getPre_id()+" next: "+lane.getNext_id());
//        }
//        for(Cross lane: temp_Cross_vec){
//            System.out.println("cross_id: "+lane.getId()+" pre: "+lane.getPre_id()+" next: "+lane.getNext_id());
//        }

//        // debug/print graph
//        System.out.println("------------------------------");
//        System.out.println(">> This is the search graph");
//        System.out.format("%02d\n", c_size);
//        for(int i=0;i<c_size;i++){
//            System.out.format("%2d", i);
//        }
//        System.out.format("\n");
//        for(int i=0;i<c_size;i++){
//            System.out.format("%2d",i);
//            for(int j=0;j<c_size;j++){
//                if(graph[i][j] == -1) System.out.format("  ");
//                else System.out.format("%2d", graph[i][j]);
//            }
//            System.out.format("\n");
//        }
//        System.out.println("------------------------------");
        int s = c_size - 2;
        int t = c_size - 1;
        Double[] dist = new Double[c_size];
        Arrays.fill(dist,Double.MAX_VALUE);
        dist[s] = 0.0;
        Boolean[] visit = new Boolean[c_size];
        Arrays.fill(visit,false);
        Integer[] pre = new Integer[c_size];
        for(int i=0;i<c_size;i++)  pre[i] = i;
        for(int i=0;i<c_size;i++){
            int u = -1;
            double MIN = Double.MAX_VALUE;
            for(int j=0;j<c_size;j++){
                if(!visit[j] && dist[j]<MIN){
                    u = j;
                    MIN = dist[j];
                }
            }
            if(u == -1 || u == t) break;
            visit[u] = true;
            for(int v=0;v < c_size; v++){
                if(!visit[v] && graph[u][v] != -1){
                    double length = temp_Lane_vec.get(graph[u][v]).getLength();
                    if(dist[u]+length < dist[v]){
                        dist[v] = dist[u] + length;
                        pre[v] = u;
                    }
                }
            }
        }
        int n = t;
        List<Integer> cross_list = new ArrayList<>();
        while(pre[n] != n){
            cross_list.add(0,n);
            n = pre[n];
        }
        cross_list.add(0,s);
        // TODO:: Debug:: 输出cross序列
        for(int i=0;i<cross_list.size();i++){
            int u = cross_list.get(i);
            result.add(u);
            if(i<cross_list.size()-1){
                int v = cross_list.get(i+1);
                result.add(graph[u][v]);
            }
        }
        // TODO:: Debug:: 输出cross/Lane路径序列

        return result;
    }

    private List<RoutePoint> convert_result_to_path(List<Integer> result, List<Lane> temp_Lane_vec, List<Cross> temp_Cross_vec){
        List<RoutePoint> result_path = new ArrayList<>();
        int size = result.size();
        RoutePoint waypoint_start = new RoutePoint();
        waypoint_start.setPosition(temp_Cross_vec.get(result.get(0)).getFirstPoint().getPosition());
        waypoint_start.setIs_lane(0);// 起点是个cornor,最终需要在smooth中修改起点弧段
        waypoint_start.setCross_id(result.get(0));
        waypoint_start.setSpeed_limit(lane_speed_limit);
        result_path.add(waypoint_start);
        double speed_limit;
        int waypoint_type;

        for(int i=1;i<size-1;i++){
            RoutePoint pre = result_path.get(result_path.size() - 1);
            int id = result.get(i);
            List<Pose> next;
            if(i % 2 == 1){
                //lane
                next = temp_Lane_vec.get(id).getPoints();
                speed_limit = lane_speed_limit;
                waypoint_type = 1;
            }else{
                //cross
                next = temp_Cross_vec.get(id).getPoints();
                speed_limit = cross_speed_limit;
                waypoint_type = 0;
            }
            Pose p_start = next.get(0);
            Pose p_last = next.get(next.size() - 1);
            if(utils.distance2points(pre.getPosition(),p_start.getPosition()) > utils.distance2points(pre.getPosition(), p_last.getPosition())){
                // reverse path
                int n_length = next.size();
                for(int j=0;i<n_length/2;i++){
                    Pose temp = next.get(j);
                    next.set(i, next.get(n_length-1-i));
                    next.set(n_length-i-1,temp);
                }
            }
            for(Pose point: next){
                RoutePoint in_waypoint = new RoutePoint();
                in_waypoint.setIs_lane(waypoint_type);
                in_waypoint.setSpeed_limit(speed_limit);
                in_waypoint.setPosition(point.getPosition());
                in_waypoint.setOrientation(point.getOrientation());
                result_path.add(in_waypoint);
            }
        }

        return result_path;
    }

    private List<RoutePoint> smooth_path(List<RoutePoint> path, StaticStation start, StaticStation target){
        List<RoutePoint> path_in = utils.depcopy(path);
        int size = path_in.size();
        if(size <= 10){
            // TODO:: path太短,无法进行平滑操作
            return path_in;
        }
        double sum_length_front = 0,sum_length_end = 0;

        // 起步阶段处理
        int start_cut_index = 0;
        List<RoutePoint> in_front = new ArrayList<>();
        for(start_cut_index = 0;start_cut_index < path_in.size() - 2;start_cut_index++){
            sum_length_front += utils.distance2points(path_in.get(start_cut_index).getPosition(), path_in.get(start_cut_index+1).getPosition());
            if(sum_length_front > thresh_start_length) break;
        }
        if(sum_length_front > thresh_start_length){
            List<RoutePoint> lane_front = new ArrayList<>();
            for(int i = 0;i<start_cut_index;i++){
                lane_front.add(path_in.get(0));
                path_in.remove(0);
            }
            double diff_x = start.getPoint().getPosition().getX() - lane_front.get(0).getPosition().getX();
            double diff_y = start.getPoint().getPosition().getY() - lane_front.get(0).getPosition().getY();
            double sum_front = 0;
            int front_index = 0;
            for(int i=0;i<lane_front.size()-2;i++){
                sum_front += utils.distance2points(lane_front.get(i).getPosition(), lane_front.get(i+1).getPosition());
                if(sum_front >= start_length){
                    front_index = i;
                    break;
                }
            }
            for(int i=0;i<=front_index;i++){ // 起步直线处理
                RoutePoint temp = new RoutePoint();
                temp.setIs_lane(0);
                temp.setSpeed_limit(0.5);
                temp.setPosition(new Position(lane_front.get(i).getPosition().getX()+diff_x,lane_front.get(i).getPosition().getY()+diff_y,0));
                in_front.add(temp);
            }

            RoutePoint s = in_front.get(front_index);
            double lenx = path_in.get(0).getPosition().getX() - s.getPosition().getX();
            double leny = path_in.get(0).getPosition().getY() - s.getPosition().getY();
            int cnt_front = (int)(utils.distance2points(s.getPosition(),path_in.get(0).getPosition())/0.5);
            double dx = lenx / cnt_front;
            double dy = leny / cnt_front;
            for(int i=0;i<cnt_front;i++){ // 起步弯道处理
                RoutePoint temp = new RoutePoint();
                temp.setIs_lane(0);
                temp.setSpeed_limit(0.5);
                temp.setPosition(new Position(s.getPosition().getX()+i*dx,s.getPosition().getY()+i*dy,0));
                in_front.add(temp);
            }
            path_in.addAll(0,in_front);
        }

        // 结束部分处理
        int end_cut_index = path_in.size() - 1;
        List<RoutePoint> in_end = new ArrayList<>();
        for(end_cut_index = path_in.size() - 1;end_cut_index>1;end_cut_index--){
            sum_length_end += utils.distance2points(path_in.get(end_cut_index-1).getPosition(), path_in.get(end_cut_index).getPosition());
            if(sum_length_end >= thresh_end_length) break;
        }
        if(sum_length_end >= thresh_end_length){
            List<RoutePoint> lane_end = new ArrayList<>();
            int now_length = path_in.size() - end_cut_index;
            for(int i = 0;i<now_length;i++){
                int last = path_in.size() - 1;
                lane_end.add(path_in.get(last));
                path_in.remove(last);
            }
            Collections.reverse(lane_end);
            double diff_x = target.getPoint().getPosition().getX() - lane_end.get(lane_end.size() - 1).getPosition().getX();
            double diff_y = target.getPoint().getPosition().getY() - lane_end.get(lane_end.size() - 1).getPosition().getY();
            double sum_end = 0;
            int end_index = 0;
            for(int i = lane_end.size() - 1;i>0;i--){
                sum_end += utils.distance2points(lane_end.get(i).getPosition(), lane_end.get(i-1).getPosition());
                if(sum_end >= end_length){
                    end_index = i;
                    break;
                }
            }
            for(int i=lane_end.size() - 1;i>end_index;i--){
                RoutePoint temp = new RoutePoint();
                temp.setIs_lane(0);
                temp.setSpeed_limit(0.5);
                temp.setPosition(new Position(lane_end.get(i).getPosition().getX()+diff_x, lane_end.get(i).getPosition().getY()+diff_y,0));
                in_end.add(temp);
            }

            RoutePoint e = in_end.get(in_end.size() - 1);
            double lenx = e.getPosition().getX() - path_in.get(path_in.size() - 1).getPosition().getX();
            double leny = e.getPosition().getY() - path_in.get(path_in.size() - 1).getPosition().getY();
            int cnt_end = (int)(utils.distance2points(e.getPosition(), path_in.get(path_in.size()-1).getPosition())/0.5);
            double dx = lenx / cnt_end;
            double dy = leny / cnt_end;
            for(int i=cnt_end;i>0;i--){
                RoutePoint temp = new RoutePoint();
                temp.setIs_lane(0);
                temp.setSpeed_limit(0.5);
                temp.setPosition(new Position(e.getPosition().getX() - (cnt_end-i)*dx,e.getPosition().getY()-(cnt_end-i)*dy,0));
                in_end.add(temp);
            }
            Collections.reverse(in_end);
            path_in.addAll(path_in.size(), in_end);
        }


        // 平滑轨迹点
        size = path_in.size();
        if(size > 30){
            smooth(path_in, 0, in_front.size(),2);
            smooth(path_in, size - in_end.size()-5, in_end.size()+5,2);
        }else{
            smooth(path_in, 0,size,2);
        }

        // 调整每个轨迹点的角度
        for(int i = 0;i<path_in.size()-2;i++){
            RoutePoint p_c = path_in.get(i);
            RoutePoint p_n = path_in.get(i+1);
            double yaw = Math.atan2(p_n.getPosition().getY() - p_c.getPosition().getY(), p_n.getPosition().getX() - p_c.getPosition().getX());
            path_in.get(i).setOrientation(utils.Euler2Quaternion(new EulerAngle(0,0,yaw)));
        }
        return path_in;
    }

    private void smooth(List<RoutePoint> path, int start_index, int length, int step){
        double change = tolerance;
        int Iterations = 0;
        List<RoutePoint> path_in = utils.depcopy(path);
        while(change >= tolerance){
            change = 0.0;
            for(int i = start_index+step;i<start_index+length-1-step;i++){
                double xtemp = path.get(i).getPosition().getX();
                double ytemp = path.get(i).getPosition().getY();

                double pwx = path.get(i).getPosition().getX() + weight_data*(path_in.get(i).getPosition().getX() - path.get(i).getPosition().getX());
                double pwy = path.get(i).getPosition().getY() + weight_data*(path_in.get(i).getPosition().getY() - path.get(i).getPosition().getY());
                path.get(i).setPosition(new Position(pwx,pwy,0));

                double pmx = path.get(i).getPosition().getX() + weight_smooth*(path.get(i-1).getPosition().getX()+path.get(i+1).getPosition().getX() - 2.0*path.get(i).getPosition().getX());
                double pmy = path.get(i).getPosition().getY() + weight_smooth*(path.get(i-1).getPosition().getY()+path.get(i+1).getPosition().getY() - 2.0*path.get(i).getPosition().getY());
                path.get(i).setPosition(new Position(pmx,pmy,0));

                change += Math.abs(xtemp - path.get(i).getPosition().getX());
                change += Math.abs(ytemp - path.get(i).getPosition().getY());
            }
            Iterations++;
        }
    }

    private boolean validate(NearestPose nearestPose, List<Cross> cross_list){
        double dist_to_cornor = Double.MAX_VALUE;
        for(Cross cross: cross_list){
            for(Pose point: cross.getPoints()){
                Position position = point.getPosition();
                double dist = utils.distance2points(nearestPose.getPoint().getPosition(), position);
                if(dist < dist_to_cornor){
                    dist_to_cornor = dist;
                }
            }
        }
        return !(dist_to_cornor < nearestPose.getDistance());
    }

    public GlobalTrajPlanner(){
        Lane_vec = new ArrayList<>();
        Cross_vec = new ArrayList<>();
        parseConfig();
        constract_Lane_Cross_vec();
        Collections.sort(Lane_vec);
        Collections.sort(Cross_vec);
    }

    @Override
    public List<RoutePoint> createTrajectory(StaticStation start, StaticStation target) {
        if(utils.distance2points(start.getPoint().getPosition(), target.getPoint().getPosition())< 20){
            // TODO::起止点太近返回
            return null;
        }
        List<Lane> temp_Lane_vec = utils.depcopy(Lane_vec);
        List<Cross> temp_Cross_vec = utils.depcopy(Cross_vec);

        startPose_cb(start, temp_Lane_vec, temp_Cross_vec);
        endPose_cb(target, temp_Lane_vec, temp_Cross_vec);

        List<Integer> result = Dij(start,target,temp_Lane_vec,temp_Cross_vec);

        List<RoutePoint> result_path = convert_result_to_path(result,temp_Lane_vec,temp_Cross_vec);

        return smooth_path(result_path, start, target);
    }
}
