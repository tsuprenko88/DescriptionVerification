package utility;

import io.appium.java_client.android.AndroidDriver;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

public class Utils {
	
	public String xlPath;
	public String xlSheetName;
	public String xlWritePath;
	public String localArray[][], number, firstNumber, secondNumber, action, expResult, actResult;
	public int xlRows, xlCols;
	public boolean fail = false;
	public List<String> failName = new ArrayList<String>();
	
	public AndroidDriver driver;
	public WebDriverWait wait;
	
	
	public void xlRead(String xlPath, String xlSheetName) throws Exception {
		File xlFile = new File(xlPath);
		FileInputStream xlInput = new FileInputStream(xlFile);
		@SuppressWarnings({ "resource" })
		XSSFWorkbook workbook = new XSSFWorkbook(xlInput);
		XSSFSheet sheet = workbook.getSheet(xlSheetName);
			xlRows = sheet.getLastRowNum() + 1;
			xlCols = sheet.getRow(0).getLastCellNum();
			//System.out.println("Total rows: " + xlRows);
			//System.out.println("Total columns: " + xlCols);
			localArray = new String[xlRows][xlCols];
			for (int i =0; i < xlRows; i++) {
				XSSFRow row = sheet.getRow(i);
				for (int j = 0; j < xlCols; j++) {
					XSSFCell cell = row.getCell(j);
					String value = cellToString(cell);
					localArray[i][j] = value;
					//System.out.print(value + " ");
				}
				//System.out.println();
			}
	}
	
	public static String cellToString(XSSFCell cell) {
		int type = cell.getCellType();
		Object result;
		switch(type) {
		case 0:
			result = cell.getNumericCellValue();
			break;
		case 1:
			result = cell.getStringCellValue();
			break;
		default:
			throw new RuntimeException("This type is not supported");
		}
		return result.toString();
	}
	
	public void xlWrite(String xlWritePath, String xlSheetName, String[][] localArray) throws Exception {
		File xlOutput = new File(xlWritePath);
		WritableWorkbook workbook = Workbook.createWorkbook(xlOutput);
		workbook.createSheet(xlSheetName, 0);
		WritableSheet sheet = workbook.getSheet(0);
		for (int i = 0; i < localArray.length; i++) {
			for (int j = 0; j < localArray[i].length; j++) {
				if (localArray[i][j] != null) {
					Label data = new Label(j, i, localArray[i][j]);
					sheet.addCell(data);
				}
			}
		}
		workbook.write();
		workbook.close();
	}
	
	public void arithmeticAction(String firstNumber, String secondNumber, String action) throws Exception {
		
		driver.findElement(By.id("bt_0"+firstNumber+"")).click();
				
		switch(action) {
		case "add":
			driver.findElement(By.id("bt_add")).click();
			break;
		case "subtract":
			driver.findElement(By.id("bt_sub")).click();
			break;
		case "multiply":
			driver.findElement(By.id("bt_mul")).click();
			break;
		case "divide":
			driver.findElement(By.id("bt_div")).click();
			break;
		default:
			throw new Exception("Unknown arithmetical action");
		}
		
		driver.findElement(By.id("bt_0"+secondNumber+"")).click();
		
		driver.findElement(By.id("bt_equal")).click();
		
		String result = driver.findElement(By.id("txtCalc")).getText();
		
		String[] resultSplit = result.split("=");
		
		String[] resultSplit2 = resultSplit[1].split(". Editing");
		
		actResult = resultSplit2[0];
		//System.out.println(actResult);
		
		driver.findElement(By.id("bt_clear")).click();
	}
	
	public void setUp() throws MalformedURLException {
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability("platformName", "Android");
		cap.setCapability("platformVersion", "5.0");
		cap.setCapability("deviceName", "MyAndroid");
		cap.setCapability("appPackage", "com.sec.android.app.popupcalculator");
		cap.setCapability("appActivity", "com.sec.android.app.popupcalculator.Calculator");
		driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), cap);
		wait = new WebDriverWait(driver, 25);
	}
	
	public void quit() throws Exception {
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
