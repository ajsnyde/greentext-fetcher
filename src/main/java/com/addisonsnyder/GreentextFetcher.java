package com.addisonsnyder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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

	private WebDriver driver;
	private int screenshotNumber = 0;
	private int page = 0;
	private String url = "http://boards.4chan.org/b/";
	private Path screenshotDirectory = Paths.get("");
	private final static Logger LOGGER = Logger.getLogger(GreentextFetcher.class);

	public GreentextFetcher() {
		LOGGER.info("Setting up PhantomJS through WebDriverManager..");
		WebDriverManager.phantomjs().setup();
		driver = new PhantomJSDriver();
		driver.manage().window().setSize(new Dimension(1280, 1080));
	}

	public void Fetch() throws IOException {
		// go through n pages
		for (int i = 0; i < 10; i++) {
			LOGGER.info("Getting/Scraping page " + i + "..");
			driver.get(url + ++page);

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

	/**
	 * Attempt to click link to expand thread
	 * 
	 * @param ele
	 */
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

		LOGGER.debug("Taking screenshot");
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

		// Copy the element screenshot to disk, increment screenshotNumber
		Path screenshotPath = Paths.get(this.screenshotDirectory.toString(), screenshotNumber++ + ".png");
		LOGGER.debug("Saving screenshot to disk: " + screenshotPath);
		FileUtils.copyFile(screenshot, screenshotPath.toFile());
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Path getScreenshotDirectory() {
		return screenshotDirectory;
	}

	public void setScreenshotDirectory(Path screenshotDirectory) {
		this.screenshotDirectory = screenshotDirectory;
	}
}
