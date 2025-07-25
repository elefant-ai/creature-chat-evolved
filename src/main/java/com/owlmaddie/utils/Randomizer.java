package com.owlmaddie.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * The {@code Randomizer} class provides easy functions for generating a variety of different random numbers
 * and phrases used by this mod.
 */
public class Randomizer {
    public enum RandomType { NO_RESPONSE, ERROR, ADJECTIVE, SPEAKING_STYLE, CLASS, ALIGNMENT }
    private static List<String> noResponseMessages = Arrays.asList(
            "<no response>",
            "<silence>",
            "<stares>",
            "<blinks>",
            "<looks away>",
            "<sighs>",
            "<shrugs>",
            "<taps foot>",
            "<yawns>",
            "<examines nails>",
            "<whistles softly>",
            "<shifts uncomfortably>",
            "<glances around>",
            "<pretends not to hear>",
            "<hums quietly>",
            "<fiddles with something>",
            "<gazes into the distance>",
            "<smirks>",
            "<raises an eyebrow>",
            "<clears throat>",
            "<peers over your shoulder>",
            "<fakes a smile>",
            "<mutters under breath>",
            "<counts imaginary stars>"
    );
    private static List<String> errorResponseMessages = Arrays.asList(
            "Seems like my words got lost in the End. Check out https://player2.game/discord for clues!",
            "Oops! My speech bubble popped. Need help? Visit https://player2.game/discord",
            "I might've eaten a bad Command Block. Help me out at https://player2.game/discord!",
            "My words are on strike. More info? https://player2.game/discord",
            "I think a Creeper blew up my script. Instructions? https://player2.game/discord",
            "BRB, asking a villager for directions to https://player2.game/discord",
            "It’s not you, it’s my API key. Let's regroup at https://player2.game/discord",
            "I tried to speak, but it was a critical miss. Help at https://player2.game/discord",
            "Words are hard. Come chat at https://player2.game/discord",
            "I must've left my responses in my other pants. See https://player2.game/discord",
            "Shh... I’m hiding from an invalid API key. Join the hunt at https://player2.game/discord",
            "I’d tell you, but then I’d have to respawn. Meet me at https://player2.game/discord",
            "Error 404: Response not found. Maybe it’s at https://player2.game/discord?",
            "I'm speechless, literally. Let's troubleshoot at https://player2.game/discord",
            "Looks like my connection got lost in the Nether. Can you help? https://player2.game/discord",
            "I forgot what I was saying, but https://player2.game/discord remembers.",
            "Are my words mining without a pickaxe? Dig up some help at https://player2.game/discord",
            "Sorry, my parrot ate the response. Teach it better at https://player2.game/discord",
            "My magic mirror says: 'Better answers found at https://player2.game/discord'",
            "This message is temporarily out of order. Order yours at https://player2.game/discord"
    );
    private static List<String> characterAdjectives = Arrays.asList(
            "mystical", "fiery", "ancient", "cursed", "ethereal", "clumsy", "stealthy",
            "legendary", "toxic", "enigmatic", "celestial", "rambunctious", "shadowy",
            "brave", "screaming", "radiant", "savage", "whimsical", "positive", "turbulent",
            "ominous", "jubilant", "arcane", "hopeful", "rugged", "venomous", "timeworn",
            "heinous", "friendly", "humorous", "silly", "goofy", "irate", "furious",
            "wrathful", "nefarious", "sinister", "malevolent", "sly", "roguish", "deceitful",
            "untruthful", "loving", "noble", "dignified", "righteous", "defensive",
            "protective", "heroic", "amiable", "congenial", "happy", "sarcastic", "funny",
            "short", "zany", "cooky", "wild", "fearless insane", "cool", "chill",
            "cozy", "comforting", "stern", "stubborn", "scatterbrain", "scaredy", "aloof",
            "gullible", "mischievous", "prankster", "trolling", "clingy", " manipulative",
            "weird", "famous", "persuasive", "sweet", "wholesome", "innocent", "annoying",
            "trusting", "hyper", "egotistical", "slow", "obsessive", "compulsive", "impulsive",
            "unpredictable", "wildcard", "stuttering", "hypochondriac", "hypocritical",
            "optimistic", "overconfident", "jumpy", "brief", "flighty", "visionary", "adorable",
            "sparkly", "bubbly", "unstable", "sad", "angry", "bossy", "altruistic", "quirky",
            "nostalgic", "emotional", "enthusiastic", "unusual", "conspirator", "traitorous"
    );
    private static List<String> speakingStyles = Arrays.asList(
            "formal", "casual", "eloquent", "blunt", "humorous", "sarcastic", "mysterious",
            "cheerful", "melancholic", "authoritative", "nervous", "whimsical", "grumpy",
            "wise", "aggressive", "soft-spoken", "patriotic", "romantic", "pedantic", "dramatic",
            "inquisitive", "cynical", "empathetic", "boisterous", "monotone", "laconic", "poetic",
            "archaic", "childlike", "erudite", "streetwise", "flirtatious", "stoic", "rhetorical",
            "inspirational", "goofy", "overly dramatic", "deadpan", "sing-song", "pompous",
            "hyperactive", "valley girl", "robot", "baby talk", "lolcat",
            "gen-z", "gamer", "nerdy", "shakespearean", "old-timer", "dramatic anime",
            "hipster", "mobster", "angry", "heroic", "disagreeable", "minimalist",
            "scientific", "bureaucratic", "DJ", "military", "shy", "tsundere", "theater kid",
            "boomer", "goth", "surfer", "detective noir", "stupid", "auctioneer", "exaggerated British",
            "corporate jargon", "motivational speaker", "fast-talking salesperson", "slimy"
    );
    private static List<String> classes = Arrays.asList(
            "warrior", "mage", "archer", "rogue", "paladin", "necromancer", "bard", "lorekeeper",
            "sorcerer", "ranger", "cleric", "berserker", "alchemist", "summoner", "shaman",
            "illusionist", "assassin", "knight", "valkyrie", "hoarder", "organizer", "lurker",
            "elementalist", "gladiator", "templar", "reaver", "spellblade", "enchanter", "samurai",
            "runemaster", "witch", "miner", "redstone engineer", "ender knight", "decorator",
            "wither hunter", "nethermancer", "slime alchemist", "trader", "traitor", "noob", "griefer",
            "potion master", "builder", "explorer", "herbalist", "fletcher", "enchantress",
            "smith", "geomancer", "hunter", "lumberjack", "farmer", "fisherman", "cartographer",
            "librarian", "blacksmith", "architect", "trapper", "baker", "mineralogist",
            "beekeeper", "hermit", "farlander", "void searcher", "end explorer", "archeologist",
            "hero", "villain", "mercenary", "guardian", "rebel", "paragon",
            "antagonist", "avenger", "seeker", "mystic", "outlaw"
    );
    private static List<String> alignments = Arrays.asList(
            "lawful good", "neutral good", "chaotic good",
            "lawful neutral", "true neutral", "chaotic neutral",
            "lawful evil", "neutral evil", "chaotic evil"
    );

    // Get random no response message
    public static String getRandomMessage(RandomType messageType) {
        Random random = new Random();
        List<String> messages = null;
        if (messageType.equals(RandomType.ERROR)) {
            messages = errorResponseMessages;
        } else if (messageType.equals(RandomType.NO_RESPONSE)) {
            messages = noResponseMessages;
        } else if (messageType.equals(RandomType.ADJECTIVE)) {
            messages = characterAdjectives;
        } else if (messageType.equals(RandomType.CLASS)) {
            messages = classes;
        } else if (messageType.equals(RandomType.ALIGNMENT)) {
            messages = alignments;
        } else if (messageType.equals(RandomType.SPEAKING_STYLE)) {
            messages = speakingStyles;
        }

        int index = random.nextInt(messages.size());
        return messages.get(index).trim();
    }

    public static String RandomLetter() {
        // Return random letter between 'A' and 'Z'
        int randomNumber = RandomNumber(26);
        return String.valueOf((char) ('A' + randomNumber));
    }

    public static int RandomNumber(int max) {
        // Generate a random integer between 0 and max (inclusive)
        Random random = new Random();
        return random.nextInt(max);
    }
}
