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

    int id = 0;
    private final DateTimeParser dateTimeParser;
    private List<Post> posts;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> posts = new ArrayList<>();
        Document doc = Jsoup.connect(link).get();
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            String hrefString = td.child(0).attr("href");
            posts.add(detail(hrefString));
        }
        return posts;
    }

    @Override
    public Post detail(String link) throws IOException {
        Post rsl = null;
        SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
        Document post = Jsoup.connect(link).get();
        Elements table = post.select(".msgTable");
        for (Element td2 : table) {
            Element description = td2.child(0).child(1).child(1);
            Element title = td2.child(0).child(0).child(0);
            Element date = td2.child(0).child(2).child(0);
            String[] dateArr = date.text().split("\\[");
            rsl = new Post(++id, title.text(), link,
                    description.text(), timeParser.parse(dateArr[0].trim()));
            break;
        }
        return rsl;
    }


    public static void main(String[] args) throws Exception {
        DateTimeParser dtp = new SqlRuDateTimeParser();
        SqlRuParse srp = new SqlRuParse(dtp);
        srp.posts = srp.list("https://www.sql.ru/forum/job-offers/");
        for (Post p: srp.posts) {
            System.out.println(p.toString());
        }

    }
}