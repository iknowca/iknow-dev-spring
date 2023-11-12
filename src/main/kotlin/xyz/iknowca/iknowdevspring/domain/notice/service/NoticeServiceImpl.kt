package xyz.iknowca.iknowdevspring.domain.notice.service

import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestHeader
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.entity.RoleType
import xyz.iknowca.iknowdevspring.domain.account.service.AccountService
import xyz.iknowca.iknowdevspring.domain.notice.entity.Notice
import xyz.iknowca.iknowdevspring.domain.notice.entity.NoticeDto
import xyz.iknowca.iknowdevspring.domain.notice.repository.NoticeRepository

@Service
@RequiredArgsConstructor
class NoticeServiceImpl(
    private val noticeRepository: NoticeRepository,
    private val accountService: AccountService
) :NoticeService {
    override fun postNotice(requestBody: NoticeDto, authorization: String?): ResponseEntity<Map<String, String>> {
        if (authorization==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val account:Account = accountService.findAccount(authorization)

        val havePermission:Boolean = accountService.checkRole(account, RoleType.ADMIN)
        if(!havePermission) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val notice = Notice(requestBody.title, requestBody.content)
        val savedNotice:Notice = noticeRepository.save(notice)
        return ResponseEntity.ok(mapOf("status" to "success", "noticeId" to savedNotice.id.toString()))
    }

    override fun getNotice(noticeId: Long): ResponseEntity<NoticeDto> {
        val maybeNotice = noticeRepository.findById(noticeId)
        if (maybeNotice.isEmpty) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
        val notice = maybeNotice.get()
        return ResponseEntity.ok(NoticeDto(notice.title, notice.content, noticeId))
    }
}