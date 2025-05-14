package com.eden.eden_crm_sec_crm_back.base.service.impl;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import com.eden.eden_crm_sec_crm_back.base.util.Constants;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;




@MappedSuperclass
public abstract class BaseServiceImpl<T extends BaseEntity<ID>, ID extends Serializable> implements BaseService<T, ID> {
    protected abstract BaseRepository<T, ID> getRepository();



    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    /**
     * @return
     */
    @Override
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public T findById(ID id) {
        String[] params = {id.toString()};
        return getRepository().findById(id).orElseThrow(() ->
                new BusinessException(Constants.ErrorKeys.EXCEPTION_RECORD_NOT_FOUND, HttpStatus.NOT_FOUND, params));
    }

    @Override
    public T customFindById(ID id) {
        if(id == null)
            return null;
        else{
            String[] params = {id.toString()};
            return getRepository().findById(id).orElseThrow(() ->
                    new BusinessException(Constants.ErrorKeys.EXCEPTION_RECORD_NOT_FOUND, HttpStatus.NOT_FOUND, params));
        }
    }

    public T getReferenceById(ID id) {
        return getRepository().getReferenceById(id);
    }


    @Override
    public Optional<T> getById(ID id) {
        return getRepository().findById(id);
    }

    /**
     * @param entity
     * @return
     */
    @Override
    public T insert(T entity) {
        return getRepository().save(entity);

    }

    /**
     * @param entity
     * @return
     */
    @Override
    public T persist(T entity) {
        return getRepository().saveAndFlush(entity);

    }

    /**
     *
     */
    @Override
    public T update(T entity) {
        return getRepository().save(entity);
    }

    /**
     * @param entities
     * @return
     */
    @Override
    public List<T> saveAll(List<T> entities) {
        return getRepository().saveAll(entities);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public void deleteById(ID id) {
        getRepository().deleteById(id);
    }

    /**
     * @param ids
     */
    @Override
    public void deleteAll(List<ID> ids) {
        ids.forEach(id ->
                getRepository().deleteById(id)
        );
    }

    public int updateIsDeleted(ID id, boolean isDeleted) {
        return getRepository().updateIsDeleted(id, isDeleted);
    }


}
