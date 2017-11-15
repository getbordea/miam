/**
 * 
 */
package fr.ubx.bph.erias.miam.drug;

/**
 * @author Georgeta Bordea
 *
 */
public class EnglishDBpediaDrugExtractor extends DBpediaDrugExtractor {

  public final String DCT_PREFIX = "dct";

  public String[] STOP_WORDS = { "industry", "associations", "education",
      "fictional", "statistics", "production", "people", "doping", "dosage",
      "research", "technology", "technologies", "manufacturing", "training",
      "education", "disease", "eruption", "controversies", "controversy",
      "withdrawn", "veterinary", "eradication", "scandal", "discovery",
      "development", "abandoned", "song", "cartel", "film", "list_of",
      "lists_of", "_lists", "sensitivity", "administration", "effects",
      "contamination", "testing", "program", "agencies", "regulation",
      "national", "safety", "availability", "driving", "recreational",
      "deaths_by", "season", "episode", "characters", "legal_status", "history",
      "culture", "dependence", "_myth", "_fungi", "therapies", "therapy",
      "related_deaths", "illegal", "drug_war", "smoking", "alcohol_in",
      "drugs_in", "alcoholic_drinks", "distilled_drinks", "nightclub",
      "law_firms", "biological_sources", "albums", "music", "works_about",
      "addiction", "alcohol_by", "tobacco_by", "policy", "case_law",
      "legislation", "controlled_substances", "politicians", "prohibition",
      "alcohol_law", "county", "buildings", "structures", "_abuse",
      "alcoholics", "rehabilitation", "television", "related_deaths",
      "activists", "drug_control", "by_country"  };

  public String[] STOP_CATEGORIES = { "pharmacokinetics", "pharmacodynamics",
      "medicinal_plants", "medicinal_fungi", "breaking_bad",
      "medicinal_herbs_and_fungi"};

  public String[] LEAF_CATEGORIES = { "tea", "alcohol", "opium", "garlic",
      "caffeine", "tobacco", "cannabis", "cocaine", "coffee" };
}
