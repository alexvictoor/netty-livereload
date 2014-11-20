package com.github.alexvictoor.livereload;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.GlobalEventExecutor;


/**
 * Copy/paste from Netty websocket example
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelGroup allChannels;
    private final String jsContent;

    public WebSocketServerInitializer(ChannelGroup allChannels, String jsContent) {
        this.allChannels = allChannels;
        this.jsContent = jsContent;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerHandler(allChannels, jsContent));
    }
}
