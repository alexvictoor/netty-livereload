package com.github.alexvictoor.livereload;

import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Broadcaster implements FileSystemWatcher.Callback {

    public static final Logger logger = LoggerFactory.getLogger(Broadcaster.class);

    private final ChannelGroup allChannels;

    public Broadcaster(ChannelGroup allChannels) {
        this.allChannels = allChannels;
    }

    @Override
    public void onFileChanged(String path) {
        String escapedPath = path.replace("\\", "\\\\");
        String jsonMessage = "{\"command\":\"reload\",\"path\":\"" + escapedPath + "\",\"liveCSS\":true}";
        logger.info("Send notification {}", jsonMessage);
        allChannels.writeAndFlush(new TextWebSocketFrame(jsonMessage));
    }
}
