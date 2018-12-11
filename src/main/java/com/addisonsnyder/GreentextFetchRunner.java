package com.addisonsnyder;

import java.io.IOException;
import java.nio.file.Paths;

public class GreentextFetchRunner {
	public static void main(String[] args) throws IOException {
		GreentextFetcher fetcher = new GreentextFetcher();
		fetcher.setScreenshotLocation(Paths.get("C:\\Users\\Dreadhawk177\\Desktop\\4chanGreentext"));
		fetcher.setUrl("http://boards.4chan.org/wg/");
		fetcher.Fetch();
	}
}
