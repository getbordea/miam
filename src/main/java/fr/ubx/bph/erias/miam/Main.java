package fr.ubx.bph.erias.miam;

import fr.ubx.bph.erias.miam.food.DBpediaFoodExtractor;

public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DBpediaFoodExtractor dfe = new DBpediaFoodExtractor();
		dfe.downloadPageHTML("http://dbpedia.org/page/Category:Foods");
	}
}