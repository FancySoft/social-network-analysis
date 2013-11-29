package com.fancy_software.accounts_matching.serverapp.match.lda;

import cc.mallet.types.Instance;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCsvIterator implements Iterator<Instance> {
    List<String> text;
    Iterator iterator;
    Pattern lineRegex;
    int uriGroup, targetGroup, dataGroup;
    String currentLine;
    int counter = 0;

    public MyCsvIterator(List<String> input, Pattern lineRegex, int dataGroup, int targetGroup, int uriGroup) {
        this.text = input;
        this.lineRegex = lineRegex;
        this.targetGroup = targetGroup;
        this.dataGroup = dataGroup;
        this.uriGroup = uriGroup;
        iterator = text.listIterator();
        if (dataGroup <= 0)
            throw new IllegalStateException("You must extract a data field.");
        try {
            if (iterator.hasNext())
                this.currentLine = (String) iterator.next();
            else
                this.currentLine = null;
        } catch (NoSuchElementException e) {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean hasNext() {
        return currentLine != null;
    }

    @Override
    public Instance next() {
        String uriStr = null;
        String data = null;
        String target = null;
        Matcher matcher = lineRegex.matcher(currentLine);
        if (matcher.find()) {
            if (uriGroup > 0)
                uriStr = matcher.group(uriGroup);
            if (targetGroup > 0)
                target = matcher.group(targetGroup);
            if (dataGroup > 0)
                data = matcher.group(dataGroup);
        } else {
            throw new IllegalStateException("Line #" + counter + " does not match regex:\n" +
                    currentLine);
        }

        String uri;
        if (uriStr == null) {
            uri = "csvline:" + counter;
        } else {
            uri = uriStr;
        }
        assert (data != null);
        Instance carrier = new Instance(data, target, uri, null);
        try {
            if (iterator.hasNext()) {
                this.currentLine = (String) iterator.next();
                counter++;
            } else
                this.currentLine = null;
        } catch (NoSuchElementException e) {
            throw new IllegalStateException();
        }
        return carrier;
    }

    @Override
    public void remove() {
        throw new IllegalStateException("This Iterator<Instance> does not support remove().");
    }
}
