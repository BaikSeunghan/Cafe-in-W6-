package com.team2.cafein.service;

import com.team2.cafein.dto.PostRequestDto;
import com.team2.cafein.dto.PostResponseDto;
import com.team2.cafein.dto.ResponseMessageDto;
import com.team2.cafein.dto.UpdatePostDto;
import com.team2.cafein.model.Bookmark;
import com.team2.cafein.model.CoffeeImg;
import com.team2.cafein.model.Post;
import com.team2.cafein.model.User;
import com.team2.cafein.repository.BookmarkRepository;
import com.team2.cafein.repository.CoffeeImgRepository;
import com.team2.cafein.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    //    private final CoffeeImgRepository coffeeImgRepository;
//    private final CoffeeImgService coffeeImgService;
    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;

    /**
     * 게시글 등록 - 해결
     */
    @Transactional
//    public ResponseMessageDto createPost(PostRequestDto requestDto, MultipartFile file) throws IOException {
    public ResponseMessageDto createPost(PostRequestDto postRequestDto, Long userId) throws IOException {

        /* 파일을 업로드 하지 않았을 경우 처리 */
        if (postRequestDto.getImageUrl() == "") {
            return new ResponseMessageDto(false, "파일 첨부는 필수입니다");
        }
        // 회원 조회
        User user = userService.findOne(userId);

        // 게시글 등록
//        Post Post = requestDto.toEntity();
//        Post savePost = Post.createPost(Post, user);
        Post savePost = Post.createPost(postRequestDto, user);
        postRepository.save(savePost);

        // 게시글 이미지 등록
//        CoffeeImg coffeeImg = coffeeImgService.savePostImage(savePost);
//        savePost.setCoffeeImg(coffeeImg);

        return new ResponseMessageDto(true, "게시글 등록 성공");
    }

//    /**
//     * 게시글 수정
//     */
//    public UpdatePostDto getUpdatePostDto(Long postId) {
//        Post post = postRepository.findById(postId).orElseThrow(
//                () -> new NullPointerException("아이디가 존재하지 않습니다."));
//
//        CoffeeImg coffeeImg = coffeeImgService.findByPost(post);
//        return UpdatePostDto.of(post, coffeeImg);
//    }

    /**
     * 하나의 게시글 정보 가져가기
     */
//    public UpdatePostDto.PostImageDto getPostImageDto(Long postId) {
//        Post post = postRepository.findById(postId).orElseThrow(
//                () -> new NullPointerException("존재하지 않는 게시글입니다."));
//
////        CoffeeImg coffeeImg = coffeeImgService.findByItemOrderByItemImageIdAsc(item);
//        CoffeeImg coffeeImg = coffeeImgService.findByPost(post);
//        return UpdatePostDto.PostImageDto.of(coffeeImg);
//    }

    /**
     * 게시글 수정 - 해결
     */
    @Transactional
    public ResponseMessageDto updatePost(PostRequestDto postRequestDto, Long postId, Long userId) throws IOException {
        // 게시글 업데이트
//        Post post = updatePostInfo(updatePostDto);
//        // 커피 이미지 업데이트
//        updatePostImage(updatePostDto, post, file);
//        PostResponseDto.of(post, postRequestDto.getImageUrl());

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시판압니다"));
        if (Objects.equals(post.getId(), userId)) {
            // 수정 로직
            post.updatePost(postRequestDto);
            return new ResponseMessageDto(true, "게시글 수정 성공");
        }
        return new ResponseMessageDto(false, "게시글을 작성한 사용자가 아닙니다");
    }

//    private Post updatePostInfo(UpdatePostDto updatePostDto) {
//        Post updatePost = updatePostDto.toEntity();
//        Post updatedPost = updatePostInformation(updatePostDto.getPostId(), updatePost);
//        return updatedPost;
//    }

//    @Transactional
//    public Post updatePostInformation(Long postId, Post updatePost) {
//        Post savedPost = postRepository.findById(postId)
//                .orElseThrow(() -> new NullPointerException("없는 게시판"));
//        savedPost.updatePost(updatePost);
//        return savedPost;
//    }

