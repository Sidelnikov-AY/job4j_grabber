package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private static Connection cn;
    private static LocalDateTime created = LocalDateTime.now();

    public AlertRabbit() {
    }

    public static void main(String[] args) {

        AlertRabbit ar= new AlertRabbit();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            int interval = ar.readProperties();
                data.put("postgresql", cn);
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(interval)
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(cn);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());

        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            cn = (Connection) context.getJobDetail().getJobDataMap().get("postgresql");
            Timestamp timestampFromLDT = Timestamp.valueOf(created);
            try (PreparedStatement statement =
                         cn.prepareStatement("insert into rabbit(created_date) values(?);")) {
                 statement.setTimestamp(1, timestampFromLDT);
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int readProperties() throws FileNotFoundException {
        Properties config = new Properties();
        try (InputStream io = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            config.load(io);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        String value = config.getProperty("rabbit.interval");
        return Integer.parseInt(value);

    }
}