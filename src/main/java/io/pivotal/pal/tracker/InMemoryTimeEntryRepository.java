package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private ArrayList<TimeEntry> timeEntries=new ArrayList<TimeEntry>();

    public TimeEntry create(TimeEntry timeEntry) {
        timeEntries.add(timeEntry);
        if(timeEntry.getId()==0){
            timeEntry.setId(timeEntries.size());
        }
        return timeEntry;
    }

    public TimeEntry find(long timeEntryId) {
        //Look at casting
        try {
            return timeEntries.get((int) timeEntryId - 1);
        }
        catch (IndexOutOfBoundsException e){
            return null;
        }

    }

    public List<TimeEntry> list() {
        return timeEntries;
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        try{
        delete(id);
        return create(timeEntry);
        }
        catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public TimeEntry delete(long id) {
        return timeEntries.remove((int) id-1);
    }
}
