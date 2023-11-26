import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;


public class ProjectTest {
    WebDriver driver;
    Actions action;
    JavascriptExecutor js;
    WebDriverWait wait;
    LocalDate date;


    @BeforeTest
    @Parameters({"browser"})
    public void setUp(@Optional String browser) {
        if (browser.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("edge")) {
            driver = new EdgeDriver();
        } else {
            System.out.println("Browser is not correct");
        }

        action = new Actions(driver);
        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        date = LocalDate.now();

    }

    public static String generateRandomAlphabetic(int length) {
        return ThreadLocalRandom.current().ints('a', 'z' + 1)
                .limit(length)
                .mapToObj(i -> String.valueOf((char) i))
                .collect(Collectors.joining());
    }

    @Test
    public void mainTest() {
        //Open website
        driver.navigate().to("http://tutorialsninja.com/demo/");
        driver.manage().window().maximize();

        //Go to registration and fill details
        driver.findElement(By.cssSelector("a[title='My Account']")).click();
        driver.findElement(By.xpath("//a[text()='Register']")).click();
        driver.findElement(By.id("input-firstname")).sendKeys("Abcd");
        driver.findElement(By.id("input-lastname")).sendKeys("Efgh");
        driver.findElement(By.id("input-telephone")).sendKeys("12345678");
        driver.findElement(By.id("input-password")).sendKeys("abcd1234");
        driver.findElement(By.id("input-confirm")).sendKeys("abcd1234");
        driver.findElement(By.cssSelector("input[name='newsletter'][value='1']")).click();
        driver.findElement(By.xpath("//input [@name='agree']")).click();

        // while 'mail is used' warning is present, regenerate random email (max 5 times)
        int numberOfIterations = 0;
        do {
            /*
              SAME AS:
              numberOfIterations++;
              if(numberOfIterations == 5)
            */
            if (numberOfIterations++ == 5) {
                System.out.println("Could not generate vacant email");
                break;
            }

            driver.findElement(By.id("input-email")).clear();
            driver.findElement(By.id("input-email")).sendKeys(generateRandomAlphabetic(10) + "@gmail.com"); // random mail
            driver.findElement(By.cssSelector("[type='submit']")).click();
        }
        while (!driver.findElements(By.className("alert-danger")).isEmpty());

        // Move to MP3 Players
        action.moveToElement(driver.findElement(By.xpath("//a[text()='Desktops']"))).perform();
        driver.findElement(By.xpath("//a[text()='Show AllDesktops']")).click();
        driver.findElement(By.xpath("//div[@class='list-group']/a[contains(text(), 'MP3 Players')]")).click();

        // Check iPod Classic hover text (tooltip)
        WebElement iPodClassic = driver.findElement(By.cssSelector("div[class='product-thumb'] img[alt='iPod Classic']"));
        boolean tooltip = iPodClassic.getAttribute("title").equalsIgnoreCase("ipod classic");
        System.out.println("iPod Classic text is shown: " + tooltip);

        // Open iPod Classic images and move through all of them
        iPodClassic.click();
        driver.findElement(By.xpath("//a[@title='iPod Classic']/img")).click();
        WebElement imgCounter = driver.findElement(By.className("mfp-counter"));
        wait.until(ExpectedConditions.visibilityOf(imgCounter));
        char imgMaxN = imgCounter.getText().charAt(imgCounter.getText().length() - 1);
        for (int i = 2; i <= Character.getNumericValue(imgMaxN); i++) {
            driver.findElement(By.cssSelector("[title='Next (Right arrow key)']")).click();
            // wait for image counter to update on each iteration
            wait.until(ExpectedConditions.textToBe(By.className("mfp-counter"), i + " of " + imgMaxN));
        }
        System.out.println("The last image had been reached: " + (imgMaxN == imgCounter.getText().charAt(0)));
        driver.findElement(By.className("mfp-close")).click();

        // Write a review
        driver.findElement(By.xpath("//a[text()='Write a review']")).click();
        driver.findElement(By.id("input-name")).clear();
        driver.findElement(By.id("input-name")).sendKeys("Abcd");
        driver.findElement(By.id("input-review")).sendKeys("This review was sponsored by Nord VPN");
        driver.findElement(By.cssSelector("input[name='rating'][value='5']")).click();
        driver.findElement(By.id("button-review")).click();
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[class='alert alert-success alert-dismissible']")));
            System.out.println("Review added successfully!");
        } catch (TimeoutException var30) {
            System.out.println("Couldn't add a review! (all required fields not filled, or review length out of bounds (25-100)");
        }


        // Add item to cart and wait until cart is updated (works only if we add just 1 item)
        String quantity = "1";
        driver.findElement(By.id("button-cart")).click();
        String price = (String) js.executeScript("return arguments[0].innerHTML;", driver.findElement(By.xpath("//ul/li/h2[contains(text(), '$')]")));
        String selectedValues = quantity + " item(s) - " + price;
        try {
            wait.until(ExpectedConditions.textToBe(By.xpath("//*[@id='cart-total']"), selectedValues)); // Checks exact text
            System.out.println("Item(s) was successfully added to the cart!");
        } catch (TimeoutException var29) {
            System.out.println("Item(s) was NOT successfully added to the cart!");
        }

