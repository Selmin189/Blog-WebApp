package net.javaguides.springboot.repository;

import net.javaguides.springboot.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByUrl(String url);

    @Query("SELECT p from Post p WHERE " +
            " p.title LIKE CONCAT('%', :query, '%') OR " +
            " p.shortDescription LIKE CONCAT('%', :query, '%')")
    List<Post> searchPosts(String query);

    @Query("SELECT p FROM Post p WHERE " +
        "(p.title LIKE CONCAT('%', :query, '%') OR " +
        "p.shortDescription LIKE CONCAT('%', :query, '%')) " +
        "AND (:isAdmin = true OR p.createdBy.id = :userId)")
    List<Post> searchPostsByUser(String query, boolean isAdmin, Long userId);

    @Query(value = "select * from posts p where p.created_by =:userId", nativeQuery = true)
    List<Post> findPostsByUser(Long userId);
    List<Post> findByCategoryId(Long categoryId);
}
