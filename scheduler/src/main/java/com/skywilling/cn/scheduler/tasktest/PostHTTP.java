package com.skywilling.cn.scheduler.tasktest;

import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.manager.car.enumeration.CarState;
import com.skywilling.cn.manager.car.mapper.CarDynamicMapper;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author han yao
 * @date 2021/6/16 0:13
 */
@Component
public class PostHTTP implements Runnable{
    private static final String POST_URL = "http://127.0.0.1:8080/api/v2/auto/car/trj/station?vin=";
    private String postData = "";
    private List<String> allVin = new ArrayList<>();
    private List<String> allStation = new ArrayList<>();

//    @Autowired
//    private CarDynamicService carDynamicService;

    @Autowired
    private CarDynamicMapper carDynamicMapper;

    @Autowired
    private MapService mapService;

    @Autowired
    CarInfoService carInfoService;

    public PostHTTP(){}

    public void getAllVinAndStation(){
        List<CarDynamic> simulationCar = carDynamicMapper.querySimulationCar();
        List<CarDynamic> realCar = carDynamicMapper.queryRealCar();
        // 从数据库拿到所有车辆vin，再通过 Redis 判断是否已连接，allVin 只存放已连接的车辆
        allVin.clear();
        allStation.clear();
        for (CarDynamic carCur : simulationCar) {
            String vinCur = carCur.getVin();
            AutonomousCarInfo car = carInfoService.getAutoCarInfo(vinCur);
            if(car != null) {
                allVin.add(vinCur);
            }
        }
        for (CarDynamic carCur : realCar) {
            String vinCur = carCur.getVin();
            AutonomousCarInfo car = carInfoService.getAutoCarInfo(vinCur);
            if(car != null) {
                allVin.add(vinCur);
            }
        }
        for(String vin: allVin){
            System.out.println("all connected car Vin: "+ vin);
        }

        List<LiveMap> liveMaps = mapService.getAllMaps();
        // 假定只有一张地图
        LiveMap liveMap = liveMaps.get(0);
        ConcurrentHashMap<String, Node> nodeMap = liveMap.getNodeMap();
        for(Map.Entry<String, Node> node:nodeMap.entrySet()){
            allStation.add(node.getValue().getName());
        }
    }

    public void randomOrder(){
//        Timer timer = new Timer();
        Random random = new Random();
        while(true) {
            // 获取当前空闲车辆
            List<String> freeCarList = new ArrayList<>();
            for (String vin:allVin) {
                AutonomousCarInfo car = carInfoService.getAutoCarInfo(vin);
                if(car != null && car.getState() == CarState.FREE.getState()){
                    freeCarList.add(vin);
                }
            }
            // 随机一辆空闲车辆、随机一个站点，并发送任务
            if (freeCarList.size() > 0 && allStation.size() > 0) {
                String vin = freeCarList.get(Math.abs(random.nextInt()) % freeCarList.size());
                String station = allStation.get(Math.abs(random.nextInt()) % allStation.size());
                SendPostURL(vin, station);
            }
            // 每 1s 左右才发送一次任务
            int delayTime = random.nextInt() % 1000 + 500;
            try {
                Thread.sleep(delayTime);
            }catch (Exception e){
            }
        }
    }

    public String SendPostURL(String vin, String goal) {
        try {
            // 发送POST请求
            String postUrl = POST_URL;
            postUrl += vin;
            postUrl += "&goal=";
//            postUrl += goal;
            String goalUTF = URLEncoder.encode(goal, "UTF-8");
            postUrl += goalUTF;
//            String postUrl = "http://127.0.0.1:8080/api/v2/auto/car/trj/start?vin=00000000112417002&goal=%E5%8C%96%E5%B7%A5%E6%A5%BC";
            System.out.println(postUrl);
            URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");//修改发送方式0
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Length", "" + postData.length());
            OutputStreamWriter out = new OutputStreamWriter(
                    conn.getOutputStream(), "UTF-8");
            out.write(postData);
            out.flush();
            out.close();

            // 获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "";
            }
            // 获取响应内容体
            String line, result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
            return result;
        } catch (IOException e) {
        }
        return "";
    }

    @Override
    public void run(){
        getAllVinAndStation();
        randomOrder();
    }
}
