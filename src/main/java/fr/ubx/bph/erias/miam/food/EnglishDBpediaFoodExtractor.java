/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Georgeta Bordea
 *
 */
public class EnglishDBpediaFoodExtractor extends DBpediaFoodExtractor {

  public final String DCT_PREFIX = "dct";

  public String[] STOP_WORDS = { "production", "people", "industry", "disease",
      "manufacturer", "companies", "company", "restaurant", "science",
      "bakeries", "farming", "pubs_", "_pubs", "distilleries", "distillery",
      "history", "films", "organisations", "breeds", "music", "plantations",
      "refineries", "organizations", "fictional", "chains", "cultivation_in",
      "regions", "crops", "breweries", "brewing", "brewers", "wineries",
      "vineyards", "festivals", "viticultural", "beer_by", "beer_in",
      "french_wine", "coats_of_arms", "founders", "franchises", "chefs",
      "pizzerias", "pet_food", "shops", "parlors", "retailers", "cheesemakers",
      "characters", "food_truck", "episodes", "ice_cream_vans", "orchardists",
      "economy", "houses", "pathogens", "geographical_indications", "studio",
      "trade", "standards", "campaigns", "litigation", "player", "spots",
      "haze", "crisis", "scandal", "popular_culture", "flour_mills",
      "criticism", "books", "list_of", "lists_of", "brand", "producer",
      "video_game", "tv_series", "theory", "logos"};

  public String[] STOP_CATEGORIES =
      { "carnivory", "alcoholic_drink_brands", "cherry_blossom", "halophiles",
          "forages", "decorative_fruits_and_seeds" };

  public String[] LEAF_CATEGORIES = { "wine", "beer", "whisky", "whiskey",
      "rubus", "onions", "table_grape_varieties", "grape_varieties", "quails",
      "grouse", "geese", "swans", "ducks" };
}
