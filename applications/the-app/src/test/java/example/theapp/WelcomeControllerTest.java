package example.theapp;


import com.example.theapp.WelcomeController;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class WelcomeControllerTest {

    private WelcomeController controller = new WelcomeController();

    @Test
    public void testHello() {
        String response = controller.hello();

        assertThat(response, equalTo("Hello world!"));
    }
}