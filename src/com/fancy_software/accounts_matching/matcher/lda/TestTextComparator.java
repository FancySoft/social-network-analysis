package com.fancy_software.accounts_matching.matcher.lda;

import cc.mallet.pipe.*;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class TestTextComparator {

    public static HashMap<Double, String[]> getLocalRes(List<String> text) throws Exception {
        if (text.size() == 0)
            return null;
        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add(new CharSequenceLowercase());
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        // pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
        pipeList.add(new TokenSequenceRemoveStopwords(new File("stoplists/ru.txt"), "CP1251", false, false, false));
        pipeList.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipeList));
        instances.addThruPipe(new MyCsvIterator(text, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                3, 2, 1)); // data, label, name fields
        // Create a com.fancy_software.accounts_matching.model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is
        int numTopics = 2;
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
        model.addInstances(instances);
        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(2);
        // Run the com.fancy_software.accounts_matching.model for 50 iterations and stop (this is for testing only,
        //  for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(50);
        model.estimate();
        // Show the words and topics in the first instance
        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();
        //FeatureSequence tokens = (FeatureSequence) com.fancy_software.accounts_matching.model.getData().get(0).instance.getData();
        //LabelSequence topics = com.fancy_software.accounts_matching.model.getData().get(0).topicSequence;
        //Formatter out = new Formatter(new StringBuilder(), Locale.US);
        // Estimate the topic distribution of the first instance,
        //  given the current Gibbs state.
        double[] topicDistribution = model.getTopicProbabilities(0);
        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        HashMap<Double, String[]> result = new HashMap<Double, String[]>();
        // Show top 5 words in topics with proportions for the first document
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
            int rank = 0;
            String[] res = new String[3];
            while (iterator.hasNext() && rank < 3) {
                IDSorter idCountPair = iterator.next();
                res[rank] = (String) dataAlphabet.lookupObject(idCountPair.getID());
                rank++;
            }
            result.put(topicDistribution[topic], res);
        }
        return result;
    }

    public static double compare(Map<Double, String[]> map1, Map<Double, String[]> map2) {
        double result = 0;
        for (double d1 : map1.keySet())
            for (double d2 : map2.keySet())
                result += (((d1 + d2) / 2) * check(map1.get(d1), map2.get(d2)));
        return 1 / result;
    }

    public static int check(String[] words1, String[] words2) {
        int counter = 0;
        for (String s1 : words1)
            for (String s2 : words2)
                if (s1.equals(s2)) {
                    counter++;
                    break;
                }
        return counter;
    }

}