package com.customer.captemplateproject.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.customer.captemplateproject.exception.BusinessException;
import com.sap.cds.CdsData;
import com.sap.cds.Result;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.StructuredType;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.draft.DraftService;

@Service
public class EntityService {

    // Select operations
    public <T extends CdsData> T selectSingle(CqnService service, CqnSelect select, Class<T> type,
            String errorMessage) {
        Result result = service.run(select);
        if (result.rowCount() == 0) {
            throw new BusinessException(errorMessage); // Changed exception type
        }
        // 修正某些Entity草稿模式下返回多条的情况
        if (result.rowCount() > 1) {
            // throw new BusinessException("Multiple_Records_Found", "Expected a single
            // record, but found multiple.");
            return result.stream().filter(r -> (Boolean) r.getOrDefault("IsActiveEntity", true)).findAny()
                    .map(r -> r.as(type))
                    .orElseThrow(() -> new BusinessException("Expected a single record, but found multiple."));

        }
        return result.single(type);
    }

    public <T extends CdsData> List<T> selectList(CqnService service, CqnSelect select, Class<T> type) {
        Result result = service.run(select);
        // 修正某些Entity草稿模式下返回多条的情况
        return result.streamOf(type).filter(r -> (Boolean) r.getOrDefault("IsActiveEntity", true)).toList();
        // return result.listOf(type);
    }

    public Result select(CqnService service, CqnSelect select) {
        return service.run(select);
    }

    // Insert operations
    public <T extends CdsData, E extends StructuredType<E>> Result insert(
            CqnService service,
            DraftService serviceDraft,
            Class<E> entityClass,
            T entity,
            Boolean isActiveEntity) {
        if (isActiveEntity) {
            return service.run(Insert.into(entityClass).entry(entity));
        } else {
            return serviceDraft.newDraft(Insert.into(entityClass).entry(entity));
        }
    }

    public <T extends CdsData, E extends StructuredType<E>> Result bulkInsert(
            CqnService service,
            DraftService serviceDraft,
            Class<E> entityClass,
            List<T> entities,
            Boolean isActiveEntity) {
        if (isActiveEntity) {
            return service.run(Insert.into(entityClass).entries(entities));
        } else {
            return serviceDraft.newDraft(Insert.into(entityClass).entries(entities));
        }
    }

    public <T extends CdsData, E extends StructuredType<E>> Result update(
            CqnService service,
            DraftService serviceDraft,
            Class<E> entityClass,
            T entity,
            Boolean isActiveEntity) {
        if (isActiveEntity) {
            return service.run(Update.entity(entityClass).entry(entity));
        } else {
            return serviceDraft.patchDraft(Update.entity(entityClass).entry(entity));
        }
    }

    public <T extends CdsData, E extends StructuredType<E>> Result delete(
            CqnService service,
            DraftService serviceDraft,
            Class<E> entityClass,
            T entity,
            Boolean isActiveEntity) {
        if (isActiveEntity) {
            return service.run(Delete.from(entityClass).matching(entity));
        } else {
            return serviceDraft.cancelDraft(Delete.from(entityClass).matching(entity));
        }
    }

    public <T extends CdsData, E extends StructuredType<E>> void batchInsert(
            CqnService service,
            DraftService serviceDraft,
            Class<E> entityClass,
            List<T> entities,
            String reportId,
            Boolean isActiveEntity) {

        bulkInsert(service, serviceDraft, entityClass, entities, isActiveEntity);

    }

}