import java.net.URL;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Test;

import utility.Utils;
import io.appium.java_client.android.AndroidDriver;


public class WebIMDBdesc extends Utils {
	
	@Test
	public void verifyDescription() throws Exception {
		
		xlPath = "excel-input//IMDBdescription.xlsx";
		xlSheetName = "verifyDescription";
		xlWritePath = "excel-output//"+xlSheetName+".xls";
		fail = false;
		
		xlRead(xlPath, xlSheetName);
		
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability("platformName", "Android");
		cap.setCapability("platformVersion", "5.0");
		cap.setCapability("deviceName", "MyAndroid");
		cap.setCapability("browserName", "Chrome");
		cap.setCapability("platformName", "com.android.chrome");
		cap.setCapability("platformName", "com.google.android.apps.chrome.Main");
		
		AndroidDriver driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), cap);
		WebDriverWait wait = new WebDriverWait(driver, 25);
			
		for (int i = 1; i < xlRows; i++) {
			
			number = localArray[i][0];
			String shortName = localArray[i][1];
			String fullName = localArray[i][2];
			expResult = localArray[i][3];
			
			driver.get("http://m.imdb.com/");
			
			for (String contextName : driver.getContextHandles()) {
				//System.out.println(contextName);
				if (contextName.contains("WEBVIEW")) {
					driver.context(contextName);
					break;
				}
			}
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
			
			driver.findElement(By.id("suggestion-search")).sendKeys(shortName);
			
			Thread.sleep(1000);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("autocomplete")));
			
			List<WebElement> names = driver.findElements(By.xpath("//div[@id='autocomplete']/a/div[@class='label']"));
			
			for (int j = 0; j < names.size(); j++) {
				//System.out.println(names.get(i).getText());
				if (names.get(j).getText().contains(fullName)) {
					names.get(j).click();
					break;
				}
			}
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
			
			actResult = driver.findElement(By.xpath("//p[@itemprop='description']")).getText();
			
			localArray[i][4] = actResult;
			
			if (expResult.equals(actResult)) {
				localArray[i][5] = "Pass";
				Reporter.log(number + ") " + fullName + " contains a correct description", true);
			}
			else {
				localArray[i][5] = "FAIL";
				fail = true;
				failName.add(number + ") " + fullName + " contains a wrong description");
			}
			
			xlWrite(xlWritePath, xlSheetName, localArray);
			
		}
		
		if (fail) {
			Reporter.log("-------------FAILED TESTS-------------", true);
			for (int i = 0; i < failName.size(); i++) {
				Reporter.log(failName.get(i) ,true);
			}
			driver.quit();
			throw new Exception("Look at \"FAILED TESTS\" section above");
		}
		driver.quit();
		
	}
		
		

		
		



}
