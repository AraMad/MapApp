package mapapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arina on 01.02.2017.
 */

public class NameCreator {

    private static final Map<Character, String> charMap = new HashMap<Character, String>();

    static {
        charMap.put('А', "A");
        charMap.put('Б', "B");
        charMap.put('В', "V");
        charMap.put('Г', "G");
        charMap.put('Ґ', "G");
        charMap.put('Д', "D");
        charMap.put('Е', "E");
        charMap.put('Є', "Ye");
        charMap.put('Ж', "Zh");
        charMap.put('З', "Z");
        charMap.put('И', "Yy");
        charMap.put('І', "I");
        charMap.put('Ї', "I");
        charMap.put('Й', "I");
        charMap.put('К', "K");
        charMap.put('Л', "L");
        charMap.put('М', "M");
        charMap.put('Н', "N");
        charMap.put('О', "O");
        charMap.put('П', "P");
        charMap.put('Р', "R");
        charMap.put('С', "S");
        charMap.put('Т', "T");
        charMap.put('У', "U");
        charMap.put('Ф', "F");
        charMap.put('Х', "Kh");
        charMap.put('Ц', "Ts");
        charMap.put('Ч', "Ch");
        charMap.put('Ш', "Sh");
        charMap.put('Щ', "Shch");
        charMap.put('Ю', "Yu");
        charMap.put('Я', "Ya");

        charMap.put('а', "a");
        charMap.put('б', "b");
        charMap.put('в', "v");
        charMap.put('г', "g");
        charMap.put('ґ', "g");
        charMap.put('д', "d");
        charMap.put('е', "e");
        charMap.put('є', "ie");
        charMap.put('ж', "zh");
        charMap.put('з', "z");
        charMap.put('и', "y");
        charMap.put('і', "i");
        charMap.put('ї', "i");
        charMap.put('й', "i");
        charMap.put('к', "k");
        charMap.put('л', "l");
        charMap.put('м', "m");
        charMap.put('н', "n");
        charMap.put('о', "o");
        charMap.put('п', "p");
        charMap.put('р', "r");
        charMap.put('с', "s");
        charMap.put('т', "t");
        charMap.put('у', "u");
        charMap.put('ф', "f");
        charMap.put('х', "kh");
        charMap.put('ц', "ts");
        charMap.put('ч', "ch");
        charMap.put('ш', "sh");
        charMap.put('щ', "shch");
        charMap.put('ь', "");
        charMap.put('ю', "iu");
        charMap.put('я', "ia");

    }

    private String transliterate(String string) {
        StringBuilder transliteratedString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            Character ch = string.charAt(i);
            String charFromMap = charMap.get(ch);
            if (charFromMap == null) {
                transliteratedString.append(ch);
            } else {
                transliteratedString.append(charFromMap);
            }
        }
        return transliteratedString.toString();
    }

    public String takeCitName(String name){

        if (name.charAt(0) <= 127 && name.charAt(0) >= 0){
            if (name.equals("Kiev")){
                return "Kyiv";
            } else if (name.equals("Odessa")){
                return "Odesa";
            } else if (name.equals("Zaporizhia")){
                return "Zaporizhzhia";
            }
            return name;
        }
        return transliterate(name);
    }
}
