package at.fhkaernten.Output;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.impl.Json;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetServer;
import org.vertx.java.core.net.NetSocket;
import org.vertx.java.platform.Verticle;

import java.util.Iterator;
import java.util.Map;


/**
 * Created by Christian on 04.04.2015.
 */
public class Output extends Verticle {
    private EventBus bus;
    private Logger log;
    private JsonObject result = new JsonObject();
    private String source;
    private long time;
    @Override
    public void start(){
        bus = vertx.eventBus();
        log = container.logger();
        bus.registerHandler("output.address", new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> message) {
                log.info("finish");
                source = message.body().getString("#SOURCE#");
                time = Long.valueOf(message.body().getString("#TIME#"));
                message.body().removeField("#SOURCE#");
                message.body().removeField("#TIME#");
                addToResult(message.body());
            }
        });
    }
    private void addToResult(JsonObject message){
        Iterator it = message.toMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Object o = result.getInteger((String) pair.getKey());
            if(result.getInteger((String) pair.getKey()) != null){
                result.putNumber((String) pair.getKey(), result.getInteger((String) pair.getKey()) + (Integer) pair.getValue());
            } else {
                result.putNumber((String) pair.getKey(), (Integer) pair.getValue());
            }
        }

    }
}