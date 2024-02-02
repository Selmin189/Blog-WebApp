package net.javaguides.springboot.service;

import net.javaguides.springboot.dto.PostDto;
import net.javaguides.springboot.dto.PostResponse;

import java.util.List;

public interface PostService {
    List<PostDto> findAllPosts();

    List<PostDto> findPostsByUser();

    void createPost(PostDto postDto);

    PostDto findPostById(Long postId);

    void updatePost(PostDto postDto);

    void deletePost(Long postId);

    PostDto findPostByUrl(String postUrl);

    List<PostDto> searchPosts(String query);

    List<PostDto> searchPostsByUser(String query, String username);

    List<PostDto> findByCategoryId(Long categoryId);
    PostResponse getAllPosts(int pageNo, int pageSize);
}
