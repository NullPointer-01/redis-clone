package core;

import conf.Configuration;
import service.EventLoop;
import service.SlaveReplicationHandler;

import java.io.IOException;
import java.util.logging.Level;

public class SlaveServer extends Server {
    public SlaveServer(Configuration conf) {
        super(conf);
    }

    @Override
    public void run() {
        try {
            new SlaveReplicationHandler(conf).start(); // Sync with master in separate thread
            EventLoop.getInstance().start();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception running redis slave server. " + e);
        }
    }
}