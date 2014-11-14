/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.alexvictoor.livereload;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Copy/paste from Netty websocket example
 */
public final class WebSocketServer {

    public static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    static final int DEFAULT_PORT = 35729;

    public final int port;
    private final String rootFolder;

    public WebSocketServer(String rootFolder) {
        this.rootFolder = rootFolder;
        port = DEFAULT_PORT;
    }

    public WebSocketServer(int port, String rootFolder) {
        this.port = port;
        this.rootFolder = rootFolder;
    }

    public void start() throws InterruptedException {
        File root = new File(rootFolder);
        if (!root.exists() || root.isFile()) {
            throw new IllegalArgumentException(rootFolder +" is not a valid folder path to watch");
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ChannelGroup allChannels =
                    new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            Broadcaster broadcaster = new Broadcaster(allChannels);

            FileSystemWatcher watcher = new FileSystemWatcher(rootFolder);
            watcher.addCallback(broadcaster);

            FileReader fileReader = new FileReader();
            String jsContent = fileReader.readFileFromClassPath("/livereload.js");

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new WebSocketServerInitializer(allChannels, jsContent));

            Channel ch = b.bind(port).sync().channel();
            watcher.start();
            logger.info("Livereload server ready");

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
