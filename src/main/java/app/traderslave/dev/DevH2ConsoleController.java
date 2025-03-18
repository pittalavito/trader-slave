package app.traderslave.dev;

import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dev/h2-console")
@CrossOrigin
public class DevH2ConsoleController {

    @Value("${spring.datasource.username}")
    private String jbdcUsername;

    @Value("${spring.datasource.password}")
    private String jbdcPassword;

    @Value("${spring.datasource.url}")
    private String jbdcUri;

    @Value("${spring.profiles.active}")
    private String propertiesActiveProfile;

    @Value("${server.port}" + "${server.servlet.context-path}" + "${spring.h2.console.path}")
    private String h2ConsolePath;

    private static final String INPUT_PASSWORD_SELECTOR = "input[name='password']";
    private static final String INPUT_JBC_URL = "input[name='url']";
    private static final String CONNECT_BUTTON_SELECTOR = "input[value='Connect']";

    private WebDriver driver;

    @PostMapping(path = "/start")
    @SneakyThrows
    public void startBrowser() {
        if (!propertiesActiveProfile.equals("h2")) {
            throw new CustomException(ExceptionEnum.H2_CONSOLE_NOT_AVAILABLE);
        }

        if (driver != null) {
            driver.quit();
        }

        driver = new ChromeDriver();
        driver.get("http://localhost:" + h2ConsolePath);
        Thread.sleep(1000);
        WebElement usernameField = driver.findElement(By.cssSelector(INPUT_PASSWORD_SELECTOR));
        usernameField.clear();
        usernameField.sendKeys(jbdcUsername);

        WebElement passwordField = driver.findElement(By.cssSelector(INPUT_PASSWORD_SELECTOR));
        passwordField.clear();
        passwordField.sendKeys(jbdcPassword);

        WebElement jdbcUrlField = driver.findElement(By.cssSelector(INPUT_JBC_URL));
        jdbcUrlField.clear();
        jdbcUrlField.sendKeys(jbdcUri);

        WebElement connectButton = driver.findElement(By.cssSelector(CONNECT_BUTTON_SELECTOR));
        connectButton.click();
    }

    @PostMapping(path = "/stop")
    public void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}