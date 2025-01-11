package repository;

import domain.Entity;

import java.util.Optional;

public interface Repository<ID, E extends Entity<ID>> {
    String url = "jdbc:postgresql://127.0.0.1:5432/socialnetwork?ssl=false";
    String username = "postgres";
    String password = "lucacrisan";

    Optional<E> save(E entity);

    Optional<E> delete(ID id);

    Optional<E> find(ID id);

    Iterable<E> getAll();
}
