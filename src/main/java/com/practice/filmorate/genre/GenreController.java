package com.practice.filmorate.genre;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/genres")
    public Collection<Genre> findAll(HttpServletRequest request) {
        setLog(request);
        return genreService.findAll();
    }

    @GetMapping("/genres/{genreId}")
    public Genre findFilmById(@PathVariable Long genreId, HttpServletRequest request) {
        setLog(request);
        return genreService.findGenreById(genreId);
    }

    @PostMapping("/genres")
    public Genre createGenre(@RequestBody Genre genre, HttpServletRequest request) {
        setLog(request);
        return genreService.createGenre(genre);
    }

    @PutMapping("/genres")
    public Genre updateGenre(@RequestBody Genre genre, HttpServletRequest request) {
        setLog(request);
        return genreService.updateGenre(genre);
    }

    @DeleteMapping("/genres")
    public void deleteGenre(@RequestBody Genre genre, HttpServletRequest request) {
        setLog(request);
        genreService.deleteGenre(genre);
    }

    public void setLog(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }
}
