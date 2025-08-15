package core;

import conf.Configuration;

import java.util.logging.Logger;

public abstract class Server extends Thread {
    protected static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    protected final Configuration conf;

    public Server(Configuration conf) {
        this.conf = conf;
    }
}
