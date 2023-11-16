package xyz.iknowca.iknowdevspring.domain.notice.service

import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.entity.RoleType
import xyz.iknowca.iknowdevspring.domain.account.exception.AccountException
import xyz.iknowca.iknowdevspring.domain.account.service.AccountService
import xyz.iknowca.iknowdevspring.domain.notice.entity.Comment
import xyz.iknowca.iknowdevspring.domain.notice.entity.CommentDto
import xyz.iknowca.iknowdevspring.domain.notice.entity.Notice
import xyz.iknowca.iknowdevspring.domain.notice.exception.NoticeException

@Service
@RequiredArgsConstructor
class CommentServiceImpl(
    val noticeService: NoticeService,
    val accountService: AccountService
):CommentService {
    override fun postComment(
        noticeId: Long,
        authorization: String,
        commentDto: CommentDto
    ): ResponseEntity<Map<String, String>> {
        val writer:Account
        try {
            writer = accountService.findAccount(authorization)
        } catch (e: AccountException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val havePermission = accountService.checkRole(writer, RoleType.NORMAL)
        if(!havePermission) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        accountService.checkRole(writer, RoleType.NORMAL)

        val notice:Notice
        try {
            notice = noticeService.findById(noticeId)
        } catch (e: NoticeException) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
        val comment: Comment = Comment(commentDto, writer, notice)
        noticeService.saveComment(comment)
        return ResponseEntity.ok(mapOf("status" to "success"))
    }
}