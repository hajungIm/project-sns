package springstudy.sns.fixture;

import springstudy.sns.model.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity get(String userName, String password, Integer userId) {
        return UserEntity.builder()
                .id(userId)
                .userName(userName)
                .password(password)
                .build();
    }
}
