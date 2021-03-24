package be.ledio.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EUConstants {

	public final static String US = "US";

	public final static Map<String, String> mapOfEUCountry = new HashMap<String, String>() {
		{
			put("AT", "Austria");
			put("BE", "Belgium");
			put("BG", "Bulgaria");
			put("CY", "Cyprus");
			put("CZ", "Czech Republic");
			put("DE", "Germany");
			put("DK", "Denmark");
			put("EE", "Estonia");
			put("ES", "Spain");
			put("FI", "Finland");
			put("FR", "France");
			put("GR", "Greece");
			put("HR", "Croatia");
			put("HU", "Hunagary");
			put("IE", "Ireland");
			put("IT", "Italy");
			put("LT", "Lithuania");
			put("LU", "Luxembourg");
			put("LV", "Latvia");
			put("MT", "Malta");
			put("NL", "Netherlands");
			put("PO", "Poland");
			put("PT", "Portugal");
			put("RO", "Romania");
			put("SE", "Sweden");
			put("SI", "Slovenia");
			put("SK", "Slovakia");
		}
	};

	public final static List<String> listOfEUCountryCode = new ArrayList<>(mapOfEUCountry.keySet());
	public final static List<String> listOfEUCountryName = new ArrayList<>(mapOfEUCountry.values());

}
