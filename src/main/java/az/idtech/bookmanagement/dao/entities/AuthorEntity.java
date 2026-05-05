package az.idtech.bookmanagement.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "authors")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        private String bio;

        @Column(name = "created_at")
        private LocalDateTime createdAt;

        @ManyToMany(fetch = FetchType.LAZY, mappedBy = "authors")
        private List<BookEntity> books;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuthorEntity that = (AuthorEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
