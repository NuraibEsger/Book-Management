package az.idtech.bookmanagement.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@RequiredArgsConstructor
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Column(name = "reviewer_name")
    private String reviewerName;

    @Column(nullable = false)
    private String content;

    private Integer rating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReviewEntity that = (ReviewEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
