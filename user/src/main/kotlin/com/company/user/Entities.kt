package com.company.user

import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.Temporal
import java.util.*
import javax.persistence.*


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: Date? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false,
)

@Entity
class User(
    @Column(length = 128, nullable = false) var firstName: String,
    @Column(length = 128, nullable = false) var lastName: String,
    @Column(length = 60, nullable = false, unique = true) var username: String,
    @Column(length = 60, nullable = false, unique = true)  var email: String,
    @Column(nullable = false) var password: String,
    var active: Boolean = false,
    var emailCode:String
) : BaseEntity()