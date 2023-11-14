package xyz.iknowca.iknowdevspring.domain.notice.service

import lombok.RequiredArgsConstructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
) : NoticeService {
    override fun postNotice(requestBody: NoticeDto, authorization: String?): ResponseEntity<Map<String, String>> {
        if (authorization == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val account: Account = accountService.findAccount(authorization)

        val havePermission: Boolean = accountService.checkRole(account, RoleType.ADMIN)
        if (!havePermission) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val notice = Notice(requestBody.title, requestBody.content)
        val savedNotice: Notice = noticeRepository.save(notice)
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

    override fun getNoticeList(page: Int, size: Int): ResponseEntity<Page<NoticeDto>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val pageNotice = noticeRepository.findAll(pageable)
        if (pageNotice.size == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
        val pageNoticeDto: Page<NoticeDto> =
            pageNotice.map { notice -> NoticeDto(notice.title, notice.content, notice.id) }
        return ResponseEntity.ok(pageNoticeDto)
    }

    override fun deleteNotice(noticeId: Long, authorization: String?): ResponseEntity<Map<String, String>> {
        if (authorization==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val account:Account = accountService.findAccount(authorization)

        val havePermission: Boolean = accountService.checkRole(account, RoleType.ADMIN)
        if (!havePermission) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val notice:Notice
        try {
            notice = findNoticeById(noticeId)
        } catch (e:Exception) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }

        noticeRepository.delete(notice)

        return ResponseEntity.ok(mapOf("status" to "success"))
    }

    override fun modifyNotice(modifiedNotice:NoticeDto, noticeId: Long, authorization: String?): ResponseEntity<Map<String, String>> {
        if(authorization==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val account:Account = accountService.findAccount(authorization)
        val havePermission: Boolean = accountService.checkRole(account, RoleType.ADMIN)
        if(!havePermission) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val notice:Notice
        try  {
            notice = findNoticeById(noticeId)
        } catch (e:Exception) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }

        val savedNotice = Notice(modifiedNotice)
        noticeRepository.save(savedNotice)
        return ResponseEntity.ok(mapOf("status" to "success", "noticeId" to noticeId.toString()))
    }

    fun findNoticeById(noticeId: Long): Notice {
        val maybeNotice = noticeRepository.findById(noticeId)
        if (maybeNotice.isEmpty) {
            throw Exception("no Notice")
        }
        return maybeNotice.get()
    }
}