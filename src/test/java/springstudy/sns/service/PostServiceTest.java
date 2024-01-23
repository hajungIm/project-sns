package springstudy.sns.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springstudy.sns.exception.ErrorCode;
import springstudy.sns.exception.SnsApplicationException;
import springstudy.sns.fixture.PostEntityFixture;
import springstudy.sns.fixture.UserEntityFixture;
import springstudy.sns.model.entity.PostEntity;
import springstudy.sns.model.entity.UserEntity;
import springstudy.sns.repository.PostEntityRepository;
import springstudy.sns.repository.UserEntityRepository;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;
    @MockBean
    private PostEntityRepository postEntityRepository;
    @MockBean
    private UserEntityRepository userEntityRepository;

    @Test
    void 포스트작성이_성공한경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        assertThatCode(() -> postService.create(title, body, userName)).doesNotThrowAnyException();
    }

    @Test
    void 포스트작성시_요청한유저가_존재하지않는경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SnsApplicationException e = assertThrows(SnsApplicationException.class,
                () -> postService.create(title, body, userName));
        Assertions.assertThat(e.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void 포스트수정이_성공한경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        assertThatCode(() -> postService.modify(title, body, userName, postId)).doesNotThrowAnyException();
    }

    @Test
    void 포스트수정시_포스트가_존재하지않는_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.modify(title, body, userName, postId))
                .isInstanceOf(SnsApplicationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
    }

    @Test
    void 포스트수정시_권한이_없는_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("writer", "password", 2);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        assertThatThrownBy(() -> postService.modify(title, body, userName, postId))
                .isInstanceOf(SnsApplicationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PERMISSION);
    }

    @Test
    void 포스트삭제가_성공한경우() {
        String username = "username";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        assertThatCode(() -> postService.delete(username, postId)).doesNotThrowAnyException();
    }

    @Test
    void 포스트삭제시_포스트가_존재하지않는_경우() {
        String username = "username";
        Integer postId = 1;

        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.delete(username, postId))
                .isInstanceOf(SnsApplicationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
    }

    @Test
    void 포스트삭제시_권한이_없는_경우() {
        String username = "username";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1);
        UserEntity writer = UserEntityFixture.get("writer", "password", 2);

        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        assertThatThrownBy(() -> postService.delete(username, postId))
                .isInstanceOf(SnsApplicationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PERMISSION);
    }

    @Test
    void 피드목록요청이_성공한경우() {
        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());
        assertThatCode(() -> postService.list(pageable)).doesNotThrowAnyException();
    }

    @Test
    void 내피드목록요청이_성공한경우() {
        Pageable pageable = mock(Pageable.class);
        UserEntity user = mock(UserEntity.class);
        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findAllByUser(user, pageable)).thenReturn(Page.empty());
        assertThatCode(() -> postService.my("", pageable)).doesNotThrowAnyException();
    }
}
