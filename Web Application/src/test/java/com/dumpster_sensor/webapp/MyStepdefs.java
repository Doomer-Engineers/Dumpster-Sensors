package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.models.Alert;
import com.dumpster_sensor.webapp.models.Sensor;
import com.dumpster_sensor.webapp.models.User;
import com.dumpster_sensor.webapp.queries.AlertRepo;
import com.dumpster_sensor.webapp.queries.SensorRepo;
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
import org.openqa.selenium.support.ui.Select;
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

    @Autowired
    private SensorRepo sRepo;


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
        User user = new User(1000L, "admin", username, password, "brenden-taul@uiowa.edu");
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

    @When("the user finds by {string} sensor {string}")
    public void theUserFindsBySensor(String func, String value) {
        WebElement container = driver.findElement(By.className("container"));
        WebElement form = container.findElement(By.id(func+"Form"));
        WebElement div = form.findElement(By.id(func+"Div"));
        WebElement div2 = div.findElement(By.className("pr-3"));
        WebElement input = div2.findElement(By.className("form-control"));
        input.sendKeys(value);
        WebElement confirmDiv = div.findElement(By.id(func+"Confirm"));
        WebElement button = confirmDiv.findElement(By.className("btn"));
        button.click();
    }

    @And("sensor is added")
    public void sensorIsAdded() {
        Sensor sensor = new Sensor(1L,"cutest", "false", null, null);
        sRepo.save(sensor);
    }

    @When("the user {string} sensor")
    public void theUserInstallsUninstallsSensor(String func) {
        WebElement container = driver.findElement(By.className("container-fluid"));
        WebElement group = container.findElement(By.className("form-group"));
        WebElement col = group.findElement(By.className("col-3"));
        WebElement border = col.findElement(By.className("border"));
        WebElement row = border.findElement(By.className("row"));
        WebElement div = row.findElement(By.id(func+"Div"));
        WebElement form  = div.findElement(By.id(func));
        WebElement button = form.findElement(By.className("btn"));
        button.click();
    }

    @When("the user updates sensor location with {string}")
    public void theUserUpdatesSensorLocationWith(String value) {
        WebElement container = driver.findElement(By.className("container"));
        WebElement form = container.findElement(By.id("form"));
        WebElement div = form.findElement(By.className("m-3"));
        WebElement div2 = div.findElement(By.id("update"));
        WebElement label = div2.findElement(By.className("col-form-label"));
        WebElement input = label.findElement(By.className("form-control"));
        input.sendKeys(value);
        WebElement div3 = div.findElement(By.id("button"));
        WebElement button = div3.findElement(By.className("btn"));
        button.click();
    }

    @When("the user adds a new {string} sensor at {string}")
    public void theUserAddsANewSensorAt(String isInstalled, String location) {
        WebElement container = driver.findElement(By.className("container"));
        WebElement form = container.findElement(By.id("form"));
        WebElement div = form.findElement(By.className("m-3"));
        WebElement div2 = div.findElement(By.id("locDiv"));
        WebElement label = div2.findElement(By.className("col-form-label"));
        WebElement input = label.findElement(By.className("form-control"));
        input.sendKeys(location);

        WebElement div3 = div.findElement(By.id("isInstalled"));
        WebElement div4 = div3.findElement(By.id("yes"));
        WebElement radio = div4.findElement(By.id("true"));
        radio.click();

        WebElement div5 = div.findElement(By.id("submit"));
        WebElement button = div5.findElement(By.className("btn"));
        button.click();
    }

    @When("the user adds a new user with username {string}, password {string}, role {string}, and email {string}")
    public void theUserAddsANewUserWithUsernamePasswordRoleAndEmail(String username, String password, String role, String email) {
        WebElement container = driver.findElement(By.className("container"));
        WebElement form = container.findElement(By.id("form"));
        WebElement ud = form.findElement(By.id("usernameDiv"));
        WebElement ud2 = ud.findElement(By.id("usernameDiv2"));
        WebElement ul2 = ud2.findElement(By.id("usernameLabel"));
        WebElement usernameInput = ul2.findElement(By.className("form-control"));
        usernameInput.sendKeys(username);

        WebElement ed = form.findElement(By.id("emailDiv"));
        WebElement ed2 = ed.findElement(By.id("emailDiv2"));
        WebElement el2 = ed2.findElement(By.id("emailLabel"));
        WebElement emailInput = el2.findElement(By.className("form-control"));
        emailInput.sendKeys(email);

        WebElement pd = form.findElement(By.id("passwordDiv"));
        WebElement pd2 = pd.findElement(By.id("passwordDiv2"));
        WebElement pl2 = pd2.findElement(By.id("passwordLabel"));
        WebElement passwordInput = pl2.findElement(By.className("form-control"));
        passwordInput.sendKeys(password);

        WebElement cd = form.findElement(By.id("checkDiv"));
        WebElement cd2 = cd.findElement(By.id("checkDiv2"));
        WebElement cl2 = cd2.findElement(By.id("checkLabel"));
        WebElement checkInput = cl2.findElement(By.className("form-control"));
        checkInput.sendKeys(password);

        WebElement rd = form.findElement(By.id("roleDiv"));
        WebElement rd2 = rd.findElement(By.id("roleDiv2"));
        WebElement rl2 = rd2.findElement(By.id("roleLabel"));
        WebElement roleInput = rl2.findElement(By.className("form-control"));
        Select selectObject = new Select(roleInput);
        selectObject.selectByValue(role);

        WebElement div = form.findElement(By.id("submit"));
        WebElement button = div.findElement(By.className("btn"));
        button.click();
    }
}
