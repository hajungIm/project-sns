package springstudy.sns.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springstudy.sns.model.User;
import springstudy.sns.model.entity.constant.UserRole;

@Getter
@AllArgsConstructor
public class UserJoinResponse {

    private Integer id;
    private String userName;
    private UserRole role;

    public static UserJoinResponse fromUser(User user) {
        return new UserJoinResponse(
                user.getId(),
                user.getUserName(),
                user.getUserRole()
        );
    }
}
