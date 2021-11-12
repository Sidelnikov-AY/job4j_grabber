package ru.job4j.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {
    final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
    final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private static final Map<String, String> MONTHS = Map.ofEntries(
            Map.entry("янв", "01"),
            Map.entry("фев", "02"),
            Map.entry("мар", "03"),
            Map.entry("апр", "04"),
            Map.entry("май", "05"),
            Map.entry("июн", "06"),
            Map.entry("июл", "07"),
            Map.entry("авг", "08"),
            Map.entry("сен", "09"),
            Map.entry("окт", "10"),
            Map.entry("ноя", "11"),
            Map.entry("дек", "12")
    );

    @Override
    public LocalDateTime parse(String parse) {
        LocalDate date;
        LocalTime time;
        String[] temp = parse.split(",");
        String[] tempTime = temp[1].split(":");
        String tempHour = tempTime[0].trim();
        String tempMin = tempTime[1];
        time = LocalTime.parse(tempHour + ":" + tempMin, timeFormatter);

        if (temp[0].equals("вчера")) {
            date = LocalDate.now().minus(Period.ofDays(1));
        } else if (temp[0].equals("сегодня")) {
            date = LocalDate.now();
        } else {
            String[] tempDate = temp[0].split(" ");
            String tempDay = tempDate[0];
            String tempMonth = MONTHS.get(tempDate[1]);
            String tempYear = tempDate[2];
            date = LocalDate.parse("20" + tempYear + "-" + tempMonth + "-" + tempDay, dateFormatter);
        }
        return date.atTime(time);
    }
}
