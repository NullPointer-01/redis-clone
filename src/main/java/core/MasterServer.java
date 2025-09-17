package core;

import conf.Configuration;
import service.AOFPersistenceHandler;
import service.BlockingOpsManager;
import service.EventLoop;
import service.MasterReplicationHandler;

import java.io.IOException;
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
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while running redis master server. " + e);
        } finally {
            AOFPersistenceHandler.getInstance().shutdown();
            BlockingOpsManager.getInstance().shutdown();
            MasterReplicationHandler.getInstance().shutdown();
        }
    }
}
