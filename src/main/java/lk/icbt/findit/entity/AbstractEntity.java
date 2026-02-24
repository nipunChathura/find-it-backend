package lk.icbt.findit.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class AbstractEntity {
    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_datetime")
    private Date createdDatetime;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name = "modified_datetime")
    private Date modifiedDatetime;

    @Version
    @Column(name = "version")
    private Integer version;
}
