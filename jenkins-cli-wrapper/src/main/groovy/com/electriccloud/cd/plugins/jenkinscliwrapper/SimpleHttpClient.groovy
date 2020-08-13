package com.electriccloud.cd.plugins.jenkinscliwrapper

import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

import java.util.logging.Logger

class SimpleHttpClient {

    Logger logger = Logger.getAnonymousLogger()

    String endpoint
    String username
    String password

    File download(String path, String filepath) {
        File file = new File(filepath)

        OkHttpClient client = new OkHttpClient()
        Request request = buildRequest('GET', path)

        logger.finer("Downloading $path to $filepath")
        Response response = client.newCall(request).execute()
        FileOutputStream fileStream = new FileOutputStream(file)
        fileStream.write(response.body().bytes())

        return file
    }

    boolean isAccessible(String path) throws RuntimeException {
        OkHttpClient client = new OkHttpClient()
        Request request = buildRequest('GET', path)

        try {
            client.newCall(request).execute()
            return true
        }
        catch (Exception ex) {
            throw new RuntimeException("HTTP check returned: " + ex.getMessage())
        }
    }

    private Request buildRequest(String method, String path) {
        Request.Builder builder = new Request.Builder()
                .method(method, null)
                .url(sanitizeEndpoint(endpoint) + path)

        if (username && password) {
            String credential = Credentials.basic(username, password)
            builder.header("Authorization", credential)
        }

        return builder.build()
    }

    private static String sanitizeEndpoint(String endpoint) {
        if (endpoint.endsWith('/')) {
            endpoint = endpoint.replaceAll(/\/+$/, '')
        }

        return endpoint
    }

}
