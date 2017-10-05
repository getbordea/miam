/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Georgeta Bordea
 *
 */
public class DBpediaFoodExtractor {

	public void downloadPageHTML(String seedPageURI) {
		Document doc;
		Elements narrowerCategoryElements;
		try {
			doc = Jsoup.connect(seedPageURI).get();
			narrowerCategoryElements = doc.body().getElementsByAttributeValue("rev", "skos:broader");

			for (Element element : narrowerCategoryElements) {
				System.out.print(element.text() + " ");
				System.out.println(element.attr("href"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
