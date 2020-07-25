// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    HashSet<String> attendeesSet = new HashSet<>(request.getAttendees());
    class Point {  // a time point
      public int time;
      // delta == 1 for a start point, delta = -1 for a end point
      // delta means the change of the count of current holding events
      public int delta;
      public Point(int time, int delta) {
        this.time = time;
        this.delta = delta;
      }
    }
    ArrayList<Point> keyPoint = new ArrayList<>();
    for(Event event: events) {
      boolean valid = false;
      for(String attendee: event.getAttendees()) {
        if(attendeesSet.contains(attendee)) {
          valid = true;
          break;
        }
      }
      if(valid) {
        TimeRange range = event.getWhen();
        keyPoint.add(new Point(range.start(), 1));
        keyPoint.add(new Point(range.end(), -1));
      }
    }
    keyPoint.sort((Point x, Point y) -> {
      if(x.time == y.time) {
        return x.delta < y.delta ? -1 : x.delta == y.delta ? 0 : 1;
      } else {
        return x.time < y.time ? -1 : 1;
      }
    });

    ArrayList<TimeRange> validTimeRanges = new ArrayList<>();
    int begin = 0, depth = 0;
    for(Point point: keyPoint) {
      if(point.delta == -1) {
        depth--;
        if(depth == 0) {
          begin = point.time;
        }
      } else {
        depth++;
        if(depth == 1 && point.time - begin >= request.getDuration()) {
          validTimeRanges.add(TimeRange.fromStartEnd(begin, point.time, false));
        }
      }
    }
    // add the final time.
    if(TimeRange.END_OF_DAY - begin >= request.getDuration()) {
      validTimeRanges.add(TimeRange.fromStartEnd(begin, TimeRange.END_OF_DAY, true));
    }
    
    return validTimeRanges;
  }
}
