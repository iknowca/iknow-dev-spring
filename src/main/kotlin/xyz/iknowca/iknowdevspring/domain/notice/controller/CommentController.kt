package xyz.iknowca.iknowdevspring.domain.notice.controller

import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import xyz.iknowca.iknowdevspring.domain.notice.entity.CommentDto
import xyz.iknowca.iknowdevspring.domain.notice.service.CommentService

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
class CommentController(
    val commentService: CommentService
) {
    @PostMapping("/{noticeId}/comment")
    fun postComment(
        @PathVariable("noticeId") noticeId: Long,
        @RequestHeader("authorization") authorization: String,
        @RequestBody commentDto: CommentDto
    ): ResponseEntity<Map<String, String>> {
        return commentService.postComment(noticeId, authorization, commentDto)
    }
}