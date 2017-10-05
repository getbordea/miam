/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Georgeta Bordea
 *
 */
public class DBpediaFoodExtractor {

	public void downloadPage(String seedPageURI) {
		Document doc;
		try {
			doc = Jsoup.connect(seedPageURI).get();
	        String text = doc.body().text();

	        System.out.print(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
