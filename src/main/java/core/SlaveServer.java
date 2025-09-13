package core;

import conf.Configuration;
import service.EventLoop;
import service.SlaveReplicationHandler;

import java.io.IOException;
import java.net.SocketException;
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
        } catch (SocketException e) {
            LOGGER.log(Level.SEVERE, "Socket Exception starting up redis slave server. " + e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO Exception starting up redis slave server. " + e);
        }
    }
}