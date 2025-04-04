package ru.bookslibrary.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.bookslibrary.repository.LibraryRepository;
import ru.bookslibrary.service.LibraryQueryService;
import ru.bookslibrary.service.LibraryService;
import ru.bookslibrary.service.criteria.LibraryCriteria;
import ru.bookslibrary.service.dto.LibraryDTO;
import ru.bookslibrary.web.rest.dto.ResultDto;
import ru.bookslibrary.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.bookslibrary.domain.Library}.
 */
@RestController
@RequestMapping("/api")
public class LibraryController {

    private final Logger log = LoggerFactory.getLogger(LibraryController.class);

    private static final String ENTITY_NAME = "library";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LibraryService libraryService;

    private final LibraryRepository libraryRepository;

    private final LibraryQueryService libraryQueryService;

    public LibraryController(LibraryService libraryService, LibraryRepository libraryRepository, LibraryQueryService libraryQueryService) {
        this.libraryService = libraryService;
        this.libraryRepository = libraryRepository;
        this.libraryQueryService = libraryQueryService;
    }

    /**
     * {@code POST  /libraries} : Create a new library.
     *
     * @param libraryDTO the libraryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new libraryDTO, or with status {@code 400 (Bad Request)} if the library has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/libraries")
    public ResponseEntity<LibraryDTO> createLibrary(@Valid @RequestBody LibraryDTO libraryDTO) throws URISyntaxException {
        log.debug("REST request to save Library : {}", libraryDTO);
        if (libraryDTO.getId() != null) {
            throw new BadRequestAlertException("A new library cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LibraryDTO result = libraryService.save(libraryDTO);
        return ResponseEntity
            .created(new URI("/api/libraries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /libraries/:id} : Updates an existing library.
     *
     * @param id the id of the libraryDTO to save.
     * @param libraryDTO the libraryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated libraryDTO,
     * or with status {@code 400 (Bad Request)} if the libraryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the libraryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/libraries/{id}")
    public ResponseEntity<LibraryDTO> updateLibrary(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LibraryDTO libraryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Library : {}, {}", id, libraryDTO);
        if (libraryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, libraryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!libraryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LibraryDTO result = libraryService.update(libraryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, libraryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /libraries/:id} : Partial updates given fields of an existing library, field will ignore if it is null
     *
     * @param id the id of the libraryDTO to save.
     * @param libraryDTO the libraryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated libraryDTO,
     * or with status {@code 400 (Bad Request)} if the libraryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the libraryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the libraryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/libraries/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LibraryDTO> partialUpdateLibrary(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LibraryDTO libraryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Library partially : {}, {}", id, libraryDTO);
        if (libraryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, libraryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!libraryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LibraryDTO> result = libraryService.partialUpdate(libraryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, libraryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /libraries} : get all the libraries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of libraries in body.
     */
    @GetMapping("/libraries")
    public ResponseEntity<List<LibraryDTO>> getAllLibraries(
        LibraryCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Libraries by criteria: {}", criteria);
        Page<LibraryDTO> page = libraryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /libraries/count} : count all the libraries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/libraries/count")
    public ResponseEntity<Long> countLibraries(LibraryCriteria criteria) {
        log.debug("REST request to count Libraries by criteria: {}", criteria);
        return ResponseEntity.ok().body(libraryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /libraries/:id} : get the "id" library.
     *
     * @param id the id of the libraryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the libraryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/libraries/{id}")
    public ResponseEntity<LibraryDTO> getLibrary(@PathVariable Long id) {
        log.debug("REST request to get Library : {}", id);
        Optional<LibraryDTO> libraryDTO = libraryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(libraryDTO);
    }

    /**
     * {@code DELETE  /libraries/:id} : delete the "id" library.
     *
     * @param id the id of the libraryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/libraries/{id}")
    public ResponseEntity<Void> deleteLibrary(@PathVariable Long id) {
        log.debug("REST request to delete Library : {}", id);
        libraryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/libraries?query=:query} : search for the library corresponding
     * to the query.
     *
     * @param query the query of the library search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/libraries")
    public ResponseEntity<List<LibraryDTO>> searchLibraries(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Libraries for query {}", query);
        Page<LibraryDTO> page = libraryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/libraries/reindex-all")
    public ResponseEntity<ResultDto> rendexAllLibraries() {
        log.debug("REST request to reindex all libraries");
        libraryService.reindexAll();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResultDto.builder().message("Все библиотеки были переиндексированны в ElasticSearch").build());
    }
}