//    private void updatePostImage(UpdatePostDto updatePostDto, Post post, MultipartFile file) throws IOException {
//
//        // 데이터베이스에 저장된 상품 이미지 정보
//        CoffeeImg coffeeImg = coffeeImgService.findByPost(post);
//        String originalImageName = updatePostDto.getOriginalImageName(); // 상품 수정 화면 조회 시에 있던 상품 이미지명 정보
//        MultipartFile postImageFile = file; // 상품 파일 이미지 정보
//
//        if (!postImageFile.isEmpty()) {  // 기존 파일 수정 or 신규 파일 등록 처리
//            coffeeImgService.updatePostImage(coffeeImg, postImageFile);
//        } else if (!StringUtils.hasText(originalImageName) &&
//                StringUtils.hasText(coffeeImg.getOriginalImageName())) { // 기존 파일 삭제
//            coffeeImgService.deletePostImage(coffeeImg);
//        }
//    }

    /**
     * 게시글 삭제 - 해결
     */
    @Transactional
    public ResponseMessageDto deletePost(Long postId, String userName) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

//        CoffeeImg coffeeImg = coffeeImgService.findByPost(post);
//        System.out.println("coffeeImg : " + coffeeImg); // ok

        String nickName = post.getUser().getNickname();
//        System.out.println("nickName : " + nickName); // ok
//        System.out.println("userName : " + userName); // ok

        if (Objects.equals(nickName, userName)) {
            // 삭제
//            coffeeImgService.deletePostImage(coffeeImg);
//            coffeeImgRepository.deleteById(coffeeImg.getId());
            bookmarkRepository.deleteAllByPostId(postId);
            postRepository.deleteById(postId);

        } else new IllegalArgumentException("작성한 유저가 아닙니다.");

        ResponseMessageDto responseMessageDto = new ResponseMessageDto();
        responseMessageDto.setStatus(true);
        responseMessageDto.setMessage("게시글 삭제 성공");
        return responseMessageDto;
    }

    /**
     * 전체 게시글 조회 - ResponseDto 를 따로 만들어서 담고 보내준다.
     */
    public List<PostResponseDto> getPosts(Long userId) {

        // 로그인 되어있는 userId로 Bookmark 테이블에서 select로 리스트 배열 받아오기
//        List<Bookmark> bookmarks = bookmarkRepository.findAllByUserId(userId);
        List<Long> bookmarkedPostIds = bookmarkRepository.findAllPostIdsByUserId(userId);

        // 접속한 유저가 북마크한 Post의 리스트
//        List<Long> responsePosts = new ArrayList<>();   // 여기는 응답할 게시글 목록을 위한 리스트 선언
//
//        for (Bookmark bookmark : bookmarks) { //for 문을 돌리면서 POST ID 에 대응되는 post의 내용을 List<post> 에 담아서 리턴
//            Long postId = bookmark.getPostId();
//            Post post = postRepository.findById(postId)
//                    .orElseThrow(() -> new NullPointerException("ID값 확인해주세여"));
//            responsePosts.add(post.getId());
//        }

        // 전체 게시글을 작성시간 순서로 추출
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        // 프론트에 보내줄 Dto 리스트
        List<PostResponseDto> listPost = new ArrayList<>();
        Boolean bookMark = true;
        for (Post post : posts) {

//            for (Long bookmarkedPostId : bookmarkedPostIds) {
//                if (bookmarkedPostId == post.getId()) {
//                    String imageUrl = post.getImageUrl();
//                    PostResponseDto postResponseDto = PostResponseDto.builder()
//                            .post(post)
//                            .imageUrl(imageUrl)
//                            .bookMark(bookMark)
//                            .build();
//                    listPost.add(postResponseDto);
//                    break;
//                } else {
//                    bookMark = false;
//                    String imageUrl = post.getImageUrl();
//                    PostResponseDto postResponseDto = PostResponseDto.builder()
//                            .post(post)
//                            .imageUrl(imageUrl)
//                            .bookMark(bookMark)
//                            .build();
//                    listPost.add(postResponseDto);
//                }
//            }
            for (Long bookmarkedPostId : bookmarkedPostIds) {
                if (bookmarkedPostId != post.getId()) {
                    bookMark = false;
                    break;
                }
            }
            String imageUrl = post.getImageUrl();
            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .post(post)
                    .imageUrl(imageUrl)
                    .bookMark(bookMark)
                    .build();
            listPost.add(postResponseDto);
        }
        return listPost;
    }

    /**
     * 내가 올린 게시글 조회 - 만져야함
     */
    public List<Post> getMyPosts(User user) {
        return postRepository.findAllByUser(user);
    }

    /**
     * 게시글 디테일 조회 - 해결
     */
    public PostResponseDto findOne(Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시글입니다."));
//        String imageUrl = post.getCoffeeImg().getImageUrl();

        PostResponseDto postResponseDto = PostResponseDto.builder()
                .post(post)
                .imageUrl(post.getImageUrl())
                .build();
        return postResponseDto;
    }
}

