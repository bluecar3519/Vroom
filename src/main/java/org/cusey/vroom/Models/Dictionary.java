package org.cusey.vroom.Models;


import javax.persistence.*;
import java.util.Comparator;

@Entity(name="t_dictionary")
public class Dictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id ;
    private String word;
    private String partOfSpeech ;
    @Column(length=1000)
    private String definition;
    @Column(length=1000)
    private String wordUsage ;
    private int totalTryCount;
    private int rightCount;

    public Dictionary(){}

    public Dictionary(int id,String word, String partOfSpeech, String definition, String wordUsage, int totalTryCount, int rightCount) {
        this.id = id;
        this.word = word;
        this.partOfSpeech = partOfSpeech;
        this.definition = definition;
        this.wordUsage = wordUsage;
        this.totalTryCount = totalTryCount;
        this.rightCount = rightCount;
    }

    public int getId() {return id; }
    public void setId(int id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getPartOfSpeech() { return partOfSpeech; }
    public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    public String getWordUsage() { return wordUsage; }
    public void setWordUsage(String wordUsage) { this.wordUsage = wordUsage; }

    public int getTotalTryCount() { return totalTryCount; }
    public void setTotalTryCount(int totalTryCount) { this.totalTryCount = totalTryCount; }

    public int getRightCount() { return rightCount; }
    public void setRightCount(int rightCount) { this.rightCount = rightCount; }

    @Override
    public String toString() {
        return "Dictionary{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", partOfSpeech='" + partOfSpeech + '\'' +
                ", definition='" + definition + '\'' +
                ", wordUsage='" + wordUsage + '\'' +
                ", totalTryCount=" + totalTryCount +
                ", rightCount=" + rightCount +
                '}';
    }

    public static Comparator<Dictionary> WordComparator = new Comparator<Dictionary>(){

        public int compare (Dictionary d1, Dictionary d2){

            String word1 = d1.getWord().toUpperCase();
            String word2 = d2.getWord().toUpperCase();

            return word1.compareTo(word2);
        }
    };
}
