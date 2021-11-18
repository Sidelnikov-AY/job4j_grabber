package ru.job4j.grabber;

import ru.job4j.Post;
import ru.job4j.html.SqlRuParse;
import ru.job4j.utils.DateTimeParser;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
    }

    public static Properties readProperties() throws IOException {
        Properties config = new Properties();
        try (InputStream io = PsqlStore.class.getClassLoader().getResourceAsStream("psqlstore.properties")) {
            config.load(io);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    public Post resultSetToPost(ResultSet rs) throws SQLException {
        return new Post(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("link"),
                rs.getTimestamp("created").toLocalDateTime()
        );
    }

    @Override
    public void save(Post post) {
        Timestamp timestampFromLDT = Timestamp.valueOf(post.getCreated());
        try (PreparedStatement statement =
                     cnn.prepareStatement("insert into post(name, description, link, created) values(?, ?, ?, ?);",
                             Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, timestampFromLDT);
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(resultSetToPost(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement =
                     cnn.prepareStatement("select * from post where id = ?;")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                post = resultSetToPost(resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }


    public static void main(String[] args) throws SQLException, IOException {
        PsqlStore grab = new PsqlStore(readProperties());
        DateTimeParser dtp = new SqlRuDateTimeParser();
        SqlRuParse srp = new SqlRuParse(dtp);
        List<Post> posts = srp.list("https://www.sql.ru/forum/job-offers/");
        for (Post p: posts) {
            grab.save(p);
        }
        System.out.println(grab.getAll());
        System.out.println(grab.findById(230));

    }
}