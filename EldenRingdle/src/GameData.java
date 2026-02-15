import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameData {

    // The Character Class
    public static class EldenChar {
        String name, gender, race, region, affiliation, combatType;

        public EldenChar(String n, String g, String r, String reg, String aff, String c) {
            this.name = n; this.gender = g; this.race = r;
            this.region = reg; this.affiliation = aff; this.combatType = c;
        }
        public Object[] toRow() {
            return new Object[]{name, gender, race, region, affiliation, combatType};
        }
    }
    public static class Quote {
        String text, author;
        public Quote(String t, String a) { this.text = t; this.author = a; }

        public Object[] toRow() {
            return new Object[]{text, author};
        }
    }
    public static List<EldenChar> characterList = new ArrayList<>();
    public static List<Quote> quoteList = new ArrayList<>();
    static {
        // Characters
        characterList.add(new EldenChar("Malenia", "Female", "Demigod", "Haligtree", "Rot", "Hybrid"));
        characterList.add(new EldenChar("Radahn", "Male", "Demigod", "Caelid", "Golden Order", "Hybrid"));
        characterList.add(new EldenChar("Godrick", "Male", "Demigod", "Limgrave", "Golden Order", "Melee"));
        characterList.add(new EldenChar("Ranni", "Female", "Empyrean", "Liurnia", "Carian", "Magic"));
        characterList.add(new EldenChar("Morgott", "Male", "Omen", "Leyndell", "Golden Order", "Hybrid"));
        characterList.add(new EldenChar("Rennala", "Female", "Human", "Liurnia", "Carian", "Magic"));
        characterList.add(new EldenChar("Maliketh", "Male", "Beast", "Farum Azula", "Golden Order", "Melee"));
        characterList.add(new EldenChar("Mohg", "Male", "Omen", "Underground", "Blood", "Magic"));

        // Quotes
        quoteList.add(new Quote("I am Malenia, Blade of Miquella.", "Malenia"));
        quoteList.add(new Quote("Put these foolish ambitions to rest.", "Margit"));
        quoteList.add(new Quote("Forefathers, one and all... Bear witness!", "Godrick"));
        quoteList.add(new Quote("...Togethaa! We will devour the very gods!", "Rykard"));
    }

    // Autocomplete
    public static List<String> getUniqueValues(String category) {
        Set<String> values = new HashSet<>();
        for (EldenChar c : characterList) {
            switch (category) {
                case "Name": values.add(c.name); break;
                case "Race": values.add(c.race); break;
                case "Region": values.add(c.region); break;
                case "Affiliation": values.add(c.affiliation); break;
                case "Type": values.add(c.combatType); break;
            }
        }
        return new ArrayList<>(values);
    }
}