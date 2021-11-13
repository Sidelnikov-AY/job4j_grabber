package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.Post;
import ru.job4j.utils.SqlRuDateTimeParser;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class SqlRuParse {

    public Post postDetail(int id, String title, String link, String description, LocalDateTime created) {
        return new Post(id, title, link, description, created);
    }

    public static void main(String[] args) throws Exception {
        List<Post> postList = new ArrayList<>();
        SqlRuParse srp = new SqlRuParse();
        int posts = 0;
        for (int page = 1; page <= 5; page++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + page).get();
                Elements row = doc.select(".postslisttopic");
                for (Element td : row) {
                    Element href = td.child(0);
                    Element parent = td.parent();
                    System.out.println(href.attr("href"));
                    System.out.println(href.text());
                    SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
                    System.out.println(timeParser.parse(parent.child(5).text()));
                    Document post = Jsoup.connect(href.attr("href")).get();
                    Elements table = post.select(".msgTable");
                    for (Element td2 : table) {
                        Element description = td2.child(0).child(1).child(1);
                        System.out.println(description.text());
                        Element date = td2.child(0).child(2).child(0);
                        String[] dateArr = date.text().split("\\[");
                        System.out.println(timeParser.parse(dateArr[0].trim()));

                        postList.add(srp.postDetail(posts++, href.text(), href.attr("href"),
                                description.text(), timeParser.parse(dateArr[0].trim())));

                        break;
                    }
                }
            }

        for (Post post: postList) {
            System.out.println(post.toString());

        }
        System.out.println(postList.size());
    }
}