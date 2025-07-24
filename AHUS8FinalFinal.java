import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;


public class AHUS8FinalFinal {
	
	public long start, finish;
	public long timeElapsed;
	public int totalPatterns;
	public Double minUtility = 0.0;
	public int minSupport;
	
	private boolean isSubsequences(ArrayList<Integer> pattern, ArrayList<Integer> sequence) {
		int i = 0, j = 0;
	
		while (i < pattern.size() && j < sequence.size()) {
			if (pattern.get(i).equals(sequence.get(j))) {
				i++;
			}
			j++;
		}
	
		return i == pattern.size(); // true if entire pattern is matched in sequence
	}
	
	private boolean isSubsequence(List<Integer> a, List<Integer> b) {
		int i = 0, j = 0;
		while (i < a.size() && j < b.size()) {
			if (a.get(i).equals(b.get(j))) {
				i++;
			}
			j++;
		}
		return i == a.size();
	}
	
	private boolean isSupersequence(List<Integer> a, List<Integer> b) {
		return isSubsequence(b, a);
	}
	
public void insertPattern(Map<Integer, List<ArrayList<Integer>>> patternMap, 
                           Map<Integer, Integer> asuMap, 
                           ArrayList<Integer> extPattern) {
    int key = asuMap.size();

    if (!patternMap.containsKey(key)) {
        // If key does not exist, create a new list and add the new pattern
        List<ArrayList<Integer>> newList = new ArrayList<>();
        newList.add(extPattern);
        patternMap.put(key, newList);
    } else {
        // If key exists, add the new pattern to the existing list
        patternMap.get(key).add(extPattern);
    }
}

	
	public static ArrayList<Integer> replaceZeroWithNegativeOne(ArrayList<Integer> pattern) {
		ArrayList<Integer> modifiedPattern = new ArrayList<>();
		for (int num : pattern) {
			modifiedPattern.add(num == 0 ? -1 : num);
		}
		return modifiedPattern;
	}
	//private Set<ArrayList<Integer>> non_CHUSP_map = new HashSet<>();

	Map<Integer, List<ArrayList<Integer>>> CHUSP_map = new HashMap<>();

	Map<Integer, List<ArrayList<Integer>>> non_CHUSP_map = new HashMap<>();
	private ArrayList<ArrayList<Integer>> highUtilityPatterns = new ArrayList<ArrayList<Integer>>();

	private HashMap<Integer, ArrayList<ArrayList<Integer>>> database = null;
	HashMap<Integer, ArrayList<ArrayList<Integer>>> supportMap = new HashMap<>();
	private HashSet<Integer> promisingItems = null;
	//private HashSet<Integer> databaseIConcatList = null;
	//private HashSet<Integer> databaseSConcatList = null;
	private HashMap<Integer, HashMap<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> mapIUList = null;
	private HashMap<ArrayList<Integer>, Integer> mapPEU = null;
	private HashMap<ArrayList<Integer>, Integer> mapASU = null;

