package springstudy.sns.model.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import springstudy.sns.model.Post;
import springstudy.sns.model.entity.base.BaseTimeEntity;

@Entity
@Table(name = "\"post\"")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE \"post\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class PostEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static PostEntity of(String title, String body, UserEntity userEntity) {
        return PostEntity.builder()
                .title(title)
                .body(body)
                .user(userEntity)
                .build();
    }

    public Post updatePost(String title, String body) {
        this.title = title;
        this.body = body;
        return Post.fromEntity(this);
    }
}
