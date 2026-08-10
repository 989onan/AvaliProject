package com.lunkoashtail.avaliproject.screen.custom;

import net.minecraft.util.RandomSource;

import java.util.List;

public class AvaliSocialLines {
    public static final List<String> TALK = List.of(
            "Tells you about the last hunt they were on.",
            "Chirps thoughtfully and asks how your day's going.",
            "Shares a quiet story about their old flock.",
            "Nods along, genuinely interested in what you have to say."
    );

    public static final List<String> GOSSIP = List.of(
            "Leans in and whispers something about another pack member.",
            "Says you didn't hear this from them, but...",
            "Trades a bit of camp gossip with a sly chitter.",
            "Raises an eyebrow-ridge and shares some juicy news."
    );

    public static final List<String> BE_RUDE = List.of(
            "Recoils, feathers puffing up in irritation.",
            "Gives you a sharp, offended chirp and turns away.",
            "Narrows their eyes at you. That was uncalled for.",
            "Lets out an annoyed hiss and takes a step back."
    );

    public static final List<String> FLIRT = List.of(
            "Ruffles their feathers and looks away, flustered.",
            "Lets out an embarrassed chirp, tail flicking rapidly.",
            "Tries to act casual, but their crest feathers give them away.",
            "Stammers a reply, clearly caught off guard."
    );

    public static final List<String> PLAY = List.of(
            "Bounces excitedly and play-chases you in a circle.",
            "Pounces at your feet, clearly enjoying themselves.",
            "Chirrups happily and darts back and forth.",
            "Tackles you playfully, tail wagging."
    );

    public static final List<String> JOKE = List.of(
            "Laughs - a good one!",
            "Groans at the pun, but can't help chittering with laughter anyway.",
            "Snorts and shakes their head, grinning.",
            "Tells one right back, even worse than yours."
    );

    public static final List<String> HUG = List.of(
            "Wraps their arms around you, feathers warm and soft.",
            "Nuzzles into the hug, clearly not expecting it.",
            "Squeaks in surprise, then hugs back tightly.",
            "Goes still for a moment, then hugs back, a little embarrassed."
    );

    public static final List<String> FLIRT_TOO_SOON = List.of(
            "Takes a step back, clearly uncomfortable. You barely know each other.",
            "Gives you a wary look. That felt a bit forward.",
            "Laughs it off awkwardly and changes the subject.",
            "Frowns slightly - too soon for that."
    );

    public static final List<String> FAILED = List.of(
            "Seems distracted and doesn't really react.",
            "Just shrugs, not really in the mood.",
            "Gives a noncommittal chirp and looks away.",
            "Doesn't seem to catch it this time."
    );

    public static String random(List<String> lines, RandomSource random) {
        return lines.get(random.nextInt(lines.size()));
    }
}
