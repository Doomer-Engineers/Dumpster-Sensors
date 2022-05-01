package cucumber;

import com.dumpster_sensor.webapp.models.Alert;
import com.dumpster_sensor.webapp.models.User;
import com.dumpster_sensor.webapp.queries.AlertRepo;
import com.dumpster_sensor.webapp.queries.UserRepo;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Sleeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

public class MyStepdefs{
    private WebDriver driver;

    @Autowired
    private UserRepo uRepo;

    @Autowired
    private AlertRepo aRepo;


    @Before
    public void openBrowser() {
        System.setProperty("webdriver.chrome.driver","src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();
    }

    @After()
    public void closeBrowser() {
        driver.quit();
    }

    @Given("the user is on the {string}")
    public void theUserCallsLogin(String call) {
        driver.get("http://localhost:8084" + call);
    }

    @When("the user has {string} of {string}")
    public void theUserHasOf(String parameter, String value) {

        WebElement container = driver.findElement(By.className("container"));
        WebElement form = container.findElement(By.className("form-signin"));

        if (parameter.equals("username")){
            WebElement input = form.findElement(By.id("username"));
            input.sendKeys(value);
        }
        else if (parameter.equals("password")){
            WebElement input = form.findElement(By.id("password"));
            input.sendKeys(value);
        }

    }

    @When("the user clicks {string} with class {string}")
    public void theUserClicks(String value, String search) {
        WebElement container = driver.findElement(By.className("container"));
        WebElement form = container.findElement(By.className("form-signin"));
        WebElement button = form.findElement(By.className(search));
        button.click();
    }

    @When("the user clicks link {string}")
    public void theUserClicksLink(String id) {
        if (id.equals("coe") || id.equals("sus") || id.equals("uiowa")){
            WebElement main = driver.findElement(By.id("main"));
            WebElement links = main.findElement(By.id("links"));
            WebElement link = links.findElement(By.id(id));
            link.click();
        }
        else if(id.equals("logs") || id.equals("logout") || id.equals("view-sensors") || id.equals("find-sensor")
                || id.equals("add-sensor") || id.equals("change-password") || id.equals("add-user") || id.equals("update-users")){
            WebElement container = driver.findElement(By.className("container-fluid"));
            WebElement sidenav = container.findElement(By.id("mySidenav"));
            String js = "document.getElementById(\"mySidenav\").style.width = \"250px\";";
            ((JavascriptExecutor) driver).executeScript(js, sidenav);

            if(id.equals("add-user") || id.equals("update-users")){
                WebElement role = sidenav.findElement(By.id("role"));
                WebElement header = role.findElement(By.id("start-" + id));
                WebElement link = header.findElement(By.id(id));
                link.click();
            }
            else{
                WebElement header = sidenav.findElement(By.id("start-" + id));
                WebElement link = header.findElement(By.id(id));
                link.click();
            }
        }
    }

    @Then("the user is on the external page {string}")
    public void theUserIsOnTheExternalPage(String page) {
        driver.get(page);
    }

    @And("with user account with username of {string} and password {string}")
    public void withUserAccountWithUsernameOfAndPassword(String username, String password) {
        User user = new User(1000L, "admin", username, password, "testing@uiowa.edu");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        uRepo.save(user);
    }

    @Then("the user is on page with title {string}")
    public void theUserIsOnPage(String page) {
        assertThat(driver.getTitle(), equalTo(page));
        if(page.equals("Confirm Log Out?")){
            WebElement container = driver.findElement(By.className("container"));
            WebElement form = container.findElement(By.className("form-signin"));
            WebElement button = form.findElement(By.className("btn"));
            button.click();
            assertThat(driver.getTitle(), equalTo("Please sign in"));
        }
    }

    @And("an alert has arisen")
    public void anAlertHasArisen() {
        Alert alert = new Alert(0L, "Testing alert cucumber", 0L, false);
        aRepo.save(alert);
    }

    @When("the user clicks button {string} on page {string}")
    public void theUserClicksButton(String button, String page) {
        if (page.equals("logs")){
            if(button.equals("back")){
                WebElement container = driver.findElement(By.className("container"));
                WebElement div = container.findElement(By.className("py-2"));
                WebElement form = div.findElement(By.id("back-form"));
                WebElement div2 = form.findElement(By.id("back-div"));
                WebElement btn  = div2.findElement(By.className("btn"));
                btn.click();
            }
        }
    }
}
