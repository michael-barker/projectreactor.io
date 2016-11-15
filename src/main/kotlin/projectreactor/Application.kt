package projectreactor


import java.util.HashMap

import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpStatus.*
import org.springframework.web.reactive.function.RequestPredicates.GET
import org.springframework.web.reactive.function.RouterFunctions.route
import org.springframework.web.reactive.function.ServerResponse.*
import reactor.ipc.netty.http.HttpServer

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.codec.BodyInserters
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.accept.PathExtensionContentTypeResolver
import org.springframework.web.reactive.function.HandlerFunction
import org.springframework.web.reactive.function.RouterFunction
import org.springframework.web.reactive.function.RouterFunctions

/**
 * Main Application for the Project Reactor home site.
 */
object Application {

    @JvmStatic fun main(args: Array<String>) {
        val httpHandler = RouterFunctions.toHttpHandler(routes())
        val adapter = ReactorHttpHandlerAdapter(httpHandler)
        val server = HttpServer.create("localhost", 8080)
        server.startAndAwait(adapter)
    }

    private fun routes(): RouterFunction<*> {

        val mediaTypes = HashMap<String, MediaType>()
        mediaTypes.put("css", MediaType.valueOf("text/css"))
        mediaTypes.put("html", MediaType.valueOf("text/html"))
        mediaTypes.put("js", MediaType.valueOf("application/javascript"))
        mediaTypes.put("png", MediaType.valueOf("image/png"))
        mediaTypes.put("jpg", MediaType.valueOf("image/jpg"))
        mediaTypes.put("ico", MediaType.valueOf("image/x-icon"))
        mediaTypes.put("woff2", MediaType.valueOf("font/woff2"))
        mediaTypes.put("woff", MediaType.valueOf("application/font-woff"))
        mediaTypes.put("ttf", MediaType.valueOf("font/truetype"))
        val contentTypeResolver = PathExtensionContentTypeResolver(mediaTypes)
        contentTypeResolver.setIgnoreUnknownExtensions(false)

        return route(GET("/docs/api/**"), HandlerFunction { request -> status(FOUND).header(LOCATION, request.path().replace("/docs/", "/old/")).build() })
                .andRoute(GET("/docs/reference/**"), HandlerFunction { request -> status(FOUND).header(LOCATION, request.path().replace("/docs/", "/old/")).build() })
                .andRoute(GET("/docs/raw/**"), HandlerFunction { request -> status(FOUND).header(LOCATION, request.path().replace("/docs/", "/old/")).build() })
                .andRoute(GET("/core/docs/reference/**"), HandlerFunction { request -> status(FOUND).header(LOCATION, "https://github.com/reactor/reactor-core/blob/master/README.md").build() })
                .andRoute(GET("/"), HandlerFunction { request -> ok().body(BodyInserters.fromResource(ClassPathResource("static/index.html"))) })
                .andRoute(GET("/docs"), HandlerFunction { request -> ok().body(BodyInserters.fromResource(ClassPathResource("static/docs/index.html"))) })
                .andRoute(GET("/{file}"), HandlerFunction { request -> ok().body(BodyInserters.fromResource(ClassPathResource("static/" + request.pathVariable("file")))) })
                .andRoute(GET("/assets/{dir}/{file}"), HandlerFunction { request ->
                    // TODO Simplify when https://jira.spring.io/browse/SPR-14905 will be fixed
                    val resource = ClassPathResource("static/assets/" + request.pathVariable("dir") + "/" + request.pathVariable("file"))
                    ok().contentType(contentTypeResolver.resolveMediaTypeForResource(resource)).body(BodyInserters.fromResource<Resource>(resource))
                })
    }
}


