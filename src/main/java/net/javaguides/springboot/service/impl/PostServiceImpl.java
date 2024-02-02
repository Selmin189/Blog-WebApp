package net.javaguides.springboot.service.impl;

import net.javaguides.springboot.dto.PostDto;
import net.javaguides.springboot.dto.PostResponse;
import net.javaguides.springboot.entity.Post;
import net.javaguides.springboot.entity.User;
import net.javaguides.springboot.mapper.CategoryMapper;
import net.javaguides.springboot.repository.PostRepository;
import net.javaguides.springboot.repository.UserRepository;
import net.javaguides.springboot.service.CategoryService;
import net.javaguides.springboot.dto.CategoryDto;
import net.javaguides.springboot.entity.Category;
import net.javaguides.springboot.mapper.PostMapper;
import net.javaguides.springboot.service.PostService;
import net.javaguides.springboot.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private CategoryService categoryService;

    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository, CategoryService categoryService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
    }

    public List<PostDto> findAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
            .sorted(Comparator.comparing(Post::getCreatedOn).reversed())
            .map(PostMapper::mapToPostDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> findPostsByUser() {
        String email = SecurityUtils.getCurrentUser().getUsername();
        User createdBy = userRepository.findByEmail(email);
        Long userId = createdBy.getId();
        List<Post> posts = postRepository.findPostsByUser(userId);
        return posts.stream()
            .sorted(Comparator.comparing(Post::getCreatedOn).reversed())
            .map(PostMapper::mapToPostDto)
            .collect(Collectors.toList());
    }

    @Override
    public void createPost(PostDto postDto) {
        String email = SecurityUtils.getCurrentUser().getUsername();
        User user = userRepository.findByEmail(email);
        Post post = PostMapper.mapToPost(postDto);
        Long categoryId = postDto.getCategoryId();
        if (categoryId != null) {
            CategoryDto categoryDto = categoryService.getCategoryById(categoryId);
            Category category = CategoryMapper.mapToCategory(categoryDto);
            post.setCategory(category);
        }
        post.setCreatedBy(user);
        postRepository.save(post);
    }

    @Override
    public PostDto findPostById(Long postId) {
        Post post = postRepository.findById(postId).get();
        return PostMapper.mapToPostDto(post);
    }

    @Override
    public void updatePost(PostDto postDto) {
        String email = SecurityUtils.getCurrentUser().getUsername();
        User createdBy = userRepository.findByEmail(email);
        Post post = PostMapper.mapToPost(postDto);

        Long categoryId = postDto.getCategoryId();
        if (categoryId != null) {
            postDto.setCategoryId(postDto.getCategoryId());
        }
        post.setCreatedBy(createdBy);
        postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public PostDto findPostByUrl(String postUrl) {
        Optional<Post> postOptional = postRepository.findByUrl(postUrl);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            return PostMapper.mapToPostDto(post);
        } else {
            return null;
        }
    }

    @Override
    public List<PostDto> searchPosts(String query) {
        List<Post> posts = postRepository.searchPosts(query);
        return posts.stream()
            .map(PostMapper::mapToPostDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> searchPostsByUser(String query, String username) {
        User createdBy = userRepository.findByEmail(username);
        boolean isAdmin = SecurityUtils.isAdmin(); // Metoda koja proverava da li je korisnik admin
        List<Post> posts = postRepository.searchPostsByUser(query, isAdmin, createdBy.getId());
        return posts.stream()
            .map(PostMapper::mapToPostDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> findByCategoryId(Long categoryId) {
        return postRepository.findByCategoryId(categoryId)
            .stream()
            .sorted(Comparator.comparing(Post::getCreatedOn).reversed())
            .map(PostMapper::mapToPostDto)
            .collect(Collectors.toList());
    }
    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize) {
        Sort sort = Sort.by("createdOn").descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize,sort);

        Page<Post> posts = postRepository.findAll(pageable);

        List<Post> listOfPosts = posts.getContent();

        List<PostDto> content = listOfPosts.stream()
            .map(PostMapper::mapToPostDto)
            .collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }
}
