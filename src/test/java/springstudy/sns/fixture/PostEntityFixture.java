package springstudy.sns.fixture;

import springstudy.sns.model.entity.PostEntity;
import springstudy.sns.model.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(String userName, Integer postId, Integer userId) {
        UserEntity user = UserEntity.builder()
                .id(userId)
                .userName(userName)
                .build();

        return PostEntity.builder()
                .user(user)
                .id(postId)
                .build();
    }
}
