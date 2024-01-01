package springstudy.sns.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springstudy.sns.exception.ErrorCode;
import springstudy.sns.exception.SnsApplicationException;
import springstudy.sns.model.User;
import springstudy.sns.model.entity.UserEntity;
import springstudy.sns.repository.UserEntityRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public User join(String userName, String password) {
        // 회원가입하려는 userName으로 회원가입된 user가 있는지
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
        });

        // 회원가입 진행 = user를 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
        return User.fromEntity(userEntity);
    }

}
