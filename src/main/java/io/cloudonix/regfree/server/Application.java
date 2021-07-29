package io.cloudonix.regfree.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import tech.greenfield.vertx.irked.Router;

public class Application extends AbstractVerticle {
	@Override
	public void start(Promise<Void> startFuture) throws Exception {
		Router router = new Router(vertx).configure(new RegFreeAPI(), "/v1/reg-free")
				.configure(new MobileUserAgentAPI(), "/v1/mobile-user-agent");
		vertx.createHttpServer(new HttpServerOptions()).requestHandler(router)
				.listen(config().getInteger("port", 8080));
	}
}

