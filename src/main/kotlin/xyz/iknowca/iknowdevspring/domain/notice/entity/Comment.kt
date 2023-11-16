package xyz.iknowca.iknowdevspring.domain.notice.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.notice.entity.Notice
import java.time.LocalDateTime

@Entity
class Comment(
    @OneToOne
    val writer: Account,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    val notice: Notice,
    val content: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    constructor(commentDto: CommentDto, writer: Account, notice: Notice) : this(writer, notice, commentDto.content) {

    }
}