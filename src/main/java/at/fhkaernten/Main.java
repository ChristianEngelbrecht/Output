package at.fhkaernten;

import org.apache.commons.io.IOUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.io.IOException;
import java.io.InputStream;

/**
 Call of class Main is done through a declaration in file resources/mod.json -> This is the entry point of the program.
 The main class is used to deploy verticles (with included JSON configuration file -> resources/<NameOfVerticle>.json
 **/
public class Main extends Verticle {

    @Override
    public void start(){
        deployVerticle("at.fhkaernten.Output.Output");
        deployVerticle("at.fhkaernten.ReceiveReduce.ReceiveReduce");
    }

    private void deployVerticle(final String classname) {
        try {
            container.deployVerticle(
                    classname,
                    getConfigs(classname),
                    1,
                    new AsyncResultHandler<String>() {

                        @Override
                        public void handle(AsyncResult<String> asyncResult) {
                            container.logger().info(String.format("Verticle %s has been deployed.", classname));
                        } // handle
                    } // handler
            );
        } catch (Exception e) {
            container.logger().error("failed to deploy "+classname, e);
        }
    } // deployVerticle

    private static JsonObject getConfigs(String classname) throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classname.replaceAll("\\.", "/")+".json");

        JsonObject config = new JsonObject(IOUtils.toString(is, "UTF-8"));
        JsonObject c = config.getObject("config");

        return c;
    } // getConfigs
}
