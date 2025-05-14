package com.eden.eden_crm_sec_crm_back.base.service;
import jakarta.persistence.MappedSuperclass;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;




@MappedSuperclass
public interface BaseService<T , ID > {
    /**
     *
     * @return
     */
    List<T> findAll();
    /**
     *
     */
    Page<T> findAll (Pageable pageable);

    /**
     *
     * @param id
     * @return
     */
    @Named("mappingEntities")
    T findById(ID id);

    @Named("customMappingEntities")
    T customFindById(ID id);

    T getReferenceById(ID id);

    /**
     *
     * @param id
     * @return
     */

    /**
     *
     * @param id
     * @return
     */
    Optional<T> getById(ID id);
    /**
     *
     * @param entity
     * @return
     */
    T insert (T entity);
    /**
     *
     * @param entity
     * @return
     */
    T persist (T entity);
    /**
     *
     * @param entity
     * @return
     */
    T update (T entity);
    /**
     *
     * @param entities
     * @return
     */
    List<T> saveAll(List<T> entities);
    /**
     *
     * @param id
     * @return
     */
    void deleteById (ID id);
    /**
     *
     * @param ids
     */
    void deleteAll (List<ID> ids);

    int updateIsDeleted(ID id, boolean isDeleted);
}
