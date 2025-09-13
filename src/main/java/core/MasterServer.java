package core;

import conf.Configuration;
import service.AOFPersistenceHandler;
import service.EventLoop;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;

public class MasterServer extends Server {
    public MasterServer(Configuration conf) {
        super(conf);
    }

    @Override
    public void run() {
        try {
            AOFPersistenceHandler.getInstance().loadFromAof();
            EventLoop.getInstance().start();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception starting up redis master server. " + e);
        }
    }
}
