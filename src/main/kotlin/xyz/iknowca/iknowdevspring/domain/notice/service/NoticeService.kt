package xyz.iknowca.iknowdevspring.domain.notice.service

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import xyz.iknowca.iknowdevspring.domain.notice.entity.NoticeDto

interface NoticeService {
    fun postNotice(requestBody: NoticeDto, authorization: String?): ResponseEntity<Map<String, String>>
    fun getNotice(noticeId: Long): ResponseEntity<NoticeDto>
}