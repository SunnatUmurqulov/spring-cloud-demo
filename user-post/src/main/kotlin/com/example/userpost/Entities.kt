package com.example.userpost

import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.repository.Temporal
import java.util.*
import javax.persistence.*

@MappedSuperclass
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: Date? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false,
)

@Entity
class Post(
    var text: String,
    @Column(nullable = false) val userId: Long,
) : BaseEntity()


@Entity
class Comments(
    @ManyToOne val post: Post,
    @Column(nullable = false) val userId: Long,
    @Column(nullable = false) var text: String,
) : BaseEntity()

@Entity
class Likes(
    @ManyToOne val post: Post,
    val userId: Long,
) : BaseEntity()

@Entity
class ViewPost(
    @ManyToOne val post: Post,
    val userId: Long,
) : BaseEntity()

