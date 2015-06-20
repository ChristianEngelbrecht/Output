package at.fhkaernten.ReceiveReduce;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetServer;
import org.vertx.java.core.net.NetSocket;
import org.vertx.java.core.parsetools.RecordParser;
import org.vertx.java.platform.Verticle;

/**
 * Created by Christian on 04.04.2015.
 */
public class ReceiveReduce extends Verticle {
    private Logger log;
    private EventBus bus;
    @Override
    public void start() {

        log = container.logger();
        bus = vertx.eventBus();
        NetServer server = vertx.createNetServer();

        server.connectHandler(new Handler<NetSocket>() {
            @Override
            public void handle(final NetSocket netSocket) {
                log.info("A client has connected");
                netSocket.dataHandler(RecordParser.newDelimited("#END#", new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {
                        log.info("I received blalba");
                        bus.send("output.address", new JsonObject(buffer.toString()));
                        netSocket.close();
                    }
                }));

            }
        }).listen(container.config().getInteger("port"));
        // , container.config().getString("IP")
    }

}
