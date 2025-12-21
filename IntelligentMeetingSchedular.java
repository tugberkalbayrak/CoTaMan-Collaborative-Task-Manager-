package com.example.Services.ArchiveService.CalendarService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.example.Entity.*;

public class IntelligentMeetingSchedular {
  
  public List<TimeSlot> findCommonSlots(Group group, Duration meetingDuration, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        List<User> members = group.getMembers();
        List<TimeSlot> commonSlots = new ArrayList<>();
        
        List<TimeSlot> busySlots = new ArrayList<>();

        for (User member : members) {

            for (CalendarEvent event : member.getSchedule()) {
                if (event.getEndTime().isAfter(rangeStart) && event.getStartTime().isBefore(rangeEnd)) {
                    if (event.getImportance() == Importance.MUST) {

                        busySlots.add(new TimeSlot(event.getStartTime(), event.getEndTime()));
                    }
                }
            }
        }
        busySlots.sort(Comparator.comparing(TimeSlot::getStart));
        List<TimeSlot> mergedBusy = new ArrayList<>();
        if (!busySlots.isEmpty()) {
            TimeSlot current = busySlots.get(0);
            for (int i = 1; i < busySlots.size(); i++) {
                TimeSlot next = busySlots.get(i);

                if (current.getEnd().isAfter(next.getStart()) || current.getEnd().isEqual(next.getStart())) {

                    if (next.getEnd().isAfter(current.getEnd())) {
                        current = new TimeSlot(current.getStart(), next.getEnd());
                    }
                } else {
                    mergedBusy.add(current);
                    current = next;
                }
            }
            mergedBusy.add(current);
        }
        LocalDateTime pointer = rangeStart;
        for (TimeSlot busy : mergedBusy) {
            if (Duration.between(pointer, busy.getStart()).compareTo(meetingDuration) >= 0) {

                commonSlots.add(new TimeSlot(pointer, busy.getStart()));
            }
            if (busy.getEnd().isAfter(pointer)) {
                pointer = busy.getEnd();
            }
        }
        if (Duration.between(pointer, rangeEnd).compareTo(meetingDuration) >= 0) {
            commonSlots.add(new TimeSlot(pointer, rangeEnd));
        }

        return commonSlots;
    }



}
