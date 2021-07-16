package com.skywilling.cn.connection.infrastructure.server.codec;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.connection.model.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class JSONDecoder extends ChannelInboundHandlerAdapter{

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof String) {
      //TODO: 这里输出了msg的内容
//      System.out.println(msg);
      Packet packet = JSONObject.parseObject((String) msg, Packet.class);
      ctx.fireChannelRead(packet);
    }
  }
}
