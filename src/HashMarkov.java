import java.util.*;

public class HashMarkov implements MarkovInterface {
    protected String[] myWords; 
	protected Random myRandom;
	protected int myOrder;
    protected HashMap<WordGram, List<String>> myMap = new HashMap<>();

    public HashMarkov(int order){
		myOrder = order;
		myRandom = new Random();
	}

    public void setTraining(String text){
		myWords = text.split("\\s+");
        myMap.clear();

        for (int i = 0; i < myWords.length - myOrder; i++) {
            WordGram other = new WordGram(myWords, i, myOrder);
            ArrayList<String> list = new ArrayList<>();
            if (!myMap.containsKey(other)) {
                myMap.put(other, list);
            }
            List<String> finalList = myMap.get(other);
            finalList.add(myWords[i + myOrder]);
            myMap.put(other, finalList);
        }
	}

    public List<String> getFollows(WordGram wgram) {
		if (!myMap.containsKey(wgram)) {
            ArrayList<String> empty = new ArrayList<>();
            return empty;
        }
        else {
            return myMap.get(wgram);
        }
	}

    public String getRandomText(int length){
		ArrayList<String> randomWords = new ArrayList<>(length);
		int index = myRandom.nextInt(myWords.length - myOrder + 1);
		WordGram current = new WordGram(myWords,index,myOrder);
		randomWords.add(current.toString());

		for(int k=0; k < length-myOrder; k++) {
            List<String> follows = getFollows(current);
            if (follows.size() == 0) {
                break;
            }
            index = myRandom.nextInt(follows.size());
            String nextWord = follows.get(index);
            if (nextWord.equals("")) {
                break;
            }
            randomWords.add(nextWord);
            current = current.shiftAdd(nextWord);
		}
		return String.join(" ", randomWords);
	}

    public int getOrder() {
		return myOrder;
	}

    public void setSeed(long seed) {
		myRandom.setSeed(seed);
	}
}
