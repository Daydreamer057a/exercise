import io.vertx.core.Vertx;

/* deployment of vertx */
public class Application {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(Main.class.getName());
    }
}