package com.skywilling.cn.connection.infrastructure.server;

import com.skywilling.cn.connection.infrastructure.server.codec.JSONDecoder;
import com.skywilling.cn.connection.infrastructure.server.codec.JSONEncoder;
import com.skywilling.cn.connection.infrastructure.server.handler.ServerChannelHandlerAdapter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;

public class NettyServer {

  private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);
  @Value("${netty.port}")
  private int port;
  private ChannelFuture future;
  private Map<ChannelOption, Object> optionMap;
  private ChannelInitializer<SocketChannel> initializer;
  private Map<ChannelOption, Object> childOptionMap;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  @Resource
  private ServerChannelHandlerAdapter channelHandlerAdapter;

  public NettyServer(int port, int workerNum) {
    this.port = port;
    this.initializer = new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("decoder", new LineBasedFrameDecoder(Integer.MAX_VALUE));
        pipeline.addLast("stringDecoder", new StringDecoder());
        pipeline.addLast("stringEncoder", new StringEncoder());

        pipeline.addLast("jsonDecoder", new JSONDecoder());
        pipeline.addLast("jsonEncoder", new JSONEncoder());

        pipeline.addLast("serverHanler", channelHandlerAdapter);
      }
    };

    bossGroup = new NioEventLoopGroup(1);
    workerGroup = new NioEventLoopGroup(workerNum);
  }

  public void start() {
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(bossGroup, workerGroup);
      bootstrap.channel(NioServerSocketChannel.class);
      bootstrap.childHandler(initializer);

      if (optionMap != null) {
        Iterator<Map.Entry<ChannelOption, Object>> iterator = optionMap.entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry<ChannelOption, Object> entry = iterator.next();
          bootstrap.option(entry.getKey(), entry.getValue());
        }
      }
      if (childOptionMap != null) {
        Iterator<Map.Entry<ChannelOption, Object>> iterator = childOptionMap.entrySet()
            .iterator();
        while (iterator.hasNext()) {
          Map.Entry<ChannelOption, Object> entry = iterator.next();
          bootstrap.childOption(entry.getKey(), entry.getValue());
        }
      }
      bootstrap.childOption(ChannelOption.ALLOW_HALF_CLOSURE, true);
      future = bootstrap.bind(port);
      future.sync();
      LOG.info("listen on port: " + port);
    } catch (InterruptedException e) {
      LOG.error("start Server encountered an error {}", e.getCause());
      shutdown();
    }
  }

  public void shutdown() {
    Future fw = workerGroup.shutdownGracefully();
    Future fb = bossGroup.shutdownGracefully();
    try {
      fw.await();
      fb.await();
    } catch (InterruptedException e) {

    }
  }
}
