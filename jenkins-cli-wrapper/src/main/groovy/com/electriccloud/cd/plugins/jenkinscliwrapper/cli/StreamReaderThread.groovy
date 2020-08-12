package com.electriccloud.cd.plugins.jenkinscliwrapper.cli

abstract class StreamReaderThread {

    static Thread runReaderStream(InputStream stream, Appendable output) {
        Thread thread = new Thread(new TextDumper(stream, output))
        thread.start()
        return thread
    }

    private static class TextDumper implements Runnable {
        InputStream input
        Appendable app

        TextDumper(InputStream input, Appendable app) {
            this.input = input
            this.app = app
        }

        void run() {
            InputStreamReader isr = new InputStreamReader(input)
            BufferedReader br = new BufferedReader(isr)
            String next
            try {
                while ((next = br.readLine()) != null) {
                    if (app != null) {
                        app.append(next)
                        app.append("\n")
                    }
                }
            } catch (IOException e) {
                if (e.getMessage() != "Stream closed") {
                    // Not using logger because we are in a thread
                    // and log will be messed up with two output lines
                    System.err.println("exception while reading process stream: " + e.getMessage())
                }
            }
        }
    }


}
