import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by qixinzhu on 10/1/16.
 */
public class WordBank {
    protected Queue<Word> words;
    protected int remainChar;

    public WordBank() {
        words = new PriorityQueue<>();
        remainChar = 0;
    }
/*
    public WordBank(WordBank other, GameBoard g) {
        remainChar = other.remainChar;
        this.words = new PriorityQueue<>();
        while (!other.words.isEmpty()) {
            Word w = other.words.poll();
            Word newWord = new Word(w.word, g);
            words.add(newWord);
        }
    }
*/
    public WordBank(WordBank other, GameBoard g) {
        this.words = new PriorityQueue<>();
        for (Word w : other.words) {
            Word newWord = new Word(w.word, g);
            words.add(newWord);
        }
        remainChar = other.remainChar;
    }

    public WordBank(String fileName, GameBoard g) throws FileNotFoundException {
        this();
        File inputFile = new File(fileName);
        Scanner in = new Scanner(inputFile);
        while (in.hasNextLine()) {
            String s = in.nextLine();
            Word w = new Word(s, g);
            words.add(w);
            remainChar += s.length();
        }
        in.close();
        System.out.println(this.toString());
    }

    public Word getNextWord() {
        Word w = words.poll();
        if (w != null) remainChar -= w.word.length();
        return w;
    }

    public boolean isEmpty() {
        return words.size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Word w : words) {
            sb.append(w);
            sb.append('\n');
        }
        return sb.toString();
    }

    public void removeWord(String word) {
        for (Word w : new PriorityQueue<>(words)) {
            if (w.word.equals(word)) words.remove(w);
        }
    }
}
