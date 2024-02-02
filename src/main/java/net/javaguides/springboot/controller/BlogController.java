package net.javaguides.springboot.controller;

import net.javaguides.springboot.dto.CommentDto;
import net.javaguides.springboot.dto.PostDto;
import net.javaguides.springboot.dto.PostResponse;
import net.javaguides.springboot.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class BlogController {

    private PostService postService;

    public BlogController(PostService postService) {
        this.postService = postService;
    }
    @GetMapping("/posts")
    public ModelAndView getAllPosts(
        @RequestParam(value = "pageNo", defaultValue = "0" , required = false) int pageNo,
        @RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize,
        Model model) {
        PostResponse postsResponse = postService.getAllPosts(pageNo, pageSize);
        model.addAttribute("postsResponse", postsResponse);
        ModelAndView modelAndView = new ModelAndView("blog/view_posts");
        modelAndView.addObject("postsResponse", postsResponse);
        return modelAndView;
    }
    @GetMapping("/post/category/{categoryId}")
    public String getPostsByCategory(@PathVariable Long categoryId, Model model) {
        List<PostDto> postsResponse = postService.findByCategoryId(categoryId);
        model.addAttribute("postsResponse", postsResponse);
        return "blog/category_posts";
    }
    // handler method to handle view post request
    @GetMapping("/post/{postUrl}")
    private String showPost(@PathVariable("postUrl") String postUrl,
                            Model model){
        PostDto post = postService.findPostByUrl(postUrl);

        CommentDto commentDto = new CommentDto();
        model.addAttribute("post", post);
        model.addAttribute("comment", commentDto);
        return "blog/blog_post";
    }
    // handler method to handle blog post search request
    // http://localhost:8080/page/search?query=java
    @GetMapping("/page/search")
    public String searchPosts(@RequestParam(value = "query") String query,
                              Model model){
        List<PostDto> postsResponse = postService.searchPosts(query);
        model.addAttribute("postsResponse", postsResponse);
        return "blog/category_posts";
    }
    @GetMapping("/contact")
    public String contactPage(){
        return "blog/contact";
    }
    @GetMapping("/about")
    public String aboutPage(){
        return "blog/about";
    }
    @GetMapping("/")
    public String redirectToPosts() {
        return "redirect:/posts";
    }
}
