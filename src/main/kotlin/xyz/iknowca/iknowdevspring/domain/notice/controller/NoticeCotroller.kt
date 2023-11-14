package xyz.iknowca.iknowdevspring.domain.notice.controller

import lombok.RequiredArgsConstructor
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.iknowca.iknowdevspring.domain.notice.entity.NoticeDto
import xyz.iknowca.iknowdevspring.domain.notice.service.NoticeService

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
class NoticeCotroller(val noticeService: NoticeService) {
    @PostMapping
    fun postNotice(
        @RequestBody requestBody: NoticeDto,
        @RequestHeader("authorization", required = false) authorization: String?
    ): ResponseEntity<Map<String, String>> {
        return noticeService.postNotice(requestBody, authorization)
    }

    @GetMapping("/{noticeId}")
    fun getNotice(@PathVariable("noticeId") noticeId: Long): ResponseEntity<NoticeDto> {
        return noticeService.getNotice(noticeId)
    }

    @GetMapping("/list")
    fun getNoticeList(@RequestParam page: Int, @RequestParam size: Int): ResponseEntity<Page<NoticeDto>> {
        return noticeService.getNoticeList(page, size)
    }

    @DeleteMapping("/{noticeId}")
    fun deleteNotice(
        @PathVariable("noticeId") noticeId: Long,
        @RequestHeader("authorization", required = false) authorization: String?
    ): ResponseEntity<Map<String, String>> {
        return noticeService.deleteNotice(noticeId, authorization)
    }

    @PutMapping("/{noticeId}")
    fun modifyNotice(
        @RequestBody modifiedNotice:NoticeDto,
        @PathVariable("noticeId") noticeId: Long,
        @RequestHeader("authorization", required = false) authorization: String?
    ): ResponseEntity<Map<String, String>> {
        return noticeService.modifyNotice(modifiedNotice, noticeId, authorization)
    }
}