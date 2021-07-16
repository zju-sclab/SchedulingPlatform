package com.skywilling.cn.connection.service;

import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.connection.infrastructure.client.ClientPromise;
import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.connection.model.ACK;
import com.skywilling.cn.connection.model.Packet;
import com.skywilling.cn.connection.model.ProtocolField;
import com.skywilling.cn.monitor.listener.BasicListener;
import com.skywilling.cn.monitor.listener.ListenerMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 请求分排器,通道中主要包含三种数据，一是请求，二是回复，三是链接断开
 */
@Component
public class RequestDispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(RequestDispatcher.class);

    @Value("${netty.dispatch.threads}")
    private int threadNum;
    private ExecutorService executorService;

    @Autowired
    ClientService clientService;
    @Autowired
    ClientPromise clientPromise;
    @Autowired
    ListenerMap listenerMap;
    @Autowired
    RequestSender requestSender;

    public RequestDispatcher() {
        //分配固定大小度的线程池多线程地处理请求的分派
        executorService = Executors.newFixedThreadPool(30);
    }

    /**
     * 读取packet数据，用于测试车端云端通信
     */
    public void readPacket(Packet packet){
        int type = packet.getType();
        String str = packet.toString();
        // GPS 过于频繁暂时不输出
//        System.out.println(type);
        if(type != TypeField.GPS_INFO.getType()){
            System.out.println("get message: " + str);
        }
    }

    /**
     * 分发，需要分发的请求有请求和回复
     */
    public void dispatch(final ChannelHandlerContext ctx, Packet packet) {

        executorService.submit(() -> {
            BasicCarResponse carResponse;
            readPacket(packet);
            if (ACK.COMMAND.getCode() == packet.getAck()) {
//                LOG.info("carRequest: " + " type :" + packet.getType() + " vin " + packet.getVin());
                //这里进行处理命令模式处理
                carResponse = commandHandler(ctx, packet); //请求包

            } else {
                // 这里对应的是车端返回的数值
                carResponse = responseHandler(packet); //回复包
            }
            if(carResponse != null){
                if (packet.getType() == TypeField.REQUEST_LOCK.getType()) {
                    LOG.info("car request Response : " + carResponse.getCode() +" "+ carResponse.getAttach());
                }
                else if(packet.getType() == TypeField.RELEASE_LOCK.getType()){
                    LOG.info("car release Response : " + carResponse.getCode() +" "+ carResponse.getAttach());
                }
                else{
                    // 这里是返回我们的值
                    Packet.Builder builder = new Packet.Builder();
                    ctx.writeAndFlush(builder.buildResponse(packet, carResponse).build());
                }

            }

        });
    }

    public BasicCarResponse commandHandler(final ChannelHandlerContext ctx, Packet packet) {
        TypeField typeField = TypeField.valueOf(packet.getType());
        if (TypeField.LOGIN == typeField) {
            if(packet.getVin().length()==17&&packet.getType() == 1){
                //login of vehicle you shold put the carclient in the map
                clientService.open(packet.getVin(),ctx.channel());
                LOG.warn("increase length of car pool");
            }
            return loginHandler(ctx, packet);
        }
        else if(TypeField.REQUEST_LOCK == typeField){
            LOG.warn("receive request lock info : " + packet.getVin());
            String name = typeField.getDesc();
            BasicListener listener = listenerMap.getListener(name);
            return listener.process(packet.getVin(),packet.getData());
        }
        else if(TypeField.RELEASE_LOCK == typeField){
            LOG.warn("receive release lock info : " + packet.getVin());
            String name = typeField.getDesc();
            BasicListener listener = listenerMap.getListener(name);
            return listener.process(packet.getVin(),packet.getData());
        }
        else if(TypeField.TELE_CONTROL == typeField || TypeField.GPS_INFO == typeField){
            //TODO:这里需要重写 写的太杂了 getRemote是通过ip和port来获取新的链接的 这里本来是通过网页来选择对应的车辆的
            //这里添加了一层操作 我觉得没有问题呀！
//             LOG.warn(packet.getData());
            if(clientService.getRemote()!=null){
                clientService.sendCommand(clientService.getRemote(),packet);
            } else{
//                LOG.warn("null warning ");
            }
            String name = typeField.getDesc();
            BasicListener listener = listenerMap.getListener(name);
            return listener.process(packet.getVin(),packet.getData());
        }
        /**Type Ox15 为车端发送过来的终端消息,包括位置和方向,速度和转角等等 */
        //这里是如果是心跳包等等情况下
        else if (typeField != null) {
            /**logout, heartbeat ,registration,terminalInfo..*/
            return listenerMap.getListener(typeField.getDesc()).process(packet.getVin(), packet.getData());
        }
        return null;
    }

    /**车端回复的消息,用于调度指令的回复和路径规划任务下发的回复,type分为prepared_auto和fire_auto,tele_atuo,pause_auto等等*/
    private BasicCarResponse loginHandler(final ChannelHandlerContext ctx, Packet packet) {
        String vin = packet.getVin();
        //requestSender.sendRequest(vin, TypeField.LOGIN,  new JSONObject());
        //TODO: 出问题了
        BasicCarResponse process = listenerMap.getListener(TypeField.LOGIN.getDesc()).process(vin, packet.getData());
        if (ACK.SUCCESS.getCode() == process.getCode()) {
            /**保存vin到ctx上下文并打开一个vin标识的TCP通道注册到ctx*/
            AttributeKey<String> key = AttributeKey.valueOf(ProtocolField.VIN.getField());
            Attribute<String> vinAttr = ctx.channel().attr(key);
            vinAttr.setIfAbsent(vin);
            //回复一个包
            //clientService.open(vin, ctx.channel());
        }
        return process;
    }
   /**车端回复的消息,用于调度指令的回复和路径规划任务下发的回复,type分为prepared_auto和fire_auto,tele_atuo,pause_auto等等*/
    public BasicCarResponse responseHandler(Packet packet) {
        boolean result = ACK.getResult(packet.getAck());
        TypeField typeField = TypeField.valueOf(packet.getType());
        if (typeField != null) {
            clientPromise.receivedResponse(packet);
            BasicCarResponse process = listenerMap.getListener(typeField.getDesc()).process(packet.getVin(), result, packet.getData());
            return process;
        }
        return null;
    }

    /**检测是否断线,断线的话关闭通道并且调用断线的handler通知客户端重连*/
    public void onLostConnection(final ChannelHandlerContext ctx) {
        AttributeKey<String> key = AttributeKey.valueOf(ProtocolField.VIN.getField());
        Attribute<String> vin = ctx.channel().attr(key);
        if (StringUtils.isNotEmpty(vin.get())) {
            clientService.close(vin.get());
            listenerMap.getListener(ACK.FAILURE.getDesc()).process(vin.get(), null);
        }
        ctx.close();
    }

}
