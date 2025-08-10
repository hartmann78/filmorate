package com.practice.filmorate.mpa;

import java.util.Collection;

public interface MpaStorage {
    Collection<Mpa> findAll();

    Mpa findMpaById(Long mpaId);

    Mpa createMpa(Mpa mpa);

    Mpa updateMpa(Mpa mpa);

    void deleteMpa(Mpa mpa);
}
