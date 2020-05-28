package com.skywilling.cn.connection.infrastructure.server.codec;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class JSONEncoder extends ChannelOutboundHandlerAdapter {
  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    if (msg instanceof JSONObject) {
      String json = ((JSONObject) msg).toJSONString();
      ctx.writeAndFlush(json + "\r\n");
    } else if (msg instanceof String) {
      ctx.writeAndFlush(msg + "\r\n");
    } else {
      ctx.writeAndFlush(JSONObject.toJSONString(msg) + "\r\n");
    }
  }
}
