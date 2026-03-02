package lk.icbt.findit.entity;

/**
 * Type of outlet schedule.
 * NORMAL = weekly recurring (day_of_week); EMERGENCY/DAILY = single date (special_date);
 * TEMPORARY = date range (start_date, end_date).
 */
public enum ScheduleType {
    NORMAL,
    EMERGENCY,
    TEMPORARY,
    DAILY
}
