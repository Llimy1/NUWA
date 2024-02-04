package org.project.nuwabackend.domain.workspace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.domain.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WorkSpace extends BaseTimeEntity {

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

    public WorkSpace(String name, String image, String introduce) {
        this.name = name;
        this.image = image;
        this.introduce = introduce;
    }
}
