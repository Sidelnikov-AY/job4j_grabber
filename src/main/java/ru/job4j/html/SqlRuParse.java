package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.Post;
import ru.job4j.utils.SqlRuDateTimeParser;
import java.io.IOException;


public class SqlRuParse {

    int id = 0;

    public Post loadPostDetail(String href) throws IOException {
        Post rsl = null;
        SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
        Document post = Jsoup.connect(href).get();
        Elements table = post.select(".msgTable");
        for (Element td2 : table) {
            Element description = td2.child(0).child(1).child(1);
            Element title = td2.child(0).child(0).child(0);
            Element date = td2.child(0).child(2).child(0);
            String[] dateArr = date.text().split("\\[");
            rsl = new Post(++id, title.text(), href,
                    description.text(), timeParser.parse(dateArr[0].trim()));
            break;
        }
        return rsl;
    }


    public static void main(String[] args) throws Exception {
        SqlRuParse srp = new SqlRuParse();
        String href = "https://www.sql.ru/forum/1339885/sistemnyy-analitik-metro-kurskaya-ot-200k-net";
        System.out.println(srp.loadPostDetail(href).toString());
    }
}