	HashMap<ArrayList<Integer>, HashMap<Integer, Integer>> pasuMap = new HashMap<ArrayList<Integer>, HashMap<Integer, Integer>> ();
	//HashMap<Integer, HashMap<ArrayList<Integer>, Integer>> plocMap = new HashMap<Integer, HashMap<ArrayList<Integer>, Integer>> ();
	public void runAlgorithm(String inputPath, String outputPath, double minThresholdUtility, double minThresholdSupport) throws IOException {
		//this.minUtility = minUtility;
		
		//start = Instant.now();
		start=System.currentTimeMillis();		
		
// 1st scan read dataset from file and find swu(p) to find initial promising items list
	//	HashSet<Integer> initialPromisingItems = getPromisingItems(inputPath, minThresholdUtility);
						
// 1st scan read dataset from file and find support count of all items to find initial promising items list
promisingItems = getPromisingItems(inputPath, minThresholdSupport);
		//System.out.println("minUtility is :  " + minUtility);

// 2nd scan read dataset from file and store pruned dataset in hashmap after removing initial unpromising items
		//HashMap<Integer, ArrayList<ArrayList<Integer>>> initialPrunedDatabase = pruneDatabase(initialPromisingItems, inputPath);
 // 2nd scan read dataset from file and store pruned dataset in hashmap after removing initial unpromising items
		 database = pruneDatabase(promisingItems, inputPath);		
		
		 minUtility = minThresholdUtility;
		 System.out.println(" min Utility Occupancy is :  " + minUtility);
		 System.out.println(" min Support is :  " + minSupport);
		// printDatabase(initialPrunedDatabase);

// 3rd scan read dataset from hashmap(initial prumed dataset) and find swu(p) to find final promising items list
		promisingItems = getPromisingItems(database, minThresholdUtility);
		
		//System.out.println("minUtility after pruning unpromising items is :  " + minUtility);
		
// 4th scan read dataset from hashmap(initial prumed dataset) and store final pruned dataset in hashmap after removing final unpromising items
		database = pruneDatabase(promisingItems, database);
		// promisingItems = initialPromisingItems;
		// database = initialPrunedDatabase;
		// printDatabase(database);
		
		//System.out.println("pruned database:"+"\n"+database);
						
		// calcDatabaseConcatLists(promisingItems, database);
		//start = Instant.now(); // starting timer		
//Read dataset from hashmap and claculate IUList, PEU and ASU for all final promising items

		//promisingItems.sort();
		calcIUListPEUandASU(promisingItems, database);
		
		//System.out.println("mapPEU: "+"\n"+mapPEU);
		//System.out.println("pasumap: "+"\n"+pasuMap);
		
		//System.out.println("index utility : " + "\n" +mapIUList);
		//System.out.println("last occurrence : " + "\n"  +plocMap);
		//System.out.println("Actual pattern utility : " + "\n"  +mapASU);
		
		HashSet<Integer> icanpromisingItems=new HashSet<Integer>(promisingItems);
		HashSet<Integer> scanpromisingItems=new HashSet<Integer>(promisingItems);
		ArrayList<Integer> pattern;
		for(int item : promisingItems) {
			pattern = new ArrayList<Integer>();
			pattern.add(item);
			//System.out.println("patterns after purning:"+ pattern);
			//System.out.println("utility of "+ pattern+ ":  " +mapASU.get(pattern) );
					
			if(mapASU.get(pattern) >= minUtility) {
				highUtilityPatterns.add(pattern);
				//System.out.println("highUtilityPatterns:"+ highUtilityPatterns);
			}
			//System.out.println("patterns after purning:"+ pattern);
			//System.out.println("mapASU:"+ mapASU);		
		
			//System.out.println("mapASU:"+ mapPEU);	
			//System.out.println("pasuMap:"+ pasuMap);
			//System.out.println("highUtilityPatterns:"+ highUtilityPatterns);
			//findHUSPs(pattern, mapASU.get(pattern), mapPEU.get(pattern), icanpromisingItems, scanpromisingItems, 'S', pasuMap.get(pattern));
			findHUSPs(pattern, mapASU.get(pattern), mapPEU.get(pattern), icanpromisingItems, scanpromisingItems, pasuMap.get(pattern));
		}

		//finish = Instant.now();		
		//timeElapsed = (Duration.between(start, finish).toMillis());
		
		// Output: support count as key, list of patterns as value
HashMap<Integer, ArrayList<ArrayList<Integer>>> supportMap = new HashMap<>();

for (ArrayList<Integer> patternz : highUtilityPatterns) {
    int supportCount = 0;

    for (Map.Entry<Integer, ArrayList<ArrayList<Integer>>> entry : database.entrySet()) {
        ArrayList<ArrayList<Integer>> sequence = entry.getValue();

        // Flatten the sequence into a single list of integers
        ArrayList<Integer> flatSequence = new ArrayList<>();
        for (ArrayList<Integer> itemset : sequence) {
            flatSequence.addAll(itemset);
        }

        // Check if pattern is a subsequence of the flattened sequence
        if (isSubsequences(patternz, flatSequence)) {
            supportCount++;
        }
    }

    // Store the pattern in supportMap
    supportMap.putIfAbsent(supportCount, new ArrayList<>());
    supportMap.get(supportCount).add(patternz);
}

		finish=System.currentTimeMillis();
		timeElapsed=finish-start;

		totalPatterns = 0;
		
	
		// Printing CHUSP_map with replacement at display time
		System.out.println("====== Final Closed Patterns ======");
		/* 
		for (Map.Entry<Integer, List<ArrayList<Integer>>> entry : CHUSP_map.entrySet()) {
			for (ArrayList<Integer> patterns : entry.getValue()) {
				System.out.println(replaceZeroWithNegativeOne(patterns) +"Support "+ CHUSP_map.get(patterns) ); 
				totalPatterns++; // Modify only while displaying
			}
		}*/
		for (Map.Entry<Integer, List<ArrayList<Integer>>> entry : CHUSP_map.entrySet()) {
			int support = entry.getKey();
			List<ArrayList<Integer>> patterns = entry.getValue();
		
			System.out.println("Support: " + support);
			System.out.println("Patterns:");
		
			for (ArrayList<Integer> patter : patterns) {
				
				System.out.println(patter);
				//System.out.println("Utility:  "+ mapASU.get(patterns));
				totalPatterns++;
			}
		
			System.out.println(); // Just to add a line between support groups
		}
		System.out.println("no of patterns :" + totalPatterns);
		int nonclosed=0;
		System.out.println("Non closed patterns");
		for (Map.Entry<Integer, List<ArrayList<Integer>>> entry : non_CHUSP_map.entrySet()) {
			int support = entry.getKey();
			List<ArrayList<Integer>> patterns = entry.getValue();
		
			System.out.println("Support: " + support );

			System.out.println("Patterns:");
		
			for (ArrayList<Integer> patter : patterns) {
				
				System.out.println(patter);
				nonclosed++;
			}
		
			System.out.println(); // Just to add a line between support groups
		}
		
		System.out.println("no of patterns :" + nonclosed);
		System.out.println("Total HighUtilityPatterns");
		for (ArrayList<Integer> patternx : highUtilityPatterns) {
			System.out.println(patternx + "  utility  "+ mapASU.get(patternx));
		}
		
		System.out.println("Highutility patterns count "+highUtilityPatterns.size());
		System.out.println("high Utility patterns with support");
		for (Map.Entry<Integer, ArrayList<ArrayList<Integer>>> entry : supportMap.entrySet()) {
			Integer support = entry.getKey();
			ArrayList<ArrayList<Integer>> patterns = entry.getValue();
		
			System.out.println("Support: " + support);
			System.out.println("Patterns: ");
		
			for (ArrayList<Integer> patterna : patterns) {
				for (Integer item : patterna) {
					System.out.print(item + " " );
				}
				System.out.println(); // New line after each pattern
			}
			System.out.println(); // Extra line between groups
		}
		
		
	
	writeResultsToFile(outputPath);
		for (Map.Entry<Integer, ArrayList<ArrayList<Integer>>> entry : database.entrySet()) {
			ArrayList<ArrayList<Integer>> outerList = entry.getValue();
		
			for (ArrayList<Integer> innerList : outerList) {
				for (int i = 0; i < innerList.size(); i++) {
					if (innerList.get(i) == 0) {
						innerList.set(i, -1);
					}
				}
			}
		}
		

		//System.out.println("Database"+database);
		//System.out.println("supportMap"+supportMap);
		//System.out.println("highutility patterns"+highUtilityPatterns);

	}

// 1st scan read dataset from file and find swu(p) to find initial promising items list
/*public HashSet<Integer> getPromisingItems(String inputPath, double minThresholdUtility) throws IOException {
    HashSet<Integer> promisingItems = new HashSet<>();
    int databaseUtility = 0;
    BufferedReader myInput = null;
    String thisLine;

    try {
        myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath))));
        Map<Integer, Integer> initialSWU = new HashMap<>();
        Set<Integer> consideredItems = new HashSet<>();

        while ((thisLine = myInput.readLine()) != null) {
            if (thisLine.trim().isEmpty()) continue;

            String tokens[] = thisLine.split(" ");
            String sequenceUtilityString = tokens[tokens.length - 1];
            int positionColons = sequenceUtilityString.indexOf(':');

            if (positionColons == -1) {
                System.out.println("Error: Missing ':' in line -> " + thisLine);
                continue;
            }

            int sequenceUtility = Integer.parseInt(sequenceUtilityString.substring(positionColons + 1));
            databaseUtility += sequenceUtility;

            for (int i = 0; i < tokens.length - 1; i++) {
                String currentToken = tokens[i];

                if (!currentToken.equals("-1") && currentToken.contains("[")) {
                    int positionLeftBracket = currentToken.indexOf('[');
                    String itemString = currentToken.substring(0, positionLeftBracket);
                    Integer item = Integer.parseInt(itemString);

                    if (!consideredItems.contains(item)) {
                        consideredItems.add(item);
                        Integer swu = initialSWU.getOrDefault(item, 0);
                        initialSWU.put(item, swu + sequenceUtility);
                    }
                }
            }
            consideredItems.clear();
        }

        System.out.println("Database Utility: " + databaseUtility);
        System.out.println("Minimum Threshold Utility: " + minThresholdUtility);
       // minUtility = (int) (databaseUtility * minThresholdUtility);
	   minUtility = (databaseUtility * minThresholdUtility);

        for (Entry<Integer, Integer> entry : initialSWU.entrySet()) {
            if (entry.getValue() >= minUtility) {
                promisingItems.add(entry.getKey());
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (myInput != null) myInput.close();
    }

    return promisingItems;
}*/
// 1st scan read dataset from file and find Support of all items to find initial promising items list
private HashSet<Integer> getPromisingItems(String inputPath, double minThresholdSupport) throws IOException {
	HashSet<Integer> promisingItems = new HashSet<Integer>();
	//int databaseutility=0; 
	int sequenceCount = 0;
	BufferedReader myInput = null;
	String thisLine;
	try {
		myInput = new BufferedReader(new InputStreamReader( new FileInputStream(new File(inputPath))));
		//Map<Integer, Integer> initialSWU = new HashMap<Integer, Integer>();
		Map<Integer, Integer> initialSup = new HashMap<Integer, Integer>();
		Set<Integer> consideredItems = new HashSet<Integer>();

		while ((thisLine = myInput.readLine()) != null) {
			if (thisLine.isEmpty()) {
				continue;
			}
			
			String tokens[] = thisLine.split(" "); 	
			
			/*
			String sequenceUtilityString = tokens[tokens.length-1];
			int positionColons = sequenceUtilityString.indexOf(':');
			int sequenceUtility = Integer.parseInt(sequenceUtilityString.substring(positionColons+1));				
			databaseutility += sequenceUtility ; 
			*/
			
			sequenceCount += 1;
			
			for(int i = 0; i < tokens.length - 3; i++) {
				String currentToken = tokens[i];

				if(currentToken.length() != 0 && !currentToken.equals("-1")) {

					int positionLeftBracketString = currentToken.indexOf('[');

					String itemString = currentToken.substring(0, positionLeftBracketString);
					Integer item = Integer.parseInt(itemString);
					
					if (!consideredItems.contains(item)) {
						consideredItems.add(item);
						/*
						Integer swu = initialSWU.get(item);							
						swu = (swu == null) ? sequenceUtility : swu + sequenceUtility;
						initialSWU.put(item, swu);
						*/
						Integer sup = initialSup.get(item);							
						sup = (sup == null) ? 1 : sup + 1;
						initialSup.put(item, sup);
					}						
				}
			}

			consideredItems.clear();
		}
		
		//System.out.println("databaseutility is :  " + databaseutility );
		//System.out.println("minThresholdUtility is :  " + minThresholdUtility );
		//minUtility = (int)(databaseutility * minThresholdUtility);
		//System.out.println("sequenceCount is :  " + sequenceCount );
		
		minSupport = (int)(sequenceCount * minThresholdSupport);
		
		for(Entry<Integer, Integer> entry : initialSup.entrySet()) {
			if(entry.getValue() >= minSupport) {
				//if(initialSup.get(entry.getKey()) >= minSupport)
				promisingItems.add(entry.getKey());
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(myInput != null){
			myInput.close();
		}
	}

	return promisingItems;
}

public HashMap<Integer, ArrayList<ArrayList<Integer>>> pruneDatabase(HashSet<Integer> promisingItems, String inputPath) throws IOException {
    HashMap<Integer, ArrayList<ArrayList<Integer>>> database = new HashMap<>();
    BufferedReader myInput = null;
    String thisLine;
    //System.out.println("Promising Items Before Pruning: " + promisingItems);

    try {
        myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath))));
        int promisingSequenceCount = 0;

