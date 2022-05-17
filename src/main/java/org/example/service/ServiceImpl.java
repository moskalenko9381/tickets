package org.example.service;

import com.google.gson.Gson;
import org.example.Main;
import org.example.model.Answer;
import org.example.model.Flight;
import org.example.model.TicketsList;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class ServiceImpl {
    public static final int PERCENTILE = 90;
    Logger logger = Logger.getLogger(ServiceImpl.class.getName());

    public void process() {
        Answer answer = new Answer();
        TicketsList flights = parseJson();
        List<Integer> timesInMinutes = getTimesArray(flights.tickets)
                .stream()
                .sorted()
                .collect(Collectors.toList());
        double average = getAverageTime(timesInMinutes);
        answer.setAverageTime(String.valueOf(convertToLocalTime((int) average)));
        logger.log(Level.INFO, "Average time of flight: " + answer.getAverageTime());
        answer.setPercentile(String.valueOf(getPercentile(timesInMinutes)));
        logger.log(Level.INFO, "90th percentile: " + answer.getPercentile());
    }

    public TicketsList parseJson() {
        Gson gson = new Gson();
        TicketsList tickets = new TicketsList();
        ClassLoader loader = Main.class.getClassLoader();
        try (Reader reader = new FileReader(loader.getResource("static/tickets.json").getPath())) {
            tickets = gson.fromJson(reader, TicketsList.class);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error opening file", e);
        }
        return tickets;
    }

    public LocalTime getPercentile(List<Integer> times) {
        int i = times.size() * PERCENTILE / 100 - 1;
        return convertToLocalTime(times.get(i));
    }

    public List<Integer> getTimesArray(List<Flight> tickets) {
        List<Integer> timeInMinutes = new ArrayList<>();
        for (Flight ticket : tickets) {
            LocalDateTime departure = convertToLocalDateTime(ticket.getDeparture_date(), ticket.getDeparture_time());
            LocalDateTime arrival = convertToLocalDateTime(ticket.getArrival_date(), ticket.getArrival_time());
            int minutes = (int) Math.abs(ChronoUnit.MINUTES.between(arrival, departure));
            timeInMinutes.add(minutes);
        }
        return timeInMinutes;
    }

    public double getAverageTime(List<Integer> times) {
        OptionalDouble average = times
                .stream()
                .mapToDouble(s -> s)
                .average();
        return average.isPresent() ? average.getAsDouble() : 0;
    }

    public LocalTime convertToLocalTime(int timeInMinutes) {
        int h = timeInMinutes / 60;
        String hours = (h < 10) ? ("0" + h) : String.valueOf(h);
        int m = timeInMinutes % 60;
        String minutes = (m < 10) ? ("0" + m) : String.valueOf(m);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime dateTime = LocalTime.parse(hours + ":" + minutes, formatter);
        return dateTime;
    }

    public LocalDateTime convertToLocalDateTime(String date, String time) {
        if (time.charAt(1) == ':') {
            time = "0" + time;
        }
        String dateInString = date + " " + time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yy HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateInString, formatter);
        return dateTime;
    }
}
