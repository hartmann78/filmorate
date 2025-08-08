package com.practice.filmorate.mpa;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("/mpa")
    public Collection<Mpa> findAll(HttpServletRequest request) {
        setLog(request);
        return mpaService.findAll();
    }

    @GetMapping("/mpa/{mpaId}")
    public Mpa findFilmById(@PathVariable Long mpaId, HttpServletRequest request) {
        setLog(request);
        return mpaService.findMpaById(mpaId);
    }

    @PostMapping("/mpa")
    public Mpa createMpa(@RequestBody Mpa mpa, HttpServletRequest request) {
        setLog(request);
        return mpaService.createMpa(mpa);
    }

    @PutMapping("/mpa")
    public Mpa updateMpa(@RequestBody Mpa mpa, HttpServletRequest request) {
        setLog(request);
        return mpaService.updateMpa(mpa);
    }

    @DeleteMapping("/mpa")
    public void deleteMpa(@RequestBody Mpa mpa, HttpServletRequest request) {
        setLog(request);
        mpaService.deleteMpa(mpa);
    }

    public void setLog(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }
}
