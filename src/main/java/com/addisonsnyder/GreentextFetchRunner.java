package com.addisonsnyder;

import java.io.IOException;
import java.nio.file.Paths;

public class GreentextFetchRunner {
	public static void main(String[] args) throws IOException {
		GreentextFetcher fetcher = new GreentextFetcher();
		fetcher.setScreenshotDirectory(Paths.get(""));
		fetcher.setUrl("http://boards.4chan.org/wg/");
		fetcher.Fetch();
	}
}
