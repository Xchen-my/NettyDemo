package com.netty.test.nettyserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class NettyServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //创建两个事件轮询组
        EventLoopGroup boos = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup();
        //为引导类注入两个实列轮询组
        serverBootstrap.group(boos,work);
        //设置通道的io类型
        serverBootstrap.channel(NioServerSocketChannel.class);
        //设置监听端口
        serverBootstrap.localAddress(new InetSocketAddress(8800));
        //设置通道参数
        serverBootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        //子通道流水线装配子通道的Handler流水线调用引导类的childHandler()方法
        // ，该方法需要传入一个ChannelInitializer通道初始化类的实例作为参数。每当父通道成功接收到一个连接并创建成功一个子通道后，
        // 就会初始化子通道，此时这里配置的ChannelInitializer实例就会被调用。
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                                         @Override
                                         protected void initChannel(SocketChannel ch) throws Exception {
                                            ch.pipeline().addLast();
                                         }
                                     }

        );
        ChannelFuture channelFuture = null;
        try {
            //调用同步方法绑定端口
            channelFuture = serverBootstrap.bind().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channelFuture.channel().localAddress();
        //自我阻塞，直到监听通道关闭
        ChannelFuture channelFuture1 = channelFuture.channel().closeFuture();
        try {
            channelFuture1.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //释放资源
        boos.shutdownGracefully();
        work.shutdownGracefully();
    }

}
