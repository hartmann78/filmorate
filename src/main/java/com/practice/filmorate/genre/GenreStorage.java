package com.practice.filmorate.genre;

import java.util.Collection;

public interface GenreStorage {
    Collection<Genre> findAll();

    Genre findGenreById(Long genreId);

    Genre createGenre(Genre genre);

    Genre updateGenre(Genre genre);

    void deleteGenre(Genre genre);
}
