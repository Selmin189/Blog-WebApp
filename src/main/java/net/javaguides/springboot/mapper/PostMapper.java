package net.javaguides.springboot.mapper;

import net.javaguides.springboot.dto.PostDto;
import net.javaguides.springboot.entity.Post;
import net.javaguides.springboot.entity.Category;

import java.util.stream.Collectors;

public class PostMapper {

    // map Post entity to PostDto
    public static PostDto mapToPostDto(Post post){
        return PostDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .url(post.getUrl())
            .content(post.getContent())
            .shortDescription(post.getShortDescription())
            .createdOn(post.getCreatedOn())
            .updatedOn(post.getUpdatedOn())
            .imageUrl(post.getImageUrl())
            .categoryId(post.getCategory() != null ? post.getCategory().getId() : null)
            .comments(post.getComments().stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toSet()))
            .build();
    }

    // map PostDto to Post entity
    public static Post mapToPost(PostDto postDto){
        Post post = Post.builder()
            .id(postDto.getId())
            .title(postDto.getTitle())
            .content(postDto.getContent())
            .url(postDto.getUrl())
            .shortDescription(postDto.getShortDescription())
            .createdOn(postDto.getCreatedOn())
            .updatedOn(postDto.getUpdatedOn())
            .imageUrl(postDto.getImageUrl())
            .build();

        // Set the category only if categoryId is not null
        if (postDto.getCategoryId() != null) {
            Category category = new Category();
            category.setId(postDto.getCategoryId());
            post.setCategory(category);
        }

        return post;
    }
}
