package net.javaguides.springboot.controller;

import jakarta.validation.Valid;
import net.javaguides.springboot.dto.CommentDto;
import net.javaguides.springboot.dto.PostDto;
import net.javaguides.springboot.service.CategoryService;
import net.javaguides.springboot.service.PostService;
import net.javaguides.springboot.util.ROLE;
import net.javaguides.springboot.dto.CategoryDto;
import net.javaguides.springboot.service.CommentService;
import net.javaguides.springboot.util.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    private PostService postService;
    private CommentService commentService;

    private CategoryService categoryService;

    public PostController(PostService postService, CommentService commentService,CategoryService categoryService) {
        this.postService = postService;
        this.commentService = commentService;
        this.categoryService = categoryService;
    }

    // create handler method, GET request and return model and view
    @GetMapping("/admin/posts")
    public String posts(Model model){
        String role = SecurityUtils.getRole();
        List<PostDto> posts = null;
        if(ROLE.ROLE_ADMIN.name().equals(role)){
            posts = postService.findAllPosts();
        }else{
            posts = postService.findPostsByUser();
        }
        model.addAttribute("posts", posts);
        return "/admin/posts";
    }

    // handler method to handle list comments request
    @GetMapping("/admin/posts/comments")
    public String postComments(Model model){
        String role = SecurityUtils.getRole();
        List<CommentDto> comments = null;
        if(ROLE.ROLE_ADMIN.name().equals(role)){
            comments = commentService.findAllComments();
        }else{
            comments = commentService.findCommentsByPost();
        }
        model.addAttribute("comments", comments);
        return "admin/comments";
    }

    // handler method to handle delete comment request
    @GetMapping("/admin/posts/comments/{commentId}")
    public String deleteComment(@PathVariable("commentId") Long commentId){
        commentService.deleteComment(commentId);
        return "redirect:/admin/posts/comments";
    }

    // handler method to handle new post request
    @GetMapping("admin/posts/newpost")
    public String newPostForm(Model model){
        PostDto postDto = new PostDto();
        List<CategoryDto> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("post", postDto);
        return "admin/create_post";
    }

    // handler method to handle form submit request
    @PostMapping("/admin/posts")
    public String createPost(@Valid @ModelAttribute("post") PostDto postDto,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("post", postDto);
            return "admin/create_post";
        }
        Long categoryId = postDto.getCategoryId();
        if (categoryId != null) {
            CategoryDto selectedCategory = categoryService.getCategoryById(categoryId);
            postDto.setCategoryId(selectedCategory.getId());
        }
        postDto.setUrl(getUrl(postDto.getTitle()));
        postService.createPost(postDto);
        return "redirect:/admin/posts";
    }
    // handler method to handle edit post request
    @GetMapping("/admin/posts/{postId}/edit")
    public String editPostForm(@PathVariable("postId") Long postId,
                               Model model){
        List<CategoryDto> categories = categoryService.getAllCategories();
        PostDto postDto = postService.findPostById(postId);
        model.addAttribute("post", postDto);
        model.addAttribute("categories", categories);
        return "admin/edit_post";
    }

    @PostMapping("/admin/posts/{postId}")
    public String updatePost(@PathVariable("postId") Long postId,
                             @Valid @ModelAttribute("post") PostDto post,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            List<CategoryDto> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("post", post);
            return "admin/edit_post";
        }
        Long categoryId = post.getCategoryId();
        if (categoryId != null) {
            CategoryDto selectedCategory = categoryService.getCategoryById(categoryId);
            post.setCategoryId(selectedCategory.getId());
        }
        post.setId(postId);
        postService.updatePost(post);
        return "redirect:/admin/posts";
    }
    // handler method to handle delete post request
    @GetMapping("/admin/posts/{postId}/delete")
    public String deletePost(@PathVariable("postId") Long postId){
        postService.deletePost(postId);
        return "redirect:/admin/posts";
    }
    // handler method to handle view post request
    @GetMapping("/admin/posts/{postUrl}/view")
    public String viewPost(@PathVariable("postUrl") String postUrl,
                           Model model){
        PostDto postDto = postService.findPostByUrl(postUrl);
        model.addAttribute("post", postDto);
        return "admin/view_post";
    }
    // handler method to handle search blog posts request
    // localhost:8080/admin/posts/search?query=java
    @GetMapping("/admin/posts/search")
    public String searchPosts(@RequestParam(value = "query") String query,
                              Model model) {
        String role = SecurityUtils.getRole();
        String username = SecurityUtils.getCurrentUser().getUsername();
        List<PostDto> posts;
        if (ROLE.ROLE_ADMIN.name().equals(role)) {
            posts = postService.searchPosts(query);
        } else {
            posts = postService.searchPostsByUser(query, username);
        }
        model.addAttribute("posts", posts);
        return "admin/posts";
    }
    private static String getUrl(String postTitle){
        String title = postTitle.trim().toLowerCase();
        String url = title.replaceAll("\\s+", "-");
        url = url.replaceAll("[^A-Za-z0-9]", "-");
        return url;
    }
}
