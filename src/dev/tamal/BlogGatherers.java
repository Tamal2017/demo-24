package dev.tamal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Gatherer;

public final class BlogGatherers {
    private BlogGatherers() { // This class is not meant to be initialized

    }

    public static <K> Gatherer<BlogPost, Map<K, List<BlogPost>>, Map.Entry<K, List<BlogPost>>> groupByLimit(
            Function<? super BlogPost, ? extends K> keyExtractor,
            int limit,
            Comparator<? super BlogPost> comparator
    ) {
        return Gatherer.of(
                // Initialize with an empty map to store our grouped items
                HashMap<K, List<BlogPost>>::new,

                // Process each blog post
                (map, post, downstream) -> {
                    // Get the key for this blog post (e.g., the category)
                    K key = keyExtractor.apply(post);

                    // Add this post to its group (creating the group if needed)
                    map.computeIfAbsent(key, k -> new ArrayList<>()).add(post);

                    // Continue processing the stream
                    return true;
                },

                // Combiner for parallel streams - just use the first map in this simple case
                (map1, map2) -> map1,

                // When all posts have been processed, emit the results
                (map, downstream) -> {
                    map.forEach((key, posts) -> {
                        // Sort the posts and limit to the specified number
                        List<BlogPost> limitedPosts = posts.stream()
                                .sorted(comparator)
                                .limit(limit)
                                .toList();

                        // Emit a Map.Entry with the key and limited posts
                        downstream.push(Map.entry(key, limitedPosts));
                    });
                }
        );
    }
}
