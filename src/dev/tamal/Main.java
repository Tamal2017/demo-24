package dev.tamal;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;

public class Main {
    public static void main(String[] args) {
        List<BlogPost> blogPosts = createSampleBlogPost();

        System.out.println("======= Find all posts by Category =======");
        //postByCategory(blogPosts, "Java");

        System.out.println("======= Befor JDK 24: nested collections =======");
        //nestedCollectors(blogPosts);

        System.out.println("======= After JDK 24: Gatherers provide useful intermediate operations =======");
        //fixedWindowExample(blogPosts);
        groupByLimit(blogPosts, 2);

    }

    private static void fixedWindowExample(List<BlogPost> blogPosts) {
        // Group post in a batch of 3
        blogPosts.stream()
                .limit(6)
                .gather(Gatherers.windowFixed(3))
                .forEach(batch -> {
                    System.out.println("\nBatch: ");
                    batch.forEach(blogPost -> System.out.println(" - " + blogPost.title()));
                });
    }

    private static void groupByLimit(List<BlogPost> blogPosts, int limit) {
        // Recent post by category
        Map<String, List<BlogPost>> blogPostByCategory = blogPosts.stream()
                .gather(BlogGatherers.groupByLimit(
                        BlogPost::category,
                        limit,
                        Comparator.comparing(BlogPost::publishedDate).reversed()
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        printRecentPostByCategory(blogPostByCategory);
    }

    // Prior to JDK 24 :: How to group by Category, order by publishedDate and limit to 3 most recent post
    private static void nestedCollectors(List<BlogPost> blogPosts) {
        var recentPostByCategory1 = blogPosts.stream()
                .collect(Collectors.groupingBy(BlogPost::category,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                posts -> posts.stream()
                                        .sorted(Comparator.comparing(BlogPost::publishedDate).reversed())
                                        .limit(3)
                                        .toList()
                        )));
        var recentPostByCategory2 = blogPosts.stream()
                .collect(Collectors.groupingBy(BlogPost::category))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparing(BlogPost::publishedDate).reversed())
                                .limit(3)
                                .toList()
                ));
        printRecentPostByCategory(recentPostByCategory1);
        printRecentPostByCategory(recentPostByCategory2);
    }

    private static void printRecentPostByCategory(Map<String, List<BlogPost>> recentPostByCategory) {
        recentPostByCategory.forEach((String category, List<BlogPost> posts) -> {
            System.out.println("\n Category : " + category);
            posts.forEach(System.out::println);
        });
    }

    private static void postByCategory(List<BlogPost> blogPosts, String category) {
        List<BlogPost> postList = blogPosts.stream()
                .filter(post -> post.category().equals(category))
                .sorted(Comparator.comparing(BlogPost::publishedDate).reversed())
                .limit(3)
                .toList();
        System.out.println("\n Post by category: " + category);
        postList.forEach(System.out::println);
    }

    private static List<BlogPost> createSampleBlogPost() {
        return List.of(
                new BlogPost(
                        1L,
                        "Getting starting with JDK 24",
                        "Sam Smith",
                        "Learn how to use java 24 Gatherers",
                        "Java",
                        LocalDateTime.of(2025, 4, 10, 10, 10)),
                new BlogPost(
                        2L,
                        "Read people like a book",
                        "Patrick King",
                        "How to analyse, understand and predict people's emotions",
                        "Psychology",
                        LocalDateTime.of(2020, 4, 7, 14, 45)),
                new BlogPost(
                        3L,
                        "Getting starting Js framework",
                        "Armand Nkoa",
                        "Learn how to use js framework to build SPA web app",
                        "JavaScript",
                        LocalDateTime.of(2022, 5, 11, 11, 10)),
                new BlogPost(
                        4L,
                        "Basic of Spring framework",
                        "Daniel Sam",
                        "A modern framework for java developers",
                        "Java",
                        LocalDateTime.of(2024, 12, 11, 10, 50)),
                new BlogPost(
                        5L,
                        "Lombok plugin",
                        "Lombok",
                        "Eliminate boilerplate code",
                        "Plugin",
                        LocalDateTime.of(2021, 7, 20, 1, 15)),
                new BlogPost(
                        6L,
                        "Error Handling in Java app",
                        "Sam Smith",
                        "Manage error properly in your Java App",
                        "Java",
                        LocalDateTime.of(2025, 1, 31, 20, 10)),
                new BlogPost(
                        7L,
                        "Java for beginners",
                        "Joseph Tamo",
                        "Introduction to java language",
                        "Java",
                        LocalDateTime.of(2024, 2, 28, 10, 30)),
                new BlogPost(
                        8L,
                        "Angular Js",
                        "Google Inc",
                        "Introduction to Angular & Typescript",
                        "JavaScript",
                        LocalDateTime.of(2021, 3, 20, 10, 30)),
                new BlogPost(
                        9L,
                        "Vue Js",
                        "Google Inc",
                        "Introduction to Angular & Typescript",
                        "JavaScript",
                        LocalDateTime.of(2021, 3, 20, 10, 30))
        );
    }

}