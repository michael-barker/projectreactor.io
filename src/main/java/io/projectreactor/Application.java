package io.projectreactor;


import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.reactive.function.RequestPredicates.GET;
import static org.springframework.web.reactive.function.RouterFunctions.route;
import static org.springframework.web.reactive.function.ServerResponse.*;
import reactor.ipc.netty.http.HttpServer;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.BodyInserters;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.accept.PathExtensionContentTypeResolver;
import org.springframework.web.reactive.function.RouterFunction;
import org.springframework.web.reactive.function.RouterFunctions;

/**
 * Main Application for the Project Reactor home site.
 */
public class Application {

	public static void main(String... args) throws InterruptedException {
		HttpHandler httpHandler = RouterFunctions.toHttpHandler(routes());
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
		HttpServer server = HttpServer.create("localhost", 8080);
		server.startAndAwait(adapter);
	}

	private static RouterFunction<?> routes() {

		Map<String, MediaType> mediaTypes = new HashMap<>();
		mediaTypes.put("css", MediaType.valueOf("text/css"));
		mediaTypes.put("html", MediaType.valueOf("text/html"));
		mediaTypes.put("js", MediaType.valueOf("application/javascript"));
		mediaTypes.put("png", MediaType.valueOf("image/png"));
		mediaTypes.put("jpg", MediaType.valueOf("image/jpg"));
		mediaTypes.put("ico", MediaType.valueOf("image/x-icon"));
		mediaTypes.put("woff2", MediaType.valueOf("font/woff2"));
		mediaTypes.put("woff", MediaType.valueOf("application/font-woff"));
		mediaTypes.put("ttf", MediaType.valueOf("font/truetype"));
		PathExtensionContentTypeResolver contentTypeResolver = new PathExtensionContentTypeResolver(mediaTypes);
		contentTypeResolver.setIgnoreUnknownExtensions(false);

		// TODO Use andRoute() when https://jira.spring.io/browse/SPR-14904 will be fixed
		return route(GET("/docs/api/**"), request ->
				status(FOUND).header(LOCATION, request.path().replace("/docs/", "/old/")).build()
			).and(route(GET("/docs/reference/**"), request ->
				status(FOUND).header(LOCATION, request.path().replace("/docs/", "/old/")).build()
			).and(route(GET("/docs/raw/**"), request ->
				status(FOUND).header(LOCATION, request.path().replace("/docs/", "/old/")).build()
			).and(route(GET("/core/docs/reference/**"), request ->
				status(FOUND).header(LOCATION, "https://github.com/reactor/reactor-core/blob/master/README.md").build()
			).and(route(GET("/"), request ->
				ok().body(BodyInserters.fromResource(new ClassPathResource("static/index.html")))
			).and(route(GET("/docs"), request ->
				ok().body(BodyInserters.fromResource(new ClassPathResource("static/docs/index.html")))
			).and(route(GET("/{file}"), request ->
				ok().body(BodyInserters.fromResource(new ClassPathResource("static/" + request.pathVariable("file"))))
			).and(route(GET("/assets/{dir}/{file}"), request -> {
					// TODO Simplify when https://jira.spring.io/browse/SPR-14905 will be fixed
					Resource resource = new ClassPathResource("static/assets/" + request.pathVariable("dir") + "/" + request.pathVariable("file"));
					return ok().contentType(contentTypeResolver.resolveMediaTypeForResource(resource)).body(BodyInserters.fromResource(resource));
				}
			)
		)))))));
	}

}
