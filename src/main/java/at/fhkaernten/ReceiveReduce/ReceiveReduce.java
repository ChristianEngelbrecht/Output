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
 * This verticle is used to receive the results from the MapReduce module (words : number of words).
 */
public class ReceiveReduce extends Verticle {
    private Logger log;
    private EventBus bus;
    private String uuid;
    private JsonObject json;

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
                        json = new JsonObject(buffer.toString());
                        uuid = json.getString("#ID#");
                        log.info("receiveResult:" + uuid);
                        bus.send("output.address", json);
                        netSocket.close();
                    }
                }));

            }
        }).listen(container.config().getInteger("port"));
    }
}
