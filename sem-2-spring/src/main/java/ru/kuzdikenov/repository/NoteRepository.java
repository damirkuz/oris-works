package ru.kuzdikenov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kuzdikenov.model.Note;
import ru.kuzdikenov.model.User;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByAuthor(User user);

    List<Note> findByPublishedTrue();

    Optional<Note> findByIdAndAuthorUsername(Long id, String authorUsername);

    long deleteByIdAndAuthorUsername(Long id, String authorUsername);

    @Query("""
            select n
            from Note n
            where n.published = true
              and (
                    lower(n.title) like lower(concat('%', :query, '%'))
                 or lower(n.content) like lower(concat('%', :query, '%'))
              )
            order by n.createdAt desc
            """)
    List<Note> searchPublic(@Param("query") String query);
}
