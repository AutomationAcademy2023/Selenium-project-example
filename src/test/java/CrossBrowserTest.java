import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.annotations.*;

import java.util.Set;


public class CrossBrowserTest {
    WebDriver driver;
    @BeforeTest
    @Parameters("browser")
    public void setUp(@Optional String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "edge":
                driver = new EdgeDriver();
                break;
            default:
                System.out.println("Browser is not correct");
        }
    }

    @Test
    public void fileUploadTest(){
        driver.navigate().to("http://the-internet.herokuapp.com/");
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }
}
