package com.hazine.study;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordCounter {
	
	public static Map<String, Integer> globalWordList = new LinkedHashMap<String, Integer>();
	private static String regexUtil="\\?|\\.|\\!";

	public List<String> textParser(String path) throws FileNotFoundException {
		
		String source = "";
		List<String> sentenceList = new ArrayList<String>();
		source=readLineByLine(path);
		String[] paragraphs = source.split(regexUtil);

		for (String paragraph : paragraphs) {
			sentenceList.add(paragraph);
		}
		return sentenceList;
	}
	
	
    private static String readLineByLine(String filePath) 
    {
        StringBuilder contentBuilder = new StringBuilder();
 
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
        {
            stream.forEach(s -> contentBuilder.append(s));
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
 
        return contentBuilder.toString();
    }

	public List<CounterThread> taskRunner(List<String> list, int threadSize) {

		List<CounterThread> threadList = new ArrayList<CounterThread>();
		List<CounterThread> threadInstance = new ArrayList<CounterThread>();

		for (int i = 0; i < threadSize; i++) {
			threadList.add(new CounterThread(i));
		}

		int k = 0;
		for (int i = 0; i < list.size(); i++) {

			if ( (i!=0) && (i % threadList.size() == 0)) {
				k = 0;
			}
			threadList.get(k).pushSentencesList(list.get(i).trim());
			k++;
		}
		for (CounterThread counterThread : threadList) {
			counterThread.run();
			threadInstance.add(counterThread);

		}

		return threadInstance;

	}

	public synchronized void calculator(String key) {
		if (globalWordList.containsKey(key))
			globalWordList.replace(key, globalWordList.get(key) + 1);
		else
			globalWordList.put(key, 1);
	}

	public static LinkedHashMap<String, Integer> reverseSorted(Map<String, Integer> liste) {
		LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

		liste.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

		return reverseSortedMap;

	}
	
	public static  int defineThread()
	{
		Scanner scan = new Scanner(System.in);
		System.out.print("Thread Sayýsýný giriniz : ");
		return scan.nextInt();
	}
	
	public int calculateAvarageWord(int countSentence ,int value)
	{	
		return (value/countSentence);
	}

	public static void main(String[] args) throws FileNotFoundException {
		String path="";
		
		if(args[0] !=null && args[0].length()>0)
		{
			path=args[0]; 
		}
		else
		{
			path="C:\\Users\\ffiruz\\Desktop\\Soft\\Hazine_Case_Study\\resources\\Soft.txt";
		}
		
		int threadSize=defineThread();
		System.out.println("-------------------------------");


		WordCounter wordCounter = new WordCounter();
		List<String> listeSentence = wordCounter.textParser(path);
		List<CounterThread> listCounterThreads = wordCounter.taskRunner(listeSentence, threadSize);
		
		listCounterThreads.stream().forEach(item -> {
			System.out.println("Thread Id : " + item.getId() + ", Count :" + item.getSentencesList().size());	
			
		});
		
		System.out.println();
		
		LinkedHashMap<String, Integer> reverseSortedMap = reverseSorted(globalWordList);
		int sum =0; 
		for (Map.Entry<String, Integer> entry : reverseSortedMap.entrySet()) {
			System.out.println(entry);
			sum +=entry.getValue();
		}
		
		int countSentence=listCounterThreads.stream().mapToInt(item -> item.getSentencesList().size()).sum();
	
		int avarageCount = wordCounter.calculateAvarageWord(countSentence, sum);	
		System.out.println();
		System.out.println("Sentence Count :" + countSentence);		
		System.out.println("Avg. Word Count :" + avarageCount);
		
		
	}

}

class CounterThread implements Runnable {

	private List<String> sentencesList;
	private int id;


	public CounterThread(int id) {
		super();
		this.sentencesList = new ArrayList<String>();
		this.id = id + 1;
	}

	@Override
	public void run() {

		List<String> liste= new ArrayList<String>();
		try {
		if(!sentencesList.isEmpty()){			
		liste = Arrays.asList(sentencesList.get(0).split("\\s+"));
		liste.stream().forEach(item -> {
			new WordCounter().calculator(item);
		});
		
		}	
		}
		catch (Exception e) {
			
			System.out.println(e.getMessage());
		}

	}

	public void pushSentencesList(String value) {
		sentencesList.add(value);
	}

	public List<String> getSentencesList() {
		return sentencesList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



}
