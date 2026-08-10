package com.lunkoashtail.avaliproject.entity;

import java.util.List;

public class ExpieContextLines {
    public enum Context { MINING, CRAFTING, SLEEPING, SMELTING, NEARBY_MONSTER }

    public static final List<String> PLUSH_GIFT_LINES = List.of(
            "F-for me? Really...?",
            "I love it, I love it so much! Thank you!",
            "You're the best, I promise I'll take care of it!",
            "Thank you, thank you, thank you!",
            "I'll never let it go, I promise!",
            "This is the best thing anyone's ever given me..."
    );

    private static final List<String> MINING_CONTENT = List.of(
            "Ooh, what's that one do?",
            "You're really good at this!",
            "Careful down there!"
    );
    private static final List<String> MINING_ANXIOUS = List.of(
            "I- I don't like it down here...",
            "Is it much longer? Please?",
            "Something feels wrong about this cave..."
    );

    private static final List<String> CRAFTING_CONTENT = List.of(
            "Ooh, what are you making?",
            "You're so clever with your hands.",
            "Can I watch?"
    );
    private static final List<String> CRAFTING_ANXIOUS = List.of(
            "P-please don't leave once you're done...",
            "I just... like watching. It's calming.",
            "Are you almost finished?"
    );

    private static final List<String> SLEEPING_CONTENT = List.of(
            "Sleep well...",
            "I'll keep watch, don't worry.",
            "Sweet dreams."
    );
    private static final List<String> SLEEPING_ANXIOUS = List.of(
            "Please wake up soon...",
            "I'll just stay right here, okay?",
            "It's so quiet when you're asleep..."
    );

    private static final List<String> SMELTING_CONTENT = List.of(
            "It's warm over here.",
            "That smells like it's working!",
            "Ooh, shiny."
    );
    private static final List<String> SMELTING_ANXIOUS = List.of(
            "The fire's a little loud, isn't it...",
            "I- I'll just stand back a bit.",
            "Is it supposed to do that?"
    );

    private static final List<String> MONSTER_CONTENT = List.of(
            "Stay close, okay?",
            "I've got your back!",
            "Watch out, over there!"
    );
    private static final List<String> MONSTER_ANXIOUS = List.of(
            "T-there's something out there!",
            "Please, be careful, please...",
            "I don't like this, I don't like this at all..."
    );

    public static List<String> linesFor(Context context, boolean anxious) {
        return switch (context) {
            case MINING -> anxious ? MINING_ANXIOUS : MINING_CONTENT;
            case CRAFTING -> anxious ? CRAFTING_ANXIOUS : CRAFTING_CONTENT;
            case SLEEPING -> anxious ? SLEEPING_ANXIOUS : SLEEPING_CONTENT;
            case SMELTING -> anxious ? SMELTING_ANXIOUS : SMELTING_CONTENT;
            case NEARBY_MONSTER -> anxious ? MONSTER_ANXIOUS : MONSTER_CONTENT;
        };
    }
}
