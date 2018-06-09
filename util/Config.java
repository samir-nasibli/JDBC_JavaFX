package main.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import main.App;


public class Config {
	private static final String CONFIG_FILE = "/src/main/resources/config.properties";
	
	public static String DB_DRIVER;
	public static String DB_URL;
	
	public static void load() {
		String rootPath = System.getProperty("user.dir");
		Properties config = new Properties();
		
		try (FileInputStream fis = new FileInputStream(rootPath + CONFIG_FILE)){
			
			config.load(fis);
			DB_DRIVER = config.getProperty("DB_DRIVER");
			DB_URL = config.getProperty("DB_URL");
			
		} catch (IOException e) {
			if(App.DEBUG) e.printStackTrace();
		}
		
	}
}
