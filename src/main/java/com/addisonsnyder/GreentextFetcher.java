package main.java.com.addisonsnyder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class GreentextFetcher {

	WebDriver driver;
	int i = 0;
	int page = 0;

	GreentextFetcher() {
		WebDriverManager.phantomjs().setup();
		driver = new PhantomJSDriver();
		driver.manage().window().setSize(new Dimension(1280, 1080));
	}

	public void Fetch() throws IOException {
		// go through n pages
		for (int i = 0; i < 10; i++) {
			driver.get("http://boards.4chan.org/b/" + ++page);

			List<WebElement> elements = driver.findElements(By.className("thread"));

			for (WebElement element : elements) {
				if (StringUtils.countMatches(element.getAttribute("innerHTML"), ("class=\"quote\"")) > 4) {
					clickFullText(element);
					takeScreenshotOfElement(element);
				}
			}
		}
		driver.quit();
	}

	private void clickFullText(WebElement ele) {
		try {
			WebElement clickSpan = ele.findElement(By.className("abbr"));
			if (clickSpan != null) {
				clickSpan.findElement(By.xpath("a")).click();
			}
		} catch (NoSuchElementException e) {
			// didn't find a "click to expand" link, oh well
		}
	}

	private void takeScreenshotOfElement(WebElement ele) throws IOException {

		// Get entire page screenshot
		((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		BufferedImage fullImg = ImageIO.read(screenshot);

		// Get the location of element on the page
		Point point = ele.getLocation();

		// Get width and height of the element
		int eleWidth = ele.getSize().getWidth();
		int eleHeight = ele.getSize().getHeight();

		// Crop the entire page screenshot to get only element screenshot
		BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
		ImageIO.write(eleScreenshot, "png", screenshot);

		// Copy the element screenshot to disk
		File screenshotLocation = new File("C:\\Users\\Dreadhawk177\\Desktop\\4chanGreentext" + i++ + ".png");
		FileUtils.copyFile(screenshot, screenshotLocation);
	}
}