        /*

        // If we have multiple items, we can make Custom Selenium Wait that waits for element partial text
        ExpectedCondition<Boolean> elementContainsText = driver -> {
            try {
                assert driver != null;
                WebElement element = driver.findElement(By.xpath("//*[@id='cart-total']")); // Element
                return element.getText().contains(quantity + " item(s)"); // Text that it should contain
            } catch (Exception e) {
                System.out.println("Item(s) was NOT successfully added to the cart!");
                return false;
            }
        };
        wait.until(elementContainsText); // it will wait until items quantity is updated on cart element

        */


        // Go to checkout and fill details
        WebElement cartButton = driver.findElement(By.cssSelector("[class='btn btn-inverse btn-block btn-lg dropdown-toggle']"));
        js.executeScript("arguments[0].click();", cartButton);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//strong[text()='Checkout']")));
        driver.findElement(By.xpath("//strong[text()='Checkout']")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("button-payment-address")));
        driver.findElement(By.id("input-payment-firstname")).sendKeys("Abcd");
        driver.findElement(By.id("input-payment-lastname")).sendKeys("Efgh");
        driver.findElement(By.id("input-payment-address-1")).sendKeys("abc st 27");
        driver.findElement(By.id("input-payment-city")).sendKeys("Tffzxv");
        // you can use Select class to select dropdown values as well
        driver.findElement(By.id("input-payment-country")).click();
        driver.findElement(By.xpath("//option[text()='Georgia']")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[text()='Tbilisi']")));
        driver.findElement(By.id("input-payment-zone")).click();
        driver.findElement(By.xpath("//option[text()='Tbilisi']")).click();
        driver.findElement(By.id("button-payment-address")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("button-shipping-address")));
        driver.findElement(By.id("button-shipping-address")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("button-shipping-method")));
        // Save flat rate delivery text
        String flatRateDeliveryFull = driver.findElement(By.xpath("//div[@id='collapse-shipping-method']//label")).getText();
        driver.findElement(By.id("button-shipping-method")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("button-payment-method")));
        js.executeScript("document.getElementsByName('agree')[0].checked=true;");
        driver.findElement(By.id("button-payment-method")).click();

        // Save SubTotal value from Cart dropdown menu
        WebElement cartButton2 = driver.findElement(By.cssSelector("[class='btn btn-inverse btn-block btn-lg dropdown-toggle']"));
        js.executeScript("arguments[0].click();", cartButton2);
        String subTotalCart = driver.findElement(By.xpath("//*[text()='Sub-Total']/ancestor::td/following-sibling::td")).getText();

        // Remove unnecessary characters from Flat Rate string (was 'Flat Shipping Rate -$8.00', will be '$8.00')
        StringBuilder flatRateDelivery = new StringBuilder();
        for (int i = 0; i < flatRateDeliveryFull.length(); ++i) {
            if (Character.isDigit(flatRateDeliveryFull.charAt(i)) ||
                    flatRateDeliveryFull.charAt(i) == '.' ||
                    flatRateDeliveryFull.charAt(i) == '$') {

                flatRateDelivery.append(flatRateDeliveryFull.charAt(i));

            }
        }
        /*
            //Simpler version with regex:
            String flatRateDeliveryFull = flatRateDeliveryFull.replaceAll("[^\\d.$]", "");
            //replaces any character except Digit, '.', and '&' with empty character ''
        */

        wait.until(ExpectedConditions.elementToBeClickable(By.id("button-confirm")));

        // ---- Check if SubTotal, Flat rate and Total values are correct, and confirm order
        String subTotalConfirm = driver.findElement(By.xpath("//*[text()='Sub-Total:']/ancestor::td/following-sibling::td")).getText();
        String flatRateConfirm = driver.findElement(By.xpath("//*[text()='Flat Shipping Rate:']/ancestor::td/following-sibling::td")).getText();
        String total = driver.findElement(By.xpath("//*[text()='Total:']/ancestor::td/following-sibling::td")).getText();
        System.out.println("Subtotal is correct: " + subTotalConfirm.equals(subTotalCart));
        System.out.println("Flat Shipping Rate is correct: " + flatRateConfirm.contentEquals(flatRateDelivery));

        // Convert strings into double and remove first character ($ symbol)
        double flatrt = Double.parseDouble(flatRateConfirm.substring(1, flatRateConfirm.length() - 1));
        double subttl = Double.parseDouble(subTotalConfirm.substring(1, subTotalConfirm.length() - 1));
        double ttl = Double.parseDouble(total.substring(1, total.length() - 1));

        System.out.println("Total is correct: " + (flatrt + subttl == ttl));
        // ----

        // Go to order history
        driver.findElement(By.id("button-confirm")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Continue']")));
        driver.findElement(By.cssSelector("a[title='My Account']")).click();
        driver.findElement(By.xpath("//ul[@class='dropdown-menu dropdown-menu-right']//a[text()='Order History']")).click();

        // Save header indexes
        List<WebElement> headers = driver.findElements(By.xpath("//thead//td"));
        int index = 0;
        int statusIndex = 0;
        int dateIndex = 0;

        for (WebElement hd : headers) {
            ++index;
            if (hd.getText().equalsIgnoreCase("status")) statusIndex = index;
            else if (hd.getText().equalsIgnoreCase("date added")) dateIndex = index;
        }

        // Check order dates are correct
        String status = driver.findElement(By.xpath("//tbody//td[" + statusIndex + "]")).getText();
        System.out.println("Status is pending: " + status.equalsIgnoreCase("pending"));
        String dateAdded = driver.findElement(By.xpath("//tbody//td[" + dateIndex + "]")).getText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("Date is correct: " + dateAdded.equals(date.format(formatter)));

    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }
}
