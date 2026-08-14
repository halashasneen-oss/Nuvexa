package com.nuvexa.app.core.model

/** HTTP status code + reason phrase are part of the HTTP specification itself and are
 * conventionally left untranslated (like error codes), so this reference list is English-only
 * by design — the surrounding screen chrome (search, labels) is fully localized. */
data class HttpStatusEntry(val code: Int, val reason: String, val description: String)

val HTTP_STATUS_CODES = listOf(
    HttpStatusEntry(200, "OK", "The request succeeded."),
    HttpStatusEntry(201, "Created", "The request succeeded and a new resource was created."),
    HttpStatusEntry(204, "No Content", "The request succeeded but there's no response body."),
    HttpStatusEntry(301, "Moved Permanently", "The resource has permanently moved to a new URL."),
    HttpStatusEntry(302, "Found", "The resource temporarily lives at a different URL."),
    HttpStatusEntry(304, "Not Modified", "The cached version is still valid; no need to re-download."),
    HttpStatusEntry(400, "Bad Request", "The server couldn't understand the request due to invalid syntax."),
    HttpStatusEntry(401, "Unauthorized", "Authentication is required and has failed or not been provided."),
    HttpStatusEntry(403, "Forbidden", "The server understood the request but refuses to authorize it."),
    HttpStatusEntry(404, "Not Found", "The requested resource couldn't be found."),
    HttpStatusEntry(405, "Method Not Allowed", "The request method isn't supported for this resource."),
    HttpStatusEntry(408, "Request Timeout", "The server timed out waiting for the request."),
    HttpStatusEntry(409, "Conflict", "The request conflicts with the current state of the resource."),
    HttpStatusEntry(410, "Gone", "The resource is no longer available and won't be back."),
    HttpStatusEntry(413, "Payload Too Large", "The request body is larger than the server will process."),
    HttpStatusEntry(415, "Unsupported Media Type", "The request's content type isn't supported."),
    HttpStatusEntry(418, "I'm a Teapot", "An April Fools' joke from RFC 2324 — some servers still honor it."),
    HttpStatusEntry(429, "Too Many Requests", "You've sent too many requests in a given time; rate limited."),
    HttpStatusEntry(500, "Internal Server Error", "The server encountered an unexpected condition."),
    HttpStatusEntry(501, "Not Implemented", "The server doesn't support the functionality required."),
    HttpStatusEntry(502, "Bad Gateway", "The server, acting as a gateway, got an invalid response."),
    HttpStatusEntry(503, "Service Unavailable", "The server isn't ready to handle the request (overload/maintenance)."),
    HttpStatusEntry(504, "Gateway Timeout", "The gateway didn't get a response in time from an upstream server."),
)
