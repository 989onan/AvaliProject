package com.lunkoashtail.avaliproject.entity.custom;

import net.minecraft.util.RandomSource;

final class AvaliNames {
    private AvaliNames() {
    }

    private static final String[] FIRST_NAMES = {
            "Eikuni", "Einuni", "Jasko", "Tasako", "Jesuli",
            "Nali", "Runo", "Halun", "Nauko", "Khauni", "Kazun", "Jasun",
            "Rhauni", "Kala", "Ranani", "Eijani", "Nuko", "Kalali", "Eili",
            "Rala", "Halun", "Nezumi", "Romi", "Eikuno", "Kasumi",
            "Renuno", "Kala", "Khaan", "Nahomi", "Eito", "Eijomi",
            "Renomi", "Romi", "Naomi", "Rhauli", "Jesumi", "Eikyo",
            "Kali", "Nali", "Renii", "Rali", "Einala", "Ruli", "Rali", "Nezyo",
            "Kasaku", "Rani", "Rhaoli", "Einali", "Ryu", "Eino", "Jesan",
            "Nezomi", "Jasi", "Kali", "Ranuni", "Ranan", "Talomi", "Kuli",
            "Eijuli", "Kasa", "Reno", "Talyu", "Nako", "Ranami", "Khaali",
            "Nesun", "Kaso", "Jesami", "Eito", "Nezumi", "Jasoli", "Rako",
            "Rhaun", "Naumi", "Rhaoli", "Rhaaku", "Ranuno", "Rano",
            "Eilan", "Nauli", "Eikaka", "Rali", "Ialii", "Eikuko", "Renuli",
            "Rhaii", "Raluni", "Nzuno", "Jasuli", "Ranuni", "Ralako",
            "Jesaka", "Halaku", "Renan", "No", "Eitala", "Ruli", "Nahuko",
            "Rumi", "Renani", "Taluko", "Eikomi", "Khaumi", "Renani",
            "Taluko", "Eitali", "Eitali", "Eikali", "Eityu", "Nauno", "Jasoli",
            "Kalali", "Ralun", "Jesyo", "Nai", "Kasoli", "Hala", "Kyo",
            "Kalumi", "Jasomi", "Eitun", "Hali", "Taloli", "Ran", "Jesali",
            "Renuli", "Kasyu", "Eilyo", "Nahumi", "Kala", "Kuko", "Talaka",
            "Talo", "Eityu", "Kan", "Zexii", "Khauko", "Eilun", "Renyu",
            "Kazan", "Raloli", "Nezyo", "Jasuko", "Nami", "Kasyu", "Rana",
            "Eitii", "Kalani", "Naka", "Nezo", "Halaka", "Tali", "Nomi",
            "Eijuno", "Rhao", "Naako", "Eikako", "Halami", "Eiki",
            "Nezako", "Khao", "Khauno", "Jesaku", "Kaluni", "Rhaa",
            "Haluli", "Raloli", "Numi", "Talako", "Jasoli", "Kasala",
            "Khaumi", "Ka", "Kan", "Rhaomi", "Talun", "Halala", "Koli",
            "Einjuni", "Kazyo", "Naii", "Eitumi", "Nahyu", "Nahuni",
            "Kazii", "Nahaku", "Eilaku", "Nahyu", "Nahuni", "Eijuli",
            "Kazuli", "Nasala", "Rhao", "Jesoli", "Nahani", "Nan", "Kalo",
            "Rhaako", "Jasyu", "Eitami", "Eita", "Nezi", "Kazo", "Jasa",
            "Nahomi", "Nezyo", "Nuno", "Ru", "Kun", "Talan", "Naaku",
            "Zieh"
    };

    private static final String[] LAST_NAMES = {
            "Mineclaw", "Ironclaw", "Irontalon",
            "Darkfeathers", "Silverfeather", "Hatchguard",
            "Hunter", "Aviator", "Sprinter"
    };

    static String random(RandomSource random) {
        String first = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String last = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return first + " " + last;
    }
}
