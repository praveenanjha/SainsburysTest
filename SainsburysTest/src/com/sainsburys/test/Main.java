package com.sainsburys.test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class which contains the entry point.
 */
public class Main {
    public static  void main(String[] args) {
        String urlStr = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
              
        try {
            URL url = new URL(urlStr);
            ScraperHelper scraper = new ScraperHelper(url);
            System.out.println(scraper.scrape());
        } catch (MalformedURLException ex) {
            System.out.println("The given web-address '" + urlStr + "' is not valid. Exiting....");
        }
    }
}
