package xyz.iknowca.iknowdevspring.domain.notice.exception

import org.springframework.http.HttpStatus

class NoticeException(error: NoticeError) : RuntimeException() {

    lateinit var noticeError: NoticeError

    enum class NoticeError(val status: HttpStatus, val message:String) {
        NO_CONTENT(HttpStatus.NO_CONTENT, "there are no corresponding notices")
    }
}