package integrationtests;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TheAppIntegrationTest {

    private Process server;
    private OkHttpClient client = new OkHttpClient();

    @Before
    public void setup() throws IOException {
        ProcessBuilder builder = new ProcessBuilder()
            .redirectError(new File("build/server.err.log"))
            .redirectOutput(new File("build/server.out.log"))
            .command("java", "-jar", "../the-app/build/libs/the-app.jar");

        Map<String, String> env = builder.environment();
        env.put("SERVER_PORT", "9090");

        server = builder.start();

        waitUntilServerIsUp();
    }

    @After
    public void teardown() {
        server.destroyForcibly();
    }


    @Test
    public void testTheApp() throws IOException {
        Request request = new Request.Builder()
            .url("http://localhost:9090")
            .build();

        try (Response response = client.newCall(request).execute()) {
            assertThat(response.code(), equalTo(200));
            assertThat(response.body().string(), equalTo("Hello world!"));
        }
    }


    private void waitUntilServerIsUp() {
        int attempts = 0;
        boolean serverIsUp = false;

        while (attempts < 500 && !serverIsUp) {
            try {
                new Socket("localhost", 9090);
                serverIsUp = true;

            } catch (IOException e) {
                attempts ++;

                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
            }
        }

        if (!serverIsUp) {
            throw new IllegalStateException("Timed out while waiting for server to be up");
        }
    }
}
