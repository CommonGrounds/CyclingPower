package dev.java4now.web.model;

import java.util.LinkedList;
import java.util.ListIterator;

// Navigable list implementation
public class NavigableActivityLinkedList {
    private LinkedList<ActivityEntry> entries = new LinkedList<>();
    private ListIterator<ActivityEntry> iterator;

    public static class ActivityEntry {
        private String key;
        private CyclingActivity value;

        public ActivityEntry(String key, CyclingActivity value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() { return key; }
        public CyclingActivity getValue() { return value; }
    }

    public NavigableActivityLinkedList() {
        iterator = entries.listIterator();
    }

    public void add(String key, CyclingActivity value) {
        entries.add(new ActivityEntry(key, value));
        iterator = entries.listIterator(); // Reset to start
    }

    public CyclingActivity get(String key) {
        for (ActivityEntry entry : entries) {
            if (entry.getKey().equals(key)) return entry.getValue();
        }
        return null;
    }

    public void clear() {
        entries.clear();
        iterator = entries.listIterator();
    }

    public int size() {
        return entries.size();
    }

    public ActivityEntry next() {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public ActivityEntry previous() {
        if (iterator.hasPrevious()) {
            return iterator.previous();
        }
        return null;
    }

    public ActivityEntry current() {
        // ListIterator doesn’t directly support "current", so we use peek-like logic
        if (iterator.hasNext()) {
            ActivityEntry next = iterator.next();
            iterator.previous(); // Move back to maintain position
            return next;
        } else if (iterator.hasPrevious()) {
            ActivityEntry prev = iterator.previous();
            iterator.next(); // Move forward to maintain position
            return prev;
        }
        return null;
    }

    public LinkedList<ActivityEntry> getEntries() {
        return entries;
    }
}