        while ((thisLine = myInput.readLine()) != null) {
            if (thisLine.trim().isEmpty()) continue;

            ArrayList<Integer> seqList = new ArrayList<>();
            ArrayList<Integer> ulistList = new ArrayList<>();
            ArrayList<Integer> rlistList = new ArrayList<>();

            String tokens[] = thisLine.split(" ");
            String sequenceUtilityString = tokens[tokens.length - 1];
            int positionColons = sequenceUtilityString.indexOf(':');

            if (positionColons == -1) {
                System.out.println("Error: Invalid sequence format -> " + thisLine);
                continue;
            }

            int sequenceUtility = Integer.parseInt(sequenceUtilityString.substring(positionColons + 1));
            int remSequenceUtility = sequenceUtility;

            boolean flag = false;
            boolean flag1 = false;

            for (int i = 0; i < tokens.length - 1; i++) {
                String currentToken = tokens[i];

                if (!currentToken.equals("-1") && currentToken.contains("[")) {
                    int positionLeftBracket = currentToken.indexOf('[');
                    String itemString = currentToken.substring(0, positionLeftBracket);
                    Integer item = Integer.parseInt(itemString);
                    String utilityString = currentToken.substring(positionLeftBracket + 1, currentToken.length() - 1);
                    Integer utility = Integer.parseInt(utilityString);

                    if (!promisingItems.contains(item)) {
                        remSequenceUtility -= utility;
                        continue;
                    }

                    if (flag) {
                        seqList.add(0);
                        ulistList.add(0);
                        seqList.add(item);
                        ulistList.add(utility);
                        flag = false;
                        flag1 = true;
                    } else {
                        seqList.add(item);
                        ulistList.add(utility);
                        flag1 = true;
                    }
                } else {
                    if (currentToken.equals("-1") && flag1) {
                        flag = true;
                    }
                }
            }

            for (int j = 0; j < seqList.size(); j++) {
                if (seqList.get(j) != 0) {
                    int utility = ulistList.get(j);
                    rlistList.add(remSequenceUtility - utility);
                    remSequenceUtility -= utility;
                } else {
                    rlistList.add(0);
                }
            }

            if (seqList.isEmpty()) continue;

            promisingSequenceCount++;
            ArrayList<ArrayList<Integer>> newSequence = new ArrayList<>();
            newSequence.add(seqList);
            newSequence.add(ulistList);
            newSequence.add(rlistList);
            database.put(promisingSequenceCount - 1, newSequence);
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (myInput != null) myInput.close();
    }
	//System.out.println("Database after pruning: " + database);

    return database;
}

// 3rd scan read dataset from hashmap(initial prumed dataset) and find swu(p) to find final promising items list
	private HashSet<Integer> getPromisingItems(HashMap<Integer, ArrayList<ArrayList<Integer>>> databaseParam, double minThresholdUtility) {
		HashMap<Integer, ArrayList<ArrayList<Integer>>> _database = new HashMap<Integer, ArrayList<ArrayList<Integer>>>(databaseParam);

		HashSet<Integer> _promisingItems = new HashSet<Integer>();

		Map<Integer, Integer> initialSWU = new HashMap<Integer, Integer>();
		Set<Integer> consideredItems = new HashSet<Integer>();
		//int databaseutility=0; 

		for(int i = 0; i < _database.size(); i++) {
			ArrayList<ArrayList<Integer>> sequence = _database.get(i);
			int sequenceUtility = sequence.get(1).get(0) + sequence.get(2).get(0);
			//databaseutility += sequenceUtility ; 
			
			for(int j = 0; j < sequence.get(0).size(); j++) {
				Integer item = sequence.get(0).get(j);
				if(!item.equals(0) && !consideredItems.contains(item)) {
					consideredItems.add(item);
					
					Integer swu = initialSWU.get(item);
					
					swu = (swu == null) ? sequenceUtility : swu + sequenceUtility;
					initialSWU.put(item, swu);
				}
			}

			consideredItems.clear();
		}
		
		//System.out.println("Final databaseutility after pruning is :  " + databaseutility );
		//System.out.println("minThresholdUtility is :  " + minThresholdUtility );
		//minUtility = (int)(databaseutility * minThresholdUtility);

		for(Entry<Integer, Integer> entry : initialSWU.entrySet()) {
			if(entry.getValue() >= minUtility) {
				_promisingItems.add(entry.getKey());
			}
		}

		return _promisingItems;
	}

// 4th scan read dataset from hashmap(initial prumed dataset) and store final pruned dataset in hashmap after removing final unpromising items
	private HashMap<Integer, ArrayList<ArrayList<Integer>>> pruneDatabase(HashSet<Integer> promisingItemsParam, HashMap<Integer, ArrayList<ArrayList<Integer>>> databaseParam) throws IOException {
		HashSet<Integer> _promisingItems = new HashSet<Integer>(promisingItems);
		HashMap<Integer, ArrayList<ArrayList<Integer>>> oldDatabase = new HashMap<Integer, ArrayList<ArrayList<Integer>>>(databaseParam);
		HashMap<Integer, ArrayList<ArrayList<Integer>>> newDatabase = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();

		int promisingSequenceCount = 0;

		for(int i = 0; i < oldDatabase.size(); i++) {
			ArrayList<Integer> seqList = new ArrayList<Integer>();
			ArrayList<Integer> ulistList = new ArrayList<Integer>();
			ArrayList<Integer> rlistList = new ArrayList<Integer>();
			
			ArrayList<ArrayList<Integer>> sequence = oldDatabase.get(i);
			int sequenceUtility = sequence.get(1).get(0) + sequence.get(2).get(0);

			int remSequenceUtility = sequenceUtility;
			boolean flag=false;
			boolean flag1=false;
			for(int j = 0; j < sequence.get(0).size(); j++) {
				Integer item = sequence.get(0).get(j);

				if(item != 0) {
					Integer utility = sequence.get(1).get(j);

					if(!_promisingItems.contains(item)) {
						remSequenceUtility -= utility;
						continue;
					}
					
					if(flag){
					seqList.add(0);
					ulistList.add(0);
					seqList.add(item);
					ulistList.add(utility);
					flag= false;
					flag1=true;
					}
					else{ 
					seqList.add(item);
					ulistList.add(utility);					
					flag1=true;
					}
					// int remUtility = remSequenceUtility - utility;
					// rlistList.add(remSequenceUtility - utility);
					// remSequenceUtility -= utility;
				} else {
					if(item == 0 && flag1)
					{					
					flag=true;
					}
				}
			}
			
			if(seqList.size() == 0) {
				continue;
			}		
			
			
			for(int j=0; j < seqList.size(); j++) {
					if(seqList.get(j) != 0) {

						int utility = ulistList.get(j);
						
						rlistList.add(remSequenceUtility - utility);
						remSequenceUtility -= utility;
					} else {

						rlistList.add(0);
					}
				}

			 /*if(seqList.size() == 0) {
				continue;
			} */

			promisingSequenceCount++;
			
			ArrayList<ArrayList<Integer>> newSequence = new ArrayList<ArrayList<Integer>>();
			newSequence.add(seqList);
			newSequence.add(ulistList);
			newSequence.add(rlistList);

			newDatabase.put(promisingSequenceCount-1, newSequence);
		}

		return newDatabase;
	}
	
