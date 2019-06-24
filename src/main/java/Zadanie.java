import com.browserup.bup.BrowserUpProxy;
import com.browserup.bup.BrowserUpProxyServer;
import com.browserup.bup.client.ClientUtil;
import com.browserup.bup.proxy.CaptureType;
import com.browserup.harreader.model.Har;
import com.browserup.harreader.model.HarEntry;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class Zadanie {


    public static void main(String[]args) {
        BrowserUpProxy proxy = new BrowserUpProxyServer();
        proxy.start();
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
        String driverPath = "C:\\Program Files\\JetBrains\\toolsy\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver",driverPath);
        WebDriver driver = new ChromeDriver(capabilities);
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        proxy.newHar("cineworld.co.uk");
        driver.get("http://www.cineworld.co.uk");
        System.out.println("get");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,1000)");
        waitForWebElementExplicityWait(By.xpath("//div[@class = 'loaded']"), driver);
        driver.quit();


        Har har = proxy.getHar();

        List<HarEntry> list ;
        list =  har.getLog().getEntries();
        System.out.println(list.size());
        List<HarEntry> result = new ArrayList();
        int code;
        for (HarEntry element: list) {
            code = element.getResponse().getStatus() ;
            if (code >= 400){
                System.out.println("Find " + code);
                result.add(element);
            }
        }
        if(result.size()==0){
            System.out.println("Every response are below 400");
        }
    }
    static void waitForWebElementExplicityWait(By locator, WebDriver driver) {
        Wait<WebDriver> wait = new WebDriverWait(driver, 10L);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
