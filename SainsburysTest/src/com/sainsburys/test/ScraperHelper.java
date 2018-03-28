package com.sainsburys.test;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ScraperHelper {
    private URL url;
    private Connection con;
    
    public ScraperHelper(URL argUrl) {
        url = argUrl;
    }
    
    
    public ProductInformation getProductInfo(String argUrl) {
        String title = "";
        float size = 0.0f;
        float unitPrice = 0.0f;
        String description = "";
        
        try {
        	
        	//System.out.println("argUrl>>"+argUrl);
            Document doc = Jsoup.connect(argUrl).get();
            Element el = doc.select("div.productTitleDescriptionContainer").first();
            if (el == null) {
            	System.out.println("if");
                return null;
            } else {
                // Let's get the product title
                Element titleElement = el.getElementsByTag("h1").first();
                title = titleElement.text();
                
                // size of the web-page (in kb)
                size = doc.toString().length() / 1024;
            }
            
            // let's get price per unit
            el = doc.select("p.pricePerUnit").first();
            if (el == null) {
                return null;
            } else {
                String ptxt = el.text();                
               // System.out.println("ptxt>>> "+ptxt);
                ptxt = ptxt.replace("/unit", "");
                ptxt = ptxt.replace("£", "");
                //System.out.println("ptxt::  "+ptxt);
                float ppunit = Float.parseFloat(ptxt);
                unitPrice = ppunit;
            }
            
            // Let's get the description.
            // NOTE: I assume description part comes always first...
            el = doc.select("div.productText").first();
            if (el == null) {
                return null;
            } else {
                description = el.text();
            }
        } catch (IOException ex) {
            Logger.getLogger(ScraperHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new ProductInformation(title, size, unitPrice, description);
    } 
    
   
    public String scrape() {
        JSONObject json = new JSONObject();
        JSONArray results = new JSONArray();
        json.put("results", results);
        float total = 0.0f; // total unit price.
        
        Connection con = Jsoup.connect(url.toString());
        if (con == null) {
            // If we can't connect, we return an empty JSON document.
        	
        	//System.out.println("Connn");
            return "{}";
        }

        try {
            Element el = con.get().select("ul.productLister").first();
            if (el == null) {
                // There is no list of products so there is no need to continue...
            	//System.out.println("no list");
                return "{}";
            }
            
            Elements els = el.getElementsByTag("li");
            for (Element element: els) {
            	
                Element pinfoel = element.select("div.productInfo").first();
                if(pinfoel !=null){
                    Element linkel = pinfoel.getElementsByTag("a").first();
                   // System.out.println(">> "+linkel.attr("abs:href")); // if we need absolute URL
                    //String infoUrl = linkel.attr("href");                     
                     String infoUrl = linkel.attr("abs:href");                    
                     ProductInformation pinfo = getProductInfo(infoUrl);
                     //System.out.println("pinfo.toJSON()>> " + pinfo.toJSON());
                     results.add(pinfo.toJSON());
                     total += pinfo.getUnitPrice();
                }
                
                 
                
                
               
            }
        } catch (IOException ex) {

        	System.out.println(" Oppss...Some thing went wrong.. ");
        }
        
        // Set the total price /unit for all products.
        json.put("total", total);
        
        return json.toJSONString();
    } 
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL argUrl) {
        url = argUrl;
    }
    
} 