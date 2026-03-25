package ro.festival.dao;

import java.util.Optional;

public abstract class BaseDAO<T,K> {
    public abstract void create(T obj);
    public abstract Optional<T> read(K id); // K = tipul cheii
    public abstract void update(T obj);
    public abstract void delete(K id);
}

