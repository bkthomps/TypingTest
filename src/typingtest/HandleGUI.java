package typingtest;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Creates and updates the GUI. The application is vertically split into six sections. The first line of text, the
 * second line of text, the line of text that the user types in, the statistics of the current round, the statistics
 * of the latest round, and the statistics of the highest score.
 */
class HandleGUI {

    private static final String NAME = "Typing Test";
    private static final ImageIcon ICON = new ImageIcon("TypingTest.png");
    private static final String SPACE = "      ";
    private static final String BIG_SPACE = SPACE + SPACE + SPACE;
    private static final String ERROR_MESSAGE = SPACE + "Error!";

    private static final JFrame frame = new JFrame(NAME);
    private static final JLabel topWords = new JLabel(ERROR_MESSAGE);
    private static final JLabel botWords = new JLabel(ERROR_MESSAGE);
    private static final JLabel currentWord = new JLabel();
    private static final JLabel timeLeft = new JLabel(ERROR_MESSAGE);
    private static final JLabel latestScore = new JLabel(ERROR_MESSAGE);
    private static final JLabel highestScore = new JLabel(ERROR_MESSAGE);

    private static final String[] WORD_DATABASE = SaveOrLoad.loadDatabase();

    private static long startTime;
    private static boolean gameInProgress;

    void createGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(ICON.getImage());
        frame.setVisible(true);

        frame.add(topWords);
        frame.add(botWords);
        frame.add(currentWord);
        frame.add(timeLeft);
        frame.add(latestScore);
        frame.add(highestScore);
        frame.setLayout(new GridLayout(6, 1, 0, 0));
        frame.requestFocus();

        createDatabase();
        updateDisplayWords();
        updateDisplayWords();
        keyPress();
    }

    private void keyPress() {
        frame.addKeyListener(new KeyListener() {
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
            final int NANOSECONDS_IN_SECOND = 1000000000;
            Data.time = (int) ((System.nanoTime() - startTime) / NANOSECONDS_IN_SECOND);
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
        currentWord.setText(BIG_SPACE + BIG_SPACE + Data.currentWord);
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
        topWords.setText(topTempSentence);
        botWords.setText(botTempSentence);
    }

    private String genNewWord() {
        final int MIN = 0;
        final int MAX = WORD_DATABASE.length - 1;
        final int INDEX = (int) (Math.random() * (MAX - MIN + 1)) + MIN;
        return WORD_DATABASE[INDEX];
    }

    private void resizeCurrentTestDisplay() {
        String botTempSentence = SPACE;
        for (int i = Data.wordIndex; i < Data.WORDS_PER_LINE; i++) {
            botTempSentence += Data.botWords[i] + " ";
        }
        botWords.setText(botTempSentence);
    }

    private void updateStats() {
        int timeLeftInRound = Data.TIME_IN_SECONDS_PER_ROUND - Data.time;
        if (timeLeftInRound < 0) {
            timeLeftInRound = 0;
        }
        timeLeft.setText(SPACE + "Time Left:  " + timeLeftInRound + BIG_SPACE + "Words:  " + Data.rightWords);
        latestScore.setText(SPACE + "Last WPM:  " + Data.lastWPM + BIG_SPACE + "Last CPM:  " + Data.lastCPM);
        highestScore.setText(SPACE + "High WPM:  " + Data.highWPM + BIG_SPACE + "High CPM:  " + Data.highCPM);
    }
}
