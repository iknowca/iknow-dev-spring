package xyz.iknowca.iknowdevspring.domain.notice.service

import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import xyz.iknowca.iknowdevspring.domain.notice.entity.NoticeDto

interface NoticeService {
    fun postNotice(requestBody: NoticeDto, authorization: String?): ResponseEntity<Map<String, String>>
    fun getNotice(noticeId: Long): ResponseEntity<NoticeDto>
    fun getNoticeList(page: Int, size: Int): ResponseEntity<Page<NoticeDto>>
    fun deleteNotice(noticeId: Long, authorization: String?): ResponseEntity<Map<String, String>>
    fun modifyNotice(modifiedNotice: NoticeDto, noticeId: Long, authorization: String?): ResponseEntity<Map<String, String>>
}