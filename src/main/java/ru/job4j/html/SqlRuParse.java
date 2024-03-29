package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.Post;
import ru.job4j.grabber.Parse;
import ru.job4j.utils.DateTimeParser;
import ru.job4j.utils.SqlRuDateTimeParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SqlRuParse implements Parse {

    private final DateTimeParser dateTimeParser;
    private List<Post> posts;

    public SqlRuParse(DateTimeParser dateTimeParser) {

        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        try {
        for (int page = 1; page <= 5; page++) {
            Document doc = Jsoup.connect(link + page).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                String hrefString = td.child(0).attr("href");
                if (hrefString.toLowerCase().contains("java") && !hrefString.toLowerCase().contains("javascript")) {
                    posts.add(detail(hrefString));
                }
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post detail(String link) {
        Post rsl = null;
        try {
            Document post = Jsoup.connect(link).get();
            Elements table = post.select(".msgTable");
            for (Element td2 : table) {
                Element description = td2.child(0).child(1).child(1);
                Element title = td2.child(0).child(0).child(0);
                Element date = td2.child(0).child(2).child(0);
                String[] dateArr = date.text().split("\\[");
                rsl = new Post(0, title.text(), link,
                        description.text(), this.dateTimeParser.parse(dateArr[0].trim()));
                break;
            }
        } catch (IOException e) {
                e.printStackTrace();
            }
        return rsl;
    }


    public static void main(String[] args) {
        DateTimeParser dtp = new SqlRuDateTimeParser();
        SqlRuParse srp = new SqlRuParse(dtp);
        srp.posts = srp.list("https://www.sql.ru/forum/job-offers/");
        for (Post p: srp.posts) {
            System.out.println(p.toString());
        }

    }
}