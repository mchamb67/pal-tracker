package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {
   private  TimeEntryRepository timeEntryRepository;

    public TimeEntryController(TimeEntryRepository timeEntryRepository){
     this.timeEntryRepository=timeEntryRepository;

    }

    @PostMapping("/time-entries")
    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {
        return new ResponseEntity<>(
                this.timeEntryRepository.create(timeEntryToCreate),
                HttpStatus.CREATED);
    }
    @RequestMapping("/time-entries/{nonExistentTimeEntryId}")
    public ResponseEntity<TimeEntry> read(@PathVariable long nonExistentTimeEntryId) {
        TimeEntry test =  this.timeEntryRepository.find(nonExistentTimeEntryId);

        if(test!=null) {
            return new ResponseEntity<TimeEntry>(
                    test,
                    HttpStatus.OK);
        }
        return new ResponseEntity<TimeEntry>(
                test,
                HttpStatus.NOT_FOUND);
    }
     @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {
        return new ResponseEntity<List<TimeEntry>>(
                this.timeEntryRepository.list(),
                HttpStatus.OK);
    }
     @PutMapping("/time-entries/{timeEntryId}")
    public ResponseEntity update(@PathVariable long timeEntryId, @RequestBody TimeEntry expected) {
        TimeEntry test =  this.timeEntryRepository.update(timeEntryId,expected);
        if(test!=null) {
            return new ResponseEntity<TimeEntry>(
                    test,
                    HttpStatus.OK);
        }
        return new ResponseEntity<TimeEntry>(
                test,
                HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/time-entries/{timeEntryId}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long timeEntryId) {
        return new ResponseEntity<TimeEntry>(
                this.timeEntryRepository.delete(timeEntryId),
                HttpStatus.NO_CONTENT);
    }
}
