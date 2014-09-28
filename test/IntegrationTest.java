import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import static org.fluentlenium.core.filter.FilterConstructor.*;

public class IntegrationTest {

    @Test
    public void test() {
        running(testServer(3333, fakeApplication()), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/search/cat");
                System.out.println("BROWSER PAGE SOURCE: " + browser.pageSource());
                assertThat(browser.pageSource()).contains("Your new application is ready.");
            }
        });
    }

}
