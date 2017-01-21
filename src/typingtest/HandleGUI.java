package typingtest;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Creates and updates the GUI.
 */
public class HandleGUI {

    private static final String SPACE = "      ";
    private static final String BIG_SPACE = SPACE + SPACE + SPACE;
    private static final String ERROR_MESSAGE = SPACE + "Error!";

    private static final String NAME = "Typing Test";
    private static final ImageIcon ICON = new ImageIcon("TypingTest.png");

    private static final JFrame FRAME = new JFrame(NAME);
    private static final JLabel TOP_WORDS = new JLabel(ERROR_MESSAGE);
    private static final JLabel BOT_WORDS = new JLabel(ERROR_MESSAGE);
    private static final JLabel CURRENT_WORD = new JLabel();
    private static final JLabel TIME_LEFT = new JLabel(ERROR_MESSAGE);
    private static final JLabel LATEST_SCORE = new JLabel(ERROR_MESSAGE);
    private static final JLabel HIGHEST_SCORE = new JLabel(ERROR_MESSAGE);

    private static final String[] WORD_DATABASE = SaveOrLoad.loadDatabase();

    static long startTime;
    static boolean gameInProgress;

    public void createGUI() {
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setResizable(false);
        FRAME.setSize(400, 400);
        FRAME.setLocationRelativeTo(null);
        FRAME.setIconImage(ICON.getImage());
        FRAME.setVisible(true);

        FRAME.add(TOP_WORDS);
        FRAME.add(BOT_WORDS);
        FRAME.add(CURRENT_WORD);
        FRAME.add(TIME_LEFT);
        FRAME.add(LATEST_SCORE);
        FRAME.add(HIGHEST_SCORE);
        FRAME.setLayout(new GridLayout(6, 1, 0, 0));
        FRAME.requestFocus();

        createDatabase();
        updateDisplayWords();
        updateDisplayWords();
        keyPress();
    }

    private void keyPress() {
        FRAME.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                updateUserWord(e.getKeyCode());
            }

            @Override
            public void keyTyped(KeyEvent e) {
                //does nothing
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //does nothing
            }
        });
    }

    private void updateUserWord(int wordVal) {
        if (!gameInProgress) {
            gameInProgress = true;
            startTime = System.nanoTime();
        } else {
            Data.time = (int) ((System.nanoTime() - startTime) / 1000000000);
        }
        if (wordVal == 32) {
            if (Data.currentWord.equals(Data.botWords[Data.wordIndex])) {
                Data.rightWords++;
            } else if (Data.rightWords > 0) {
                Data.rightWords--;
            }
            Data.currentWord = "";
            if (Data.wordIndex < Data.WORDS_PER_LINE - 1) {
                Data.wordIndex++;
            } else {
                updateDisplayWords();
                Data.wordIndex = 0;
            }
            resizeCurrentTestDisplay();

            if (Data.time >= Data.TIME_IN_SECONDS_PER_ROUND) {
                gameInProgress = false;
                Data.resetValues();
                updateDisplayWords();
                updateDisplayWords();
            }
        } else if (wordVal == 8 && !"".equals(Data.currentWord)) {
            Data.currentWord = Data.currentWord.substring(0, Data.currentWord.length() - 1);
        } else if (wordVal >= 65 && wordVal <= 90) {
            Data.currentWord += (char) (wordVal + 32);
        } else if (wordVal >= 97 && wordVal <= 122) {
            Data.currentWord += (char) wordVal;
        }

        if (Data.lastWPM > Data.highWPM) {
            Data.highWPM = Data.lastWPM;
            Data.highCPM = Data.lastCPM;
        }
        SaveOrLoad.save();
        updateStats();
        CURRENT_WORD.setText(BIG_SPACE + BIG_SPACE + Data.currentWord);
    }

    private void createDatabase() {
        int tempVals[] = SaveOrLoad.load();
        Data.highCPM = tempVals[0];
        Data.highWPM = tempVals[1];
        Data.lastCPM = tempVals[2];
        Data.lastWPM = tempVals[3];

        updateStats();
    }

    private void updateDisplayWords() {
        String topTempSentence = SPACE;
        String botTempSentence = SPACE;

        for (int i = 0; i < Data.WORDS_PER_LINE; i++) {
            Data.botWords[i] = Data.topWords[i];
            Data.topWords[i] = genNewWord();
            topTempSentence += Data.topWords[i] + " ";
            botTempSentence += Data.botWords[i] + " ";
        }

        TOP_WORDS.setText(topTempSentence);
        BOT_WORDS.setText(botTempSentence);
    }

    private String genNewWord() {
        final int min = 0;
        final int max = WORD_DATABASE.length - 1;

        int index = (int) (Math.random() * (max - min + 1)) + min;
        return WORD_DATABASE[index];
    }

    private void resizeCurrentTestDisplay() {
        String botTempSentence = SPACE;

        for (int i = Data.wordIndex; i < Data.WORDS_PER_LINE; i++) {
            botTempSentence += Data.botWords[i] + " ";
        }

        BOT_WORDS.setText(botTempSentence);
    }

    private void updateStats() {
        int timeLeftInRound = Data.TIME_IN_SECONDS_PER_ROUND - Data.time;
        if (timeLeftInRound < 0) {
            timeLeftInRound = 0;
        }
        TIME_LEFT.setText(SPACE + "Time Left:  " + timeLeftInRound + BIG_SPACE + "Words:  " + Data.rightWords);
        LATEST_SCORE.setText(SPACE + "Last WPM:  " + Data.lastWPM + BIG_SPACE + "Last CPM:  " + Data.lastCPM);
        HIGHEST_SCORE.setText(SPACE + "High WPM:  " + Data.highWPM + BIG_SPACE + "High CPM:  " + Data.highCPM);
    }
}
