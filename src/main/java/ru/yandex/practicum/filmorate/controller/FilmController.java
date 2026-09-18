package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

/**
 * FilmController.
 */
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос GET /films");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Integer id) {
        log.info("Получен запрос GET /films/{}", id);
        return filmService.findById(id);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос POST /films: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Получен запрос PUT /films: {}", film);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Получен запрос PUT /films/{}/like/{}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Получен запрос DELETE /films/{}/like/{}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(required = false) Integer count) {
        log.info("Получен запрос GET /films/popular?count={}", count);
        return filmService.getPopular(count);
    }
}