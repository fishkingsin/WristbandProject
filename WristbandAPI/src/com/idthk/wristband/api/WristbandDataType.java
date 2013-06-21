package com.idthk.wristband.api;

public class WristbandDataType {
	public class UserProfile {
		public int gender;
		public int birthyear;
		public int birthmonth;
		public int weight;
		public int height;
	}
	public class ActivityTarget {
		public int duration;
		public int toggle;
		public int step;
		public int distance;
		public int calories;
	}
	public class SleepTarget {
		public int weekday_weak_hour;
		public int weekday_weak_minute;
		public int weekend_weak_hour;
		public int weekend_weak_minute;
		public int toggle;
	}
}
