package temp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class IconXML {

	public static void main(String[] args) {
		File file = new File("src/temp/icons.xml");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String line;
			while ((line = reader.readLine()) != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("\"").append(line).append("\"").append(" + ");
				System.out.println(sb.toString());
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}