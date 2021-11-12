package ru.job4j;

import java.time.LocalDateTime;

public class Post {

    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime localDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Post post = (Post) o;

        if (id != post.id) {
            return false;
        }
        if (link != null ? !link.equals(post.link) : post.link != null) {
            return false;
        }
        if (description != null ? !description.equals(post.description) : post.description != null) {
            return false;
        }
        return localDateTime != null ? localDateTime.equals(post.localDateTime) : post.localDateTime == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (localDateTime != null ? localDateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", link='" + link + '\''
                + ", description='" + description + '\''
                + ", localDateTime=" + localDateTime
                + '}';
    }
}
