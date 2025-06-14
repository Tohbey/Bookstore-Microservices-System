package com.bookstore.bookinventoryservice.entity.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class AuditableEntity extends BaseEntity {

    @Getter
    @Setter
    @CreatedBy
    @JsonIgnore
    @Column(name = "created_by", updatable = false)
    @Audited
    private String createdBy;

    @Getter
    @Setter
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @Audited
    private LocalDateTime createdDate;

    @Getter
    @Setter
    @LastModifiedBy
    @JsonIgnore
    @Column(name = "modified_by", insertable = false)
    @Audited
    private String lastModifiedBy;

    @Getter
    @Setter
    @LastModifiedDate
    @Basic(optional = true)
    @Column(name = "last_modified_date", insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @Audited
    private LocalDateTime lastModifiedDate;
}
