package typingtest;

/**
 * Special data type.
 */
class Data {

    private static final int CHAR_TO_WORD = 5;
    static final int WORDS_PER_LINE = 5;
    static final int TIME_IN_SECONDS_PER_ROUND = 60;

    static int rightWords;
    static int wordIndex;

    static final String[] topWords = new String[WORDS_PER_LINE];
    static final String[] botWords = new String[WORDS_PER_LINE];
    static String currentWord = "";
    static int time;
    static int lastCPM, lastWPM;
    static int highCPM, highWPM;

    private Data() {
        //not used
    }

    public static void resetValues() {
        lastWPM = rightWords * TIME_IN_SECONDS_PER_ROUND / 60;
        lastCPM = lastWPM * CHAR_TO_WORD;

        rightWords = 0;
        wordIndex = 0;

        for (int i = 0; i < WORDS_PER_LINE; i++) {
            topWords[i] = null;
            botWords[i] = null;
        }
        currentWord = "";
        time = 0;
    }
}
