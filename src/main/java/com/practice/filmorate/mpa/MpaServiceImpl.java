package com.practice.filmorate.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    @Override
    public Collection<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    @Override
    public Mpa findMpaById(Long mpaId) {
        return mpaStorage.findMpaById(mpaId);
    }

    @Override
    public Mpa createMpa(Mpa mpa) {
        return mpaStorage.createMpa(mpa);
    }

    @Override
    public Mpa updateMpa(Mpa mpa) {
        return mpaStorage.updateMpa(mpa);
    }

    @Override
    public void deleteMpa(Mpa mpa) {
        mpaStorage.deleteMpa(mpa);
    }
}
