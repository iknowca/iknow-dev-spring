package xyz.iknowca.iknowdevspring.domain.notice.service

import org.springframework.http.ResponseEntity
import xyz.iknowca.iknowdevspring.domain.notice.entity.CommentDto

interface CommentService {
    fun postComment(noticeId: Long, authorization: String, commentDto: CommentDto): ResponseEntity<Map<String, String>>
}