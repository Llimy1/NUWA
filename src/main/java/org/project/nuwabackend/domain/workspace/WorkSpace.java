package org.project.nuwabackend.domain.workspace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.domain.base.BaseTimeJpa;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WorkSpace extends BaseTimeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Long id;

    @Column(name = "workspace_name")
    private String name;

    @Column(name = "workspace_image")
    private String image;

    // 이름이 애매합니다. 좋은 단어 있으면 변경 해주세요.
    // Lob으로 할지도 애매합니다.
    @Column(name = "workspace_introduce")
    private String introduce;

    @Builder
    private WorkSpace(String name, String image, String introduce) {
        this.name = name;
        this.image = image;
        this.introduce = introduce;
    }

    // 워크스페이스 생성
    public static WorkSpace createWorkSpace(String name, String image, String introduce) {
        return WorkSpace.builder()
                .name(name)
                .image(image)
                .introduce(introduce)
                .build();
    }

    // 워크스페이스 수정
    public void updateWorkSpace(String name, String image) {
        this.name = name;
        this.image = image;
    }
}
