package com.practice.filmorate.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    @Override
    public Genre findGenreById(Long genreId) {
        return genreStorage.findGenreById(genreId);
    }

    @Override
    public Genre createGenre(Genre genre) {
        return genreStorage.createGenre(genre);
    }

    @Override
    public Genre updateGenre(Genre genre) {
        return genreStorage.updateGenre(genre);
    }

    @Override
    public void deleteGenre(Genre genre) {
        genreStorage.deleteGenre(genre);
    }
}