//Read dataset from hashmap and claculate IUList, PEU and ASU for all final promising items
	private void calcIUListPEUandASU(HashSet<Integer> promisingItemsParam, HashMap<Integer, ArrayList<ArrayList<Integer>>> databaseParam) {
		//HashSet<Integer> _promisingItems = new HashSet<Integer>(promisingItemsParam);
		HashMap<Integer, ArrayList<ArrayList<Integer>>> _database = new HashMap<Integer, ArrayList<ArrayList<Integer>>>(databaseParam);

		HashMap<Integer ,HashMap<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> _mapIUList = new HashMap<Integer, HashMap<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>>();
		HashMap<ArrayList<Integer>, Integer> _mapPEU = new HashMap<ArrayList<Integer>, Integer>();
		HashMap<ArrayList<Integer>, Integer> _mapASU = new HashMap<ArrayList<Integer>, Integer>();
		
		//ArrayList<Integer> index;
		//HashMap<ArrayList<Integer>, Integer> locMap;
		
		//HashMap<ArrayList<Integer>, HashMap<Integer, Integer>> pasuMap = new HashMap<ArrayList<Integer>, HashMap<Integer, Integer>> ();
		//HashMap<Integer, Integer> psasuMap ;
		
		for(int i : _database.keySet()) {
			ArrayList<ArrayList<Integer>> sequence = _database.get(i);
			HashMap<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> iuListInSequence = new HashMap<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>();
			ArrayList<Integer> pattern = null;
			HashMap<ArrayList<Integer>, Integer> maxUtilInSequence = new HashMap<ArrayList<Integer>, Integer>();
			HashMap<ArrayList<Integer>, Integer> maxPEUInSequence = new HashMap<ArrayList<Integer>, Integer>();
			int tno = 1;
			for(int j=0; j < sequence.get(0).size(); j++) {
				if(sequence.get(0).get(j) == 0) {
					tno++;
					continue;
				}

				Integer item = sequence.get(0).get(j);
				pattern = new ArrayList<Integer>();
				pattern.add(item);

				if(iuListInSequence.get(pattern) == null) {
					iuListInSequence.put(pattern, new ArrayList<ArrayList<Integer>>());
					iuListInSequence.get(pattern).add(new ArrayList<Integer>());
					iuListInSequence.get(pattern).add(new ArrayList<Integer>());
					iuListInSequence.get(pattern).add(new ArrayList<Integer>());
				}
				//iuListInSequence.get(pattern).get(0).add(j);
				iuListInSequence.get(pattern).get(0).add(tno);
				iuListInSequence.get(pattern).get(1).add(sequence.get(1).get(j));
				iuListInSequence.get(pattern).get(2).add(sequence.get(1).get(j) + sequence.get(2).get(j));

				if(maxUtilInSequence.get(pattern) == null) {
					maxUtilInSequence.put(pattern, 0);
				}
				if(maxUtilInSequence.get(pattern) < sequence.get(1).get(j)) {
					maxUtilInSequence.put(pattern, sequence.get(1).get(j));
				}

				if(maxPEUInSequence.get(pattern) == null) {
					maxPEUInSequence.put(pattern, 0);
				}
				if(maxPEUInSequence.get(pattern) < sequence.get(1).get(j) + sequence.get(2).get(j)) {
					maxPEUInSequence.put(pattern, sequence.get(1).get(j) + sequence.get(2).get(j));
				}
			}

			if(_mapIUList.get(i) == null) {
				_mapIUList.put(i, new HashMap<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>());
			}
			_mapIUList.put(i, iuListInSequence);
			HashMap<Integer, Integer> puMap = null;
			for(ArrayList<Integer> pat : maxUtilInSequence.keySet()) {
				//psasuMap = new HashMap<Integer, Integer> ();
				_mapASU.put(pat, (_mapASU.get(pat) == null) ? maxUtilInSequence.get(pat) : _mapASU.get(pat) + maxUtilInSequence.get(pat));
				_mapPEU.put(pat, (_mapPEU.get(pat) == null) ? maxPEUInSequence.get(pat) : _mapPEU.get(pat) + maxPEUInSequence.get(pat));
				//psasuMap.put(i, maxUtilInSequence.get(pat));
					if(pasuMap.get(pat) == null){	
					puMap = new HashMap<Integer, Integer>();
					puMap.put(i, maxUtilInSequence.get(pat));
					pasuMap.put(pat,puMap);										
					}
					else {
					pasuMap.get(pat).put(i, maxUtilInSequence.get(pat));									
				//pasuMap.get(pat) == null ? pasuMap.put(pat,psasuMap) : pasuMap.get(pat).put(i, maxUtilInSequence.get(pat));
					}
			}
			/*
			locMap= new HashMap<ArrayList<Integer>, Integer> ();
			int loc;
			for(ArrayList<Integer> pat : iuListInSequence.keySet())
			{
				index = iuListInSequence.get(pat).get(0);
				loc=index.get(index.size()-1);
				locMap.put(pat,loc);
			}
			plocMap.put(i,locMap);
			*/
		}
		//System.out.println(pasuMap);
		mapIUList = _mapIUList;
		mapASU = _mapASU;
		mapPEU = _mapPEU;
	}

	


	private void calcIUListPEUandASU(ArrayList<Integer> pattern, Integer item, char concatType, HashMap<Integer, Integer>  _dasuMap) {
		ArrayList<Integer> prefixPattern = new ArrayList<Integer>(pattern);
		ArrayList<Integer> itemPattern = new ArrayList<Integer>();
		itemPattern.add(item);

		ArrayList<Integer> extPattern = new ArrayList<Integer>(prefixPattern);
		
		if(concatType == 'i') {
			extPattern.add(item);
		} else {
			extPattern.add(-1);
			extPattern.add(item);
		}
		//System.out.println("calcIUListPEUandASU is running for pattern: " + pattern + " and item: " + item);

	
		//System.out.println("database:"+ database);
		for(int i : database.keySet()) {
			
			if(!mapIUList.get(i).keySet().contains(prefixPattern) || !mapIUList.get(i).keySet().contains(itemPattern)) {
				continue;
			}

			//ArrayList<ArrayList<Integer>> sequence = database.get(i);
			
			ArrayList<ArrayList<Integer>> itemIUList = mapIUList.get(i).get(itemPattern);
			ArrayList<ArrayList<Integer>> prefixIUList = mapIUList.get(i).get(prefixPattern);
			ArrayList<ArrayList<Integer>> extIUList = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> itemocclist = itemIUList.get(0);  
			//for(int itemIndex : itemIUList.get(0)) 
			//boolean patternFoundInSequence = true; // Track if pattern exists in sequence
			for(int index = 0; index < itemocclist.size(); index++) 	
			{
				int itemIndex = itemocclist.get(index);
				/*
				int rightBound = -1, leftBound = 0;

				if(concatType == 'i') {
					rightBound = itemIndex-1;
					for(int j = itemIndex-1; j >= 0; j--) {
						if(sequence.get(0).get(j) == 0) {
							leftBound = j+1;
							break;
						}
					}
				} else {
					for(int j = itemIndex-1; j >= 0; j--) {
						if(sequence.get(0).get(j) == 0) {
							rightBound = j-1;
							break;
						}
					}
				}

				if(rightBound == -1) {
					continue;
				}
				*/
				int max = 0;
				ArrayList<Integer> prefixIndexList = prefixIUList.get(0);
				if(concatType == 'i')
				{				
				for(int prefixIndex = 0; prefixIndex < prefixIndexList.size(); prefixIndex++) {
					//if(prefixIndexList.get(prefixIndex) >= leftBound && prefixIndexList.get(prefixIndex) <= rightBound) {
						if(prefixIndexList.get(prefixIndex) == itemIndex) {
						max = (max < prefixIUList.get(1).get(prefixIndex)) ? prefixIUList.get(1).get(prefixIndex) : max;
					} 
				}
				}
				else
				{				
				for(int prefixIndex = 0; prefixIndex < prefixIndexList.size(); prefixIndex++) {
					//if(prefixIndexList.get(prefixIndex) >= leftBound && prefixIndexList.get(prefixIndex) <= rightBound) {
						if(prefixIndexList.get(prefixIndex) < itemIndex) {
						max = (max < prefixIUList.get(1).get(prefixIndex)) ? prefixIUList.get(1).get(prefixIndex) : max;
					} 
				}	
				}					
				
				//ArrayList<Integer> al=null;
				if(max != 0) {
					if(extIUList.size() == 0) {
						//System.out.println("size is 0");
						extIUList.add(new ArrayList<Integer>());
						extIUList.add(new ArrayList<Integer>());
						extIUList.add(new ArrayList<Integer>());
					}
					extIUList.get(0).add(itemIndex);
					extIUList.get(1).add(max + itemIUList.get(1).get(index));
					extIUList.get(2).add(max + itemIUList.get(2).get(index));
					//extIUList.get(1).add(max + sequence.get(1).get(itemIndex));
					//extIUList.get(2).add(max + sequence.get(1).get(itemIndex) + sequence.get(2).get(itemIndex));
					
					//System.out.println(extIUList.get(0)+ " " + extIUList.get(1)+ " " + extIUList.get(2));
				}
			}

			if(extIUList.size() != 0) {
				mapIUList.get(i).put(extPattern, extIUList);

				int maxPEUInSequence = 0, maxUtilInSequence = 0;
				for(int index = 0; index < extIUList.get(0).size(); index++) {
					if(extIUList.get(1).get(index) != extIUList.get(2).get(index))
					maxPEUInSequence = (extIUList.get(2).get(index) > maxPEUInSequence) ? extIUList.get(2).get(index) : maxPEUInSequence;
					maxUtilInSequence = (extIUList.get(1).get(index) > maxUtilInSequence) ? extIUList.get(1).get(index) : maxUtilInSequence;
				}				
				mapPEU.put(extPattern, (mapPEU.keySet().contains(extPattern) ? mapPEU.get(extPattern) + maxPEUInSequence : maxPEUInSequence));
				mapASU.put(extPattern, (mapASU.keySet().contains(extPattern) ? mapASU.get(extPattern) + maxUtilInSequence : maxUtilInSequence));
				_dasuMap.put(i,maxUtilInSequence);
				
			}
		}
		//System.out.println("Final Support Count for pattern " + extPattern + " = " + supportCount);

		
	}


	private void getRSU(ArrayList<Integer> pattern, HashSet<Integer> dicanpromisingItems, HashSet<Integer> dscanpromisingItems, HashMap<Integer, Integer>  dasuMap ) {

		ArrayList<Integer> prefixPattern = new ArrayList<Integer>(pattern);
		HashMap<Integer, Integer> speuMap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> ipeuMap = new HashMap<Integer, Integer>();
		HashSet<Integer> _dicanpromisingItems=new HashSet<Integer>();
		HashSet<Integer> _dscanpromisingItems=new HashSet<Integer>();
		ArrayList<Integer> itemPattern;
		ArrayList<ArrayList<Integer>> itemIUList;
		ArrayList<Integer> itemocclist;
		
		for(int seqNum : database.keySet()) {
			
		_dicanpromisingItems.clear();
		_dscanpromisingItems.clear();		
					
		if(mapIUList.get(seqNum).get(prefixPattern) != null)
		{	
		int prefixIndex = mapIUList.get(seqNum).get(prefixPattern).get(0).get(0);
		for(int item : dscanpromisingItems) 
		{
			itemPattern = new ArrayList<Integer>();
			itemPattern.add(item);	
			itemIUList = mapIUList.get(seqNum).get(itemPattern);			 
			if(itemIUList != null)
			{
			itemocclist = itemIUList.get(0); 
			for(int index = 0; index < itemocclist.size(); index++) 	
			{
			int iindex = itemocclist.get(index);
			if(	prefixIndex < iindex && !_dscanpromisingItems.contains(item))
			{
			_dscanpromisingItems.add(item);
			speuMap.put(item, (speuMap.get(item) == null) ? (dasuMap.get(seqNum)+itemIUList.get(2).get(index)) : (dasuMap.get(seqNum)+itemIUList.get(2).get(index)+speuMap.get(item)));			
			}
			if(dicanpromisingItems.contains(item)){
			if(	prefixIndex == iindex && !_dicanpromisingItems.contains(item))
			{
			_dicanpromisingItems.add(item);
			ipeuMap.put(item, (ipeuMap.get(item) == null) ? (dasuMap.get(seqNum)+itemIUList.get(2).get(index)) : (dasuMap.get(seqNum)+itemIUList.get(2).get(index)+ipeuMap.get(item)));			
			}
			}
			}			
			}
			/* if(plocMap.get(seqNum).get(itemPattern) != null)
			{
			if(prefixIndex < plocMap.get(seqNum).get(itemPattern))
			{
			if(dscanpromisingItems.contains(item))			
			_dscanpromisingItems.add(item);	
			}
			if(prefixIndex <= plocMap.get(seqNum).get(itemPattern))
			{
			if(dicanpromisingItems.contains(item))
			_dicanpromisingItems.add(item);				
			}
			} */		
		}
		
		/*
		ArrayList<Integer> prefixIndexList = mapIUList.get(seqNum).get(prefixPattern).get(0);
		//System.out.println(prefixIndexList);
		ArrayList<Integer> seq = database.get(seqNum).get(0);
		ArrayList<Integer> util = database.get(seqNum).get(1);
		ArrayList<Integer> rutil = database.get(seqNum).get(2);
		for(int prefixIndex : prefixIndexList) {
				boolean itemsetEnded = false;
				for(int index = prefixIndex+1; index < seq.size(); index++) {
					if(seq.get(index) == 0) {
						itemsetEnded = true;
						//System.out.println("item set ended");
						break;
					}
					if(!itemsetEnded && dicanpromisingItems.contains(seq.get(index))) {	
						if(!_dicanpromisingItems.contains(seq.get(index))) {				
						_dicanpromisingItems.add(seq.get(index));
						//System.out.println(pattern+ "  " + seq.get(index));
						//if(ipeuMap.get(seq.get(index)) == null)
						//System.out.println(dasuMap.get(seqNum));
						ipeuMap.put(seq.get(index), (ipeuMap.get(seq.get(index)) == null) ? (dasuMap.get(seqNum)+util.get(index)+rutil.get(index)) : (dasuMap.get(seqNum)+util.get(index)+rutil.get(index)+ipeuMap.get(seq.get(index))));	
						
						}						
					}
				}
		
		}
		int minIndex = Integer.MAX_VALUE;
			for(int index : prefixIndexList) {
				minIndex = (index < minIndex) ? index : minIndex;
			}

			boolean seqEnded = false;
			for(int index = minIndex+1; index < seq.size(); index++) {
				if(seq.get(index) == 0) {
					seqEnded = true;
					continue;
				}
				if(seqEnded && dscanpromisingItems.contains(seq.get(index))) {
					if(!_dscanpromisingItems.contains(seq.get(index))) {	
					_dscanpromisingItems.add(seq.get(index));
					speuMap.put(seq.get(index), (speuMap.get(seq.get(index)) == null) ? (dasuMap.get(seqNum)+util.get(index)+rutil.get(index)) : (dasuMap.get(seqNum)+util.get(index)+rutil.get(index)+speuMap.get(seq.get(index))));
					}
				}
			}
			*/
		/*
		for(int item : _dicanpromisingItems) {
		ipeuMap.put(item, (ipeuMap.get(item) == null) ? dasuMap.get(seqNum) : dasuMap.get(seqNum)+ ipeuMap.get(item));		
		}
		for(int item : _dscanpromisingItems) {
		speuMap.put(item, (speuMap.get(item) == null) ? dasuMap.get(seqNum) : dasuMap.get(seqNum)+ speuMap.get(item));		
		}
		*/
		/*
		for(int item : _dicanpromisingItems) {		
		ipeuMap.put(item, (ipeuMap.get(item) == null) ? dasuMap.get(seqNum) : dasuMap.get(seqNum)+ ipeuMap.get(item));				
		}
		for(int item : _dscanpromisingItems) {				
		speuMap.put(item, (speuMap.get(item) == null) ? dasuMap.get(seqNum) : dasuMap.get(seqNum)+ speuMap.get(item));		
		}
		*/
		}
		}
		
		dicanpromisingItems.clear();
		dscanpromisingItems.clear();
		for (int item : ipeuMap.keySet()) {			
			if(ipeuMap.get(item) >= minUtility)
				dicanpromisingItems.add(item);
		}
		
		for (int item : speuMap.keySet()) {			
			if(speuMap.get(item) >= minUtility)
				dscanpromisingItems.add(item);
		}
		
		//System.out.println("dicanpromisingItems " + dicanpromisingItems);
		//System.out.println("dscanpromisingItems " + dscanpromisingItems);
		
		/*
		if(concatType == 'i') {
			for(int prefixIndex : prefixIndexList) {
				boolean itemsetEnded = false;
				for(int index = prefixIndex; index < seq.size(); index++) {
					if(seq.get(index) == 0) {
						itemsetEnded = true;
						break;
					}
					if(!itemsetEnded && seq.get(index) == item) {
						extPatternExists = true;

						extPattern.add(item);

						break;
					}
				}

				// if(itemsetEnded || extPatternExists)
				if(extPatternExists)
					break;
			
			}
		} else {
			int minIndex = Integer.MAX_VALUE;
			for(int index : prefixIndexList) {
				minIndex = (index < minIndex) ? index : minIndex;
			}

			boolean seqEnded = false;
			for(int index = minIndex; index < seq.size(); index++) {
				if(seq.get(index) == 0) {
					seqEnded = true;
					continue;
				}
				if(seqEnded && seq.get(index) == item) {
					extPatternExists = true;

					extPattern.add(0);
					extPattern.add(item);
					
					break;
				}
			}

		}

		// System.out.println(extPatternExists + "\t" + prefixPeu);

		if(extPatternExists) {
			return prefixPeu;
		}
		return 0;
		*/
	}

	//private void findHUSPs(ArrayList<Integer> pattern, Integer _asuP, Integer _psuP, HashSet<Integer> icanpromisingItems, HashSet<Integer> scanpromisingItems, char lastExtType, HashMap<Integer, Integer> _asuMap) {
	private void findHUSPs(ArrayList<Integer> pattern, Integer _asuP, Integer _psuP, 
		HashSet<Integer> icanpromisingItems, HashSet<Integer> scanpromisingItems, 
		HashMap<Integer, Integer> _asuMap)
		{    
	
		ArrayList<Integer> prefixPattern = new ArrayList<>(pattern);
		HashMap<Integer, Integer> asuMap;
		// Modify prefixPattern: Replace all 0s with -1
	    for (int i = 0; i < prefixPattern.size(); i++) {
		   if (prefixPattern.get(i) == 0) {
			prefixPattern.set(i, -1);
		  }
	    }
		HashSet<Integer> _icanpromisingItems = new HashSet<>(icanpromisingItems);
		HashSet<Integer> _scanpromisingItems = new HashSet<>(scanpromisingItems);
	
		if (_psuP < minUtility) return;
        //System.out.println(pattern+" extn utility is: "+_psuP);
		
		/*
		if(lastExtType=='I')
		{
		 _icanpromisingItems=new HashSet<Integer>(icanpromisingItems);
		 _scanpromisingItems=new HashSet<Integer>(scanpromisingItems);
		 Collection<Integer> removeCandidates = new LinkedList<Integer>();
		
		for(Integer item : _icanpromisingItems) {
			ArrayList<Integer> itemPattern = new ArrayList<Integer>();
			itemPattern.add(item);

			if(_asuP + mapPEU.get(itemPattern) < minUtility) {
				//_icanpromisingItems.remove(item);
				removeCandidates.add(item);
			}
		}
		_icanpromisingItems.removeAll(removeCandidates);
		removeCandidates.clear();
		
		for(Integer item : _scanpromisingItems) {
			ArrayList<Integer> itemPattern = new ArrayList<Integer>();
			itemPattern.add(item);

			if(_asuP + mapPEU.get(itemPattern) < minUtility) {
				//_scanpromisingItems.remove(item);
				removeCandidates.add(item);
			}
		}
		_scanpromisingItems.removeAll(removeCandidates);
		}		
		else
		{
		_icanpromisingItems=new HashSet<Integer>(scanpromisingItems);
		_scanpromisingItems=new HashSet<Integer>(scanpromisingItems);
		Collection<Integer> removeCandidates = new LinkedList<Integer>();
		for(Integer item : _scanpromisingItems) {
		//for (Iterator<Integer> i = _scanpromisingItems.iterator(); i.hasNext();) {
		//Integer item = i.next();
				
			ArrayList<Integer> itemPattern = new ArrayList<Integer>();
			itemPattern.add(item);

			if(_asuP + mapPEU.get(itemPattern) < minUtility) {
				//_scanpromisingItems.remove(item);				
				_icanpromisingItems.remove(item);
				removeCandidates.add(item);
			}
		}
		_scanpromisingItems.removeAll(removeCandidates);
		}
		*/		
		
		//System.out.println(prefixPattern+ " inside FHUP _icanpromisingItems:   " + _icanpromisingItems);
		//System.out.println(prefixPattern+ " inside FHUP _scanpromisingItems:     " + _scanpromisingItems);
		
		for(Integer item : promisingItems) {
			ArrayList<Integer> itemPattern = new ArrayList<Integer>();
			itemPattern.add(item);
		if(_asuP + mapPEU.get(itemPattern) < minUtility) {
				_scanpromisingItems.remove(item);				
				_icanpromisingItems.remove(item);				
			}	
		}
	/* 	for(int i = prefixPattern.size()-1; i >= 0; i--) {					
			if(_icanpromisingItems.contains(prefixPattern.get(i))) {
				_icanpromisingItems.remove(prefixPattern.get(i));
			}
			if(prefixPattern.get(i).equals(0)) {
				break;
			}					
        }

       int litem = prefixPattern.get(prefixPattern.size()-1);
       System.out.println(prefixPattern+ " last item: "+ litem);
    */	Integer litem = prefixPattern.get(prefixPattern.size()-1);
		Collection<Integer> removeCandidates = new LinkedList<Integer>();
		for(Integer item : _icanpromisingItems) {			
		//System.out.println(item.intValue() + "<="+ litem.intValue() + ":  "+(item.intValue() <= litem.intValue()));	
		if(item.intValue() <= litem.intValue() )
		removeCandidates.add(item);			
		}
		_icanpromisingItems.removeAll(removeCandidates);
		
		
		//System.out.println(prefixPattern+ " before Rsu _icanpromisingItems:   " + _icanpromisingItems);
		//System.out.println(prefixPattern+ " before Rsu _scanpromisingItems:     " + _scanpromisingItems);
		
		getRSU(prefixPattern, _icanpromisingItems, _scanpromisingItems, _asuMap);
		
		//System.out.println(prefixPattern+ " after Rsu _icanpromisingItems:   " + _icanpromisingItems);
		//System.out.println(prefixPattern+ " after Rsu _scanpromisingItems:     " + _scanpromisingItems);
		
		
		for(Integer item : _icanpromisingItems)
		{
			//HashMap<List<ArrayList<Integer>>,Integer> supportPatterns = new HashMap<>();

			//peuMap = new HashMap<Integer, Integer>();
			asuMap = new HashMap<Integer, Integer>();
			calcIUListPEUandASU(prefixPattern, item, 'i', asuMap);
            ArrayList<Integer> extPattern = new ArrayList<Integer>(prefixPattern);
			extPattern.add(item);
			//System.out.println("utility of "+ extPattern+ ":  " +mapASU.get(extPattern) );
			if(mapASU.get(extPattern) != null)
			{
			    // Closed Patterns Algorithm condition
			    //t=prefixPattern t'=extPattern
			/* 	System.out.println("====== ASU Map Values ======");
				for (Map.Entry<Integer, Integer> entry : asuMap.entrySet()) {
					System.out.println("Key: " + entry.getKey() + " -> Value: " + entry.getValue());
				}
			*/	
		//	System.out.println("minUtility"+mapASU.get(extPattern));
			    if(mapASU.get(extPattern) >= minUtility && asuMap.size()>= minSupport)
				{
					highUtilityPatterns.add(extPattern);
					if ( asuMap.size() == _asuMap.size() ) 
					{
					
						//CHUSP_map.remove(prefixPattern);
						List<Integer> keysToRemove = new ArrayList<>();
						for (Map.Entry<Integer, List<ArrayList<Integer>>> entry : CHUSP_map.entrySet()) {
							if (entry.getValue().contains(prefixPattern)) {
								entry.getValue().remove(prefixPattern);
								if (entry.getValue().isEmpty()) {
									keysToRemove.add(entry.getKey());
								}
							}
						}
						for (Integer key : keysToRemove) {
							CHUSP_map.remove(key);
						}
						
							 //non_CHUSP_map.computeIfAbsent(mapPEU.get(prefixPattern), k -> new ArrayList<>()).add(prefixPattern);
							 insertPattern(non_CHUSP_map, asuMap, prefixPattern);
	 
							 //adding t'=extpattern into CHUSP_map
	 
							 if (CHUSP_map.containsKey(asuMap.size())) 
							 { 
								 int newSupport = asuMap.size();
								 List<ArrayList<Integer>> existingPatterns = CHUSP_map.get(newSupport);	
								 boolean isClosed = true;
								// List<ArrayList<Integer>> patternsToRemove = new ArrayList<>();
	 
								 for (ArrayList<Integer> existingPattern : existingPatterns) {
									 if (isSubsequence(extPattern, existingPattern)) {
										 // extPattern is a subsequence, not closed
										 isClosed = false;
										 break;
									 } else if (isSupersequence(extPattern, existingPattern)) {
										 // extPattern is a superpattern, remove the old one
										 //patternsToRemove.add(existingPattern);
										 int support = _asuMap.size();  // Get the support value

										 List<ArrayList<Integer>> listOfPatterns = CHUSP_map.get(support);
										 if (listOfPatterns != null) {
											 listOfPatterns.removeIf(patternToRemove -> pattern.equals(existingPattern));
										 
											 //  remove the key if the list becomes empty
											 if (listOfPatterns.isEmpty()) {
												 CHUSP_map.remove(support);
											 }
										 }
										 
										 if(!non_CHUSP_map.containsValue(prefixPattern)){
											//CHUSP_map.computeIfAbsent(mapPEU.get(prefixPattern), k -> new ArrayList<>()).add(prefixPattern);
											insertPattern(non_CHUSP_map, asuMap, prefixPattern);
										}
									 }
								 }
	 
								// existingPatterns.removeAll(patternsToRemove);
								 //CHUSP_map.removeAll(patternsToRemove);
								 if (isClosed) {
									 insertPattern(CHUSP_map, asuMap, extPattern);
								 } else {
									 insertPattern(non_CHUSP_map, asuMap, extPattern);
								 }
	 
								} 
							 
							 else 
							 { 
								 //if closed pattern does not contain extpattern support value insert directly
								 //CHUSP_map.computeIfAbsent(mapPEU.get(extPattern), k -> new ArrayList<>()).add(extPattern);
								 insertPattern(CHUSP_map, asuMap, extPattern);
							 }
					}			      
					else
					{
						 //if the prefixpattern and extpattern does not have same support
						 //CHUSP_map.computeIfAbsent(mapPEU.get(extPattern), k -> new ArrayList<>()).add(extPattern);
						 insertPattern(CHUSP_map, asuMap, extPattern);
						
								 
					}
				
				
			}  	
				       findHUSPs(extPattern, mapASU.get(extPattern), mapPEU.get(extPattern), 
						  icanpromisingItems, scanpromisingItems, asuMap);
		}
			
	}
		
		// *Recursive Call for s-extension*
		for (Integer item : _scanpromisingItems)
		{
			asuMap = new HashMap<>();
			//HashMap<Integer, List<ArrayList<Integer>>> supportPatterns = new HashMap<>();

			calcIUListPEUandASU(prefixPattern, item, 's', asuMap);
	
			ArrayList<Integer> extPattern = new ArrayList<>(prefixPattern);
			extPattern.add(-1);
			extPattern.add(item);
			if(mapASU.get(extPattern) != null)
			{
			    // Closed Patterns Algorithm condition
			    //t=prefixPattern t'=extPattern
			/* 	System.out.println("====== ASU Map Values ======");
				for (Map.Entry<Integer, Integer> entry : asuMap.entrySet()) {
					System.out.println("Key: " + entry.getKey() + " -> Value: " + entry.getValue());
				}
			*/	
			    if(mapASU.get(extPattern) >= minUtility && asuMap.size()>= minSupport)
				{
					highUtilityPatterns.add(extPattern);
					
					if ( asuMap.size() == _asuMap.size() ) 
					{
					
						//CHUSP_map.remove(prefixPattern);
						List<Integer> keysToRemove = new ArrayList<>();
						for (Map.Entry<Integer, List<ArrayList<Integer>>> entry : CHUSP_map.entrySet()) {
							if (entry.getValue().contains(prefixPattern)) {
								entry.getValue().remove(prefixPattern);
								if (entry.getValue().isEmpty()) {
									keysToRemove.add(entry.getKey());
								}
							}
						}
						for (Integer key : keysToRemove) {
							CHUSP_map.remove(key);
						}
						
							 //non_CHUSP_map.computeIfAbsent(mapPEU.get(prefixPattern), k -> new ArrayList<>()).add(prefixPattern);
							 insertPattern(non_CHUSP_map, asuMap, prefixPattern);
	 
							 //adding t'=extpattern into CHUSP_map
	 
							 if (CHUSP_map.containsKey(asuMap.size())) 
							 { 
								 int newSupport = asuMap.size();
								 List<ArrayList<Integer>> existingPatterns = CHUSP_map.get(newSupport);	
								 boolean isClosed = true;
								// List<ArrayList<Integer>> patternsToRemove = new ArrayList<>();
	 
								 for (ArrayList<Integer> existingPattern : existingPatterns) {
									 if (isSubsequence(extPattern, existingPattern)) {
										 // extPattern is a subsequence, not closed
										 isClosed = false;
										 break;
									 } else if (isSupersequence(extPattern, existingPattern)) {
										 // extPattern is a superpattern, remove the old one
										// patternsToRemove.add(existingPattern);
										int support = _asuMap.size();  // Get the support value

										List<ArrayList<Integer>> listOfPatterns = CHUSP_map.get(support);
										if (listOfPatterns != null) {
											listOfPatterns.removeIf(patternsToRemove -> pattern.equals(existingPattern));
										
											//  remove the key if the list becomes empty
											if (listOfPatterns.isEmpty()) {
												CHUSP_map.remove(support);
											}
										}
										
										 if(!non_CHUSP_map.containsValue(prefixPattern)){
											//CHUSP_map.computeIfAbsent(mapPEU.get(prefixPattern), k -> new ArrayList<>()).add(prefixPattern);
											insertPattern(non_CHUSP_map, asuMap, prefixPattern);
										}
									 }
								 }
	 
								// existingPatterns.removeAll(patternsToRemove);
								 //CHUSP_map.removeAll(patternsToRemove);
								 if (isClosed) {
									 insertPattern(CHUSP_map, asuMap, extPattern);
								 } else {
									 insertPattern(non_CHUSP_map, asuMap, extPattern);
								 }
	 
								} 
							 
							 else 
							 { 
								 //if closed pattern does not contain extpattern support value insert directly
								 //CHUSP_map.computeIfAbsent(mapPEU.get(extPattern), k -> new ArrayList<>()).add(extPattern);
								 insertPattern(CHUSP_map, asuMap, extPattern);
							 }
					}			      
					else
					{
						 //if the prefixpattern and extpattern does not have same support
						 //CHUSP_map.computeIfAbsent(mapPEU.get(extPattern), k -> new ArrayList<>()).add(extPattern);
						 insertPattern(CHUSP_map, asuMap, extPattern);
							
								 
					}
				
				
			}  	
					findHUSPs(extPattern, mapASU.get(extPattern), mapPEU.get(extPattern), 
					   icanpromisingItems, scanpromisingItems, asuMap);
			}
			 
		}
	}
	
	/*private void writeResultsToFile(String outputPath) {
	    String lineSeparator = System.getProperty("line.separator");

	    try (FileOutputStream fos = new FileOutputStream(outputPath)) {
	    	for(ArrayList<Integer> pattern : highUtilityPatterns) {
	    		for(Integer item : pattern) {
	    			fos.write(Integer.toString(item).getBytes());
	    			fos.write(" ".getBytes());
	    		}
	    		fos.write(("   #UTIL: " + mapASU.get(pattern)).getBytes());
				fos.write(lineSeparator.getBytes());
	    	}
			fos.flush();
	    } catch (Exception e2) {
	      e2.printStackTrace();
	    }
	}*/
	private void writeResultsToFile(String outputPath) {
		String lineSeparator = System.getProperty("line.separator");
	    int closed=0,nonclosed=0;
		try (FileOutputStream fos = new FileOutputStream(outputPath)) {
			// Write high utility patterns
			fos.write(("minUtility: " + minUtility + "minsupport: "+minSupport).getBytes());
            fos.write(("HighUtility Patterns " ).getBytes());
			fos.write(lineSeparator.getBytes());
			for (ArrayList<Integer> pattern : highUtilityPatterns) {
				for (Integer item : pattern) {
					fos.write(Integer.toString(item).getBytes());
					fos.write(" ".getBytes());
				}
				fos.write(("   #UTIL: " + mapASU.get(pattern)).getBytes());

				fos.write(lineSeparator.getBytes());
				
			}
			fos.write(("Highutility Patterns size: " + highUtilityPatterns.size()).getBytes());
			fos.write(lineSeparator.getBytes());
			//supportMap
			// Write full supportMap data

			for (Map.Entry<Integer, ArrayList<ArrayList<Integer>>> entry : supportMap.entrySet()) {
				// Access key
				Integer support = entry.getKey();
				
				// Access value (which is a list of patterns)
				ArrayList<ArrayList<Integer>> patterns = entry.getValue();
			
				// Print the support value
				fos.write(("Support: " + support).getBytes());
				fos.write(lineSeparator.getBytes());
			
				// Iterate over the list of patterns
				for (ArrayList<Integer> pattern : patterns) {
					for (Integer item : pattern) {
						fos.write(Integer.toString(item).getBytes());
						fos.write(" ".getBytes());
					}
					fos.write(lineSeparator.getBytes()); // new line after each pattern
				}
				fos.write(lineSeparator.getBytes()); // extra line between support groups
			}
			
			
			fos.write(lineSeparator.getBytes());
			//supportMap
			// Write full supportMap data
			// Now write CHUSP_map data
			fos.write(("Closed Patterns " ).getBytes());
			fos.write(lineSeparator.getBytes());
			for (Map.Entry<Integer, List<ArrayList<Integer>>> entry : CHUSP_map.entrySet()) {
				int support = entry.getKey();
				List<ArrayList<Integer>> patterns = entry.getValue();
	
				fos.write(("Support: " + support).getBytes());
				fos.write(lineSeparator.getBytes());
				fos.write("Patterns:".getBytes());
				
				fos.write(lineSeparator.getBytes());
	
				for (ArrayList<Integer> pattern : patterns) {
					closed++;
					for (Integer val : pattern) {
						// Replace 0 with -1
						fos.write(Integer.toString(val == 0 ? -1 : val).getBytes());
						fos.write(" ".getBytes());
					}
					fos.write(("   #UTIL: " + mapASU.get(pattern)).getBytes());
					fos.write(lineSeparator.getBytes());
				}
				fos.write(lineSeparator.getBytes()); // extra space between groups
			}
			fos.write(("closed Patterns size: " + closed).getBytes());
			fos.write(lineSeparator.getBytes());
			//non closed
			fos.write(("Non Closed Patterns Patterns " ).getBytes());
			fos.write(lineSeparator.getBytes());
			for (Map.Entry<Integer, List<ArrayList<Integer>>> entry : non_CHUSP_map.entrySet()) {
				int support = entry.getKey();
				List<ArrayList<Integer>> patterns = entry.getValue();
	
				fos.write(("Support: " + support).getBytes());
				fos.write(lineSeparator.getBytes());
				fos.write("Patterns:".getBytes());
				
				fos.write(lineSeparator.getBytes());
	
				for (ArrayList<Integer> pattern : patterns) {
					nonclosed++;
					for (Integer val : pattern) {
						
						// Replace 0 with -1
						fos.write(Integer.toString(val == 0 ? -1 : val).getBytes());
						fos.write(" ".getBytes());
					}
					fos.write(("   #UTIL: " + mapASU.get(pattern)).getBytes());
					fos.write(lineSeparator.getBytes());
				}
				fos.write(lineSeparator.getBytes()); // extra space between groups
				//fos.write(("NonClosed Patterns size: " + nonclosed).getBytes());
				fos.write(lineSeparator.getBytes()); 
			}
			fos.write(("NonClosed Patterns size: " + nonclosed).getBytes());
			fos.flush();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
}
