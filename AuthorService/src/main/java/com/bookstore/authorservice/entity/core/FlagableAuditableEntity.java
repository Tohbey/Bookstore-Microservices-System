package com.bookstore.authorservice.entity.core;

import com.bookstore.bookstorestarter.Util.JpaConverter;
import com.bookstore.bookstorestarter.enums.Flag;
import com.bookstore.bookstorestarter.model.Flagable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.io.Serializable;

@Data
@MappedSuperclass
public abstract class FlagableAuditableEntity  extends AuditableEntity implements Flagable, Serializable {

    @Convert(converter = JpaConverter.FlagConverter.class)
    @Basic(optional = true)
    @Column(name = "flag")
    @Enumerated(EnumType.ORDINAL)
    @Audited
    private Flag flag;
}
