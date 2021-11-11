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

    public AlertRabbit() {
    }

    public static void main(String[] args) {

        AlertRabbit ar = new AlertRabbit();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            int interval = Integer.parseInt(ar.readProperties().getProperty("rabbit.interval"));
            try (Connection cn = ar.initCon(ar.readProperties())) {
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
            } catch (Exception se) {
                se.printStackTrace();
            }

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
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("postgresql");
            Timestamp timestampFromLDT = Timestamp.valueOf(LocalDateTime.now());
            try (PreparedStatement statement =
                         cn.prepareStatement("insert into rabbit(created_date) values(?);")) {
                statement.setTimestamp(1, timestampFromLDT);
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Properties readProperties() throws IOException {
        Properties config = new Properties();
        try (InputStream io = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            config.load(io);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    public Connection initCon(Properties properties) throws ClassNotFoundException, SQLException {
        Class.forName(properties.getProperty("driver-class-name"));
        Connection cn = null;
        try {
            cn = DriverManager.getConnection(
                    properties.getProperty("url"),
                    properties.getProperty("username"),
                    properties.getProperty("password")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cn;
    }
